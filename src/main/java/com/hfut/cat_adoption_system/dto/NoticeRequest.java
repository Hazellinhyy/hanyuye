package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

public record NoticeRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotBlank String publisher,
        boolean pinned,
        boolean enabled
) {
}
