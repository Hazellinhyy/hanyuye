package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record Favorite(
        String userId,
        String catId,
        LocalDateTime createdAt
) {
}
