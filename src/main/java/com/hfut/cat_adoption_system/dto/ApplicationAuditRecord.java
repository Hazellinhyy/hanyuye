package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

public record ApplicationAuditRecord(
        Long id,
        String applicationId,
        String auditorId,
        String auditorName,
        String auditStage,
        String auditResult,
        String auditComment,
        String beforeStatus,
        String afterStatus,
        LocalDateTime auditTime
) {
}
