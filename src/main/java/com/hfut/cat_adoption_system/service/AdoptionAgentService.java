package com.hfut.cat_adoption_system.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfut.cat_adoption_system.dto.AgentChatRequest;
import com.hfut.cat_adoption_system.dto.AgentChatResponse;
import com.hfut.cat_adoption_system.dto.AgentChatTurn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdoptionAgentService {
    private static final String GENERAL_SYSTEM_PROMPT = """
            You are a general-purpose helpful assistant.
            Answer naturally and clearly. If the user writes in Chinese, answer in Chinese.
            Answer the user's actual question without limiting yourself to cat adoption topics.
            Do not reveal private chain-of-thought.
            Do not use Markdown formatting. Do not use **bold**, code fences, or bullet symbols.
            If asked who you are, say you are a local general AI assistant for this system.
            You can help with learning, writing, planning, coding, daily questions, campus cat adoption, and system usage.
            For medical, legal, financial, or safety-sensitive questions, provide general information and recommend professional help when needed.
            Do not invent private data, backend approval results, or real-time facts that are not provided.
            """;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final boolean localEnabled;
    private final String localEndpoint;
    private final String localModel;

    public AdoptionAgentService(ObjectMapper objectMapper,
                                @Value("${agent.local.enabled:true}") boolean localEnabled,
                                @Value("${agent.local.endpoint:http://localhost:9100/v1/chat/completions}") String localEndpoint,
                                @Value("${agent.local.model:gemma-4-E2B-it-UD-IQ2_M}") String localModel) {
        this.objectMapper = objectMapper;
        this.localEnabled = localEnabled;
        this.localEndpoint = localEndpoint;
        this.localModel = localModel;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    public AgentChatResponse chat(AgentChatRequest request) {
        if (localEnabled) {
            try {
                return chatCompletion(request, localEndpoint, localModel, "", "local-gemma", false);
            } catch (Exception error) {
                System.err.println("Local agent failed: " + error.getMessage());
            }
        }
        return new AgentChatResponse(localFallback(request.message()), "local", "rule-fallback", true);
    }

    public void streamChat(AgentChatRequest request, OutputStream outputStream) throws Exception {
        if (localEnabled) {
            try {
                streamCompletion(request, outputStream, localEndpoint, localModel, "");
                return;
            } catch (Exception error) {
                System.err.println("Local stream agent failed: " + error.getMessage());
            }
        }
        outputStream.write(localFallback(request.message()).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private void streamCompletion(AgentChatRequest request,
                                  OutputStream outputStream,
                                  String endpoint,
                                  String model,
                                  String apiKey) throws Exception {
        Map<String, Object> body = completionBody(request, model);
        body.put("stream", true);
        byte[] jsonBody = objectMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofMinutes(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(jsonBody));
        if (apiKey != null && !apiKey.isBlank()) {
            builder.header("Authorization", "Bearer " + apiKey);
        }

        HttpResponse<java.io.InputStream> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Agent stream provider returned " + response.statusCode());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("data:")) {
                    continue;
                }
                String data = line.substring(5).trim();
                if (data.isBlank() || "[DONE]".equals(data)) {
                    continue;
                }
                JsonNode root = objectMapper.readTree(data);
                String chunk = root.path("choices").path(0).path("delta").path("content").asText("");
                if (!chunk.isEmpty()) {
                    outputStream.write(stripMarkdown(chunk).getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                }
            }
        }
    }

    private AgentChatResponse chatCompletion(AgentChatRequest request,
                                             String endpoint,
                                             String model,
                                             String apiKey,
                                             String provider,
                                             boolean fallback) throws Exception {
        Map<String, Object> body = completionBody(request, model);
        byte[] jsonBody = objectMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofMinutes(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(jsonBody));
        if (apiKey != null && !apiKey.isBlank()) {
            builder.header("Authorization", "Bearer " + apiKey);
        }

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Agent provider returned " + response.statusCode() + ": " + response.body());
        }
        JsonNode root = objectMapper.readTree(response.body());
        String answer = root.path("choices").path(0).path("message").path("content").asText("");
        if (answer.isBlank()) {
            throw new IllegalStateException("Agent provider returned empty answer");
        }
        return new AgentChatResponse(cleanAnswer(answer), provider, model, fallback);
    }

    private Map<String, Object> completionBody(AgentChatRequest request, String model) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", GENERAL_SYSTEM_PROMPT));
        if (request.history() != null) {
            request.history().stream()
                    .filter(turn -> turn != null && turn.content() != null && !turn.content().isBlank())
                    .limit(8)
                    .forEach(turn -> messages.add(Map.of(
                            "role", safeRole(turn),
                            "content", turn.content().trim()
                    )));
        }
        messages.add(Map.of("role", "user", "content", request.message().trim()));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("temperature", 0.55);
        body.put("max_tokens", 4096);
        return body;
    }

    private String safeRole(AgentChatTurn turn) {
        return "assistant".equals(turn.role()) ? "assistant" : "user";
    }

    private String cleanAnswer(String answer) {
        String value = answer == null ? "" : answer.trim();
        String lowered = value.toLowerCase();
        if (lowered.contains("trained by google") || lowered.contains("created by google")
                || lowered.contains("created by alibaba") || lowered.contains("anthropic")
                || lowered.contains("openai")) {
            value = "我是本系统的本地通用智能体，可以回答学习、写作、生活、系统使用和校园猫认养等问题。你可以直接告诉我想问什么。";
        }
        return stripMarkdown(value).trim();
    }

    private String stripMarkdown(String value) {
        return value
                .replace("**", "")
                .replace("```", "")
                .replace("`", "")
                .replaceAll("(?m)^\\s*[-*]\\s+", "");
    }

    private String localFallback(String message) {
        String text = message == null ? "" : message.trim();
        if (text.isBlank()) {
            return "本地通用智能体还没有启动。请稍后再试，或启动 model-service/llama.cpp/llama-server.exe。";
        }
        return "本地通用智能体暂时不可用。我已收到你的问题：" + text + "。请稍后再试，或检查本地 Gemma 模型服务是否启动。";
    }
}
