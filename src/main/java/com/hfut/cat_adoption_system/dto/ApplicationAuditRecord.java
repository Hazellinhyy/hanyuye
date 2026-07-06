package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

/**
 * 认养申请审核记录DTO
 * 
 * 记录认养申请的每一次审核操作，用于追踪审核流程和历史记录
 */
public record ApplicationAuditRecord(
                /** 审核记录ID */
                Long id,
                /** 关联的认养申请ID */
                String applicationId,
                /** 审核人ID */
                String auditorId,
                /** 审核人姓名 */
                String auditorName,
                /** 审核阶段（如：初审、终审等） */
                String auditStage,
                /** 审核结果（通过/拒绝） */
                String auditResult,
                /** 审核意见 */
                String auditComment,
                /** 审核前的状态 */
                String beforeStatus,
                /** 审核后的状态 */
                String afterStatus,
                /** 审核时间 */
                LocalDateTime auditTime) {
}