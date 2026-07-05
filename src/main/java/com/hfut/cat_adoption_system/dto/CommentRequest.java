package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequest(
        @NotBlank String sourceType,
        @NotNull Integer sourceId,
        Integer replyToId,
        @NotBlank String content
) {
}
