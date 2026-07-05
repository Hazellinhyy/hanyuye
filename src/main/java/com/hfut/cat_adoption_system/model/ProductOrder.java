package com.hfut.cat_adoption_system.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductOrder(
        String orderId,
        String userId,
        String userName,
        String productId,
        String productName,
        Integer quantity,
        BigDecimal amount,
        String payUrl,
        String orderStatus,
        LocalDateTime createdAt
) {
}
