package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record ArticleType(
        Integer typeId,
        String typeName,
        String description,
        Integer sortOrder,
        LocalDateTime createdAt
) {
}
