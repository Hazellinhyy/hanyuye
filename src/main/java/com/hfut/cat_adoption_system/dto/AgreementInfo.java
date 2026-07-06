package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

/**
 * 认养协议信息DTO
 * 
 * 封装认养协议的完整信息，包含协议基本信息、关联的申请和猫咪信息、交接记录等
 */
public record AgreementInfo(
                /** 协议ID */
                Long id,
                /** 协议编号 */
                String agreementNo,
                /** 关联的认养申请ID */
                String applicationId,
                /** 关联的猫咪ID */
                String catId,
                /** 猫咪名称 */
                String catName,
                /** 领养人ID */
                String adopterId,
                /** 领养人姓名 */
                String adopterName,
                /** 领养人手机号 */
                String adopterPhone,
                /** 协议内容 */
                String agreementContent,
                /** 协议状态 */
                String status,
                /** 协议生成时间 */
                LocalDateTime generatedTime,
                /** 交接时间 */
                LocalDateTime handoverTime,
                /** 交接地点 */
                String handoverLocation,
                /** 执行交接的志愿者ID */
                String handoverUserId,
                /** 执行交接的志愿者姓名 */
                String handoverUserName,
                /** 领养人是否确认交接 */
                Boolean adopterConfirmed,
                /** 志愿者是否确认交接 */
                Boolean volunteerConfirmed,
                /** 备注说明 */
                String remark,
                /** 终审通过时间 */
                LocalDateTime finalApprovedAt,
                /** 关联的申请状态 */
                String applicationStatus) {
}