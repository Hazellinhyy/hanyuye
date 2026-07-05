package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ApplicationAuditRequest(
        @Pattern(regexp = "APPROVED|REJECTED", message = "必须是 APPROVED 或 REJECTED") String auditResult,
        @NotBlank String auditComment
) {
}
