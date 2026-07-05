package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record Slide(
        Integer slideId,
        String title,
        String content,
        String imageUrl,
        String linkUrl,
        boolean enabled,
        Integer sortOrder,
        LocalDateTime createdAt
) {
}
