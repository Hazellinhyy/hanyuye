package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record Article(
        Integer articleId,
        String title,
        String typeName,
        String coverUrl,
        String summary,
        String content,
        String source,
        String sourceUrl,
        String tags,
        Integer hits,
        Integer praiseCount,
        boolean published,
        boolean pinned,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
