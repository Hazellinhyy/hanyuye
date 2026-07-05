package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AgentChatRequest(
        @NotBlank
        @Size(max = 800)
        String message,
        List<AgentChatTurn> history
) {
}
