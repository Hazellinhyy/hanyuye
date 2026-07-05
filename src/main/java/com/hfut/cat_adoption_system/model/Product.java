package com.hfut.cat_adoption_system.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Product(
        String productId,
        String productName,
        String category,
        BigDecimal price,
        String imageUrl,
        String description,
        String payUrl,
        Integer stock,
        boolean status,
        LocalDateTime createdAt
) {
}
