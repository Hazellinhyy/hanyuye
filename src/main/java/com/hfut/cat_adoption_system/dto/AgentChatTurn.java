package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.Size;

public record AgentChatTurn(
        String role,
        @Size(max = 1000)
        String content
) {
}
