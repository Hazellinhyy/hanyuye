package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.ApplicationStatus;

import java.time.LocalDateTime;

/**
 * 认养申请详情DTO
 * 
 * 封装认养申请的完整详细信息，包含申请人信息、猫咪信息、申请内容和流程状态
 */
public record ApplicationDetail(
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
                /** 住房条件信息 */
                String housingInfo,
                /** 家人态度/支持情况 */
                String familyAttitude,
                /** 养宠经验描述 */
                String petExperience,
                /** 经济能力说明 */
                String economicAbility,
                /** 是否接受认养承诺 */
                boolean promiseAccepted,
                /** 申请状态 */
                ApplicationStatus status,
                /** 审核备注 */
                String reviewNote,
                /** 面试备注 */
                String interviewNote,
                /** 关联的协议编号 */
                String agreementNo,
                /** 申请提交时间 */
                LocalDateTime appliedAt,
                /** 审核完成时间 */
                LocalDateTime reviewedAt,
                /** 交接完成时间 */
                LocalDateTime handedOverAt) {
}