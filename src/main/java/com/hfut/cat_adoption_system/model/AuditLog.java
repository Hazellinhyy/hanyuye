package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record AuditLog(
        String logId,
        String operator,
        String action,
        String targetType,
        String targetId,
        String detail,
        LocalDateTime createdAt
) {
}
