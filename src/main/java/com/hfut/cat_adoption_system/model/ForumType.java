package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record ForumType(
        Integer typeId,
        String typeName,
        String description,
        LocalDateTime createdAt
) {
}
