package com.hfut.cat_adoption_system.dto;

public record AgentChatResponse(
        String answer,
        String provider,
        String model,
        boolean fallback
) {
}
