package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 认养申请审核请求DTO
 * 
 * 用于提交认养申请的审核结果
 */
public record ApplicationAuditRequest(
                /** 审核结果，必填字段，只能是 APPROVED（通过）或 REJECTED（拒绝） */
                @Pattern(regexp = "APPROVED|REJECTED", message = "必须是 APPROVED 或 REJECTED") String auditResult,
                /** 审核意见，必填字段（审核理由说明） */
                @NotBlank String auditComment) {
}