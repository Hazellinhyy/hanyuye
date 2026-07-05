package com.hfut.cat_adoption_system.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfut.cat_adoption_system.config.RecognitionModelProperties;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProfessionalImageModelClient {
    private final RecognitionModelProperties properties;
    private final ObjectMapper objectMapper;

    public ProfessionalImageModelClient(RecognitionModelProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public Optional<ModelEmbedding> embed(byte[] imageBytes, String contentType) {
        if (!properties.isEnabled() || properties.getEndpoint() == null || properties.getEndpoint().isBlank()) {
            return Optional.empty();
        }
        try {
            String boundary = "cat-recognition-" + UUID.randomUUID();
            byte[] body = multipartBody(boundary, imageBytes, contentType);
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(properties.getConnectTimeoutMillis()))
                    .build();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(properties.getEndpoint()))
                    .timeout(Duration.ofMillis(properties.getReadTimeoutMillis()))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body));
            if (properties.getApiKey() != null && !properties.getApiKey().isBlank()) {
                requestBuilder.header("Authorization", "Bearer " + properties.getApiKey());
            }
            HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return Optional.empty();
            }
            return parseEmbedding(response.body());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private byte[] multipartBody(String boundary, byte[] imageBytes, String contentType) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(output, "--" + boundary + "\r\n");
        write(output, "Content-Disposition: form-data; name=\"model\"\r\n\r\n");
        write(output, properties.getModelName() + "\r\n");
        write(output, "--" + boundary + "\r\n");
        write(output, "Content-Disposition: form-data; name=\"image\"; filename=\"cat.jpg\"\r\n");
        write(output, "Content-Type: " + safeContentType(contentType) + "\r\n\r\n");
        output.write(imageBytes);
        write(output, "\r\n--" + boundary + "--\r\n");
        return output.toByteArray();
    }

    private void write(ByteArrayOutputStream output, String value) throws Exception {
        output.write(value.getBytes(StandardCharsets.UTF_8));
    }

    private String safeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "image/jpeg";
        }
        return contentType;
    }

    private Optional<ModelEmbedding> parseEmbedding(String body) throws Exception {
        JsonNode root = objectMapper.readTree(body);
        JsonNode vectorNode = firstNonMissing(root.get("embedding"), root.get("vector"));
        if (vectorNode == null && root.has("data") && root.get("data").isArray() && !root.get("data").isEmpty()) {
            JsonNode item = root.get("data").get(0);
            vectorNode = firstNonMissing(item.get("embedding"), item.get("vector"));
        }
        if (vectorNode == null || !vectorNode.isArray() || vectorNode.isEmpty()) {
            return Optional.empty();
        }
        double[] vector = new double[vectorNode.size()];
        for (int i = 0; i < vectorNode.size(); i++) {
            vector[i] = vectorNode.get(i).asDouble();
        }
        String model = root.hasNonNull("model") ? root.get("model").asText() : properties.getModelName();
        return Optional.of(new ModelEmbedding("model-" + model.replace(':', '_'), model.replace(':', '_'), vector));
    }

    private JsonNode firstNonMissing(JsonNode first, JsonNode second) {
        if (first != null && !first.isMissingNode() && !first.isNull()) {
            return first;
        }
        if (second != null && !second.isMissingNode() && !second.isNull()) {
            return second;
        }
        return null;
    }

    public record ModelEmbedding(String version, String modelName, double[] vector) {
    }
}
