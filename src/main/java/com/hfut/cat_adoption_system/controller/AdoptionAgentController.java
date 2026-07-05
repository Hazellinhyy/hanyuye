package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.PublicApi;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.AgentChatRequest;
import com.hfut.cat_adoption_system.dto.AgentChatResponse;
import com.hfut.cat_adoption_system.service.AdoptionAgentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/agent")
public class AdoptionAgentController {
    private final AdoptionAgentService service;

    public AdoptionAgentController(AdoptionAgentService service) {
        this.service = service;
    }

    @PostMapping("/adoption-chat")
    @PublicApi
    public ApiResponse<AgentChatResponse> adoptionChat(@Valid @RequestBody AgentChatRequest request) {
        return ApiResponse.ok(service.chat(request));
    }

    @PostMapping(value = "/adoption-chat-stream", produces = "text/plain;charset=UTF-8")
    @PublicApi
    public StreamingResponseBody adoptionChatStream(@Valid @RequestBody AgentChatRequest request) {
        return outputStream -> {
            try {
                service.streamChat(request, outputStream);
            } catch (Exception error) {
                outputStream.flush();
            }
        };
    }
}
