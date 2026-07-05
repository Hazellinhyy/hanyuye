package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record GenericCollect(
        String userId,
        String sourceType,
        Integer sourceId,
        String title,
        String imageUrl,
        LocalDateTime createdAt
) {
}
