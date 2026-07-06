package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认养申请审核详情DTO
 * 
 * 封装认养申请的完整审核信息，包含申请内容、审核评分、审核记录、协议信息和回访任务
 */
public record ApplicationReviewDetail(
                /** 申请ID */
                String applicationId,
                /** 申请人用户ID */
                String userId,
                /** 申请人姓名 */
                String userName,
                /** 申请人手机号 */
                String phone,
                /** 申请认养的猫咪ID */
                String catId,
                /** 猫咪名称 */
                String catName,
                /** 猫咪封面图片URL */
                String coverUrl,
                /** 居住条件描述 */
                String livingCondition,
                /** 养宠经验描述 */
                String petExperience,
                /** 家人支持情况 */
                String familySupport,
                /** 经济能力说明 */
                String costAffordability,
                /** 是否接受后续回访 */
                boolean acceptFollowup,
                /** 承诺文本内容 */
                String commitmentText,
                /** 额外理由说明 */
                String extraReason,
                /** 审核评分 */
                Integer score,
                /** 风险等级 */
                String riskLevel,
                /** 评分理由 */
                String scoreReasons,
                /** 申请状态 */
                ApplicationStatus status,
                /** 审核备注 */
                String reviewNote,
                /** 申请提交时间 */
                LocalDateTime appliedAt,
                /** 审核完成时间 */
                LocalDateTime reviewedAt,
                /** 审核记录列表 */
                List<ApplicationAuditRecord> audits,
                /** 关联的协议信息 */
                AgreementInfo agreementInfo,
                /** 关联的回访任务列表 */
                List<FollowupTaskInfo> followupTasks) {
}