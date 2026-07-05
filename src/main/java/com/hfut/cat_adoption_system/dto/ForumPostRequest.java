package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ForumPostRequest(
        String typeName,
        @NotBlank String title,
        @NotBlank String content,
        String coverUrl
) {
}
