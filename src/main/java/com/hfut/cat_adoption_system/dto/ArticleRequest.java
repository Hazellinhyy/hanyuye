package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

public record ArticleRequest(
        @NotBlank String title,
        String typeName,
        String coverUrl,
        String summary,
        @NotBlank String content,
        String source,
        String sourceUrl,
        String tags,
        boolean published,
        boolean pinned
) {
}
