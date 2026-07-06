package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

/**
 * 告警信息DTO
 * 
 * 用于封装系统告警的详细信息，包含告警类型、级别、关联业务信息、处理状态等，
 * 支持管理员查看和处理系统告警。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record WarningInfo(
                /** 告警ID */
                Long id,

                /** 告警类型（如疫苗到期、绝育提醒、健康异常等） */
                String warningType,

                /** 告警级别（如低、中、高、紧急） */
                String warningLevel,

                /** 业务类型 */
                String bizType,

                /** 业务ID */
                String bizId,

                /** 猫咪ID */
                String catId,

                /** 猫咪名称 */
                String catName,

                /** 用户ID */
                String userId,

                /** 用户姓名 */
                String userName,

                /** 申请ID */
                String applicationId,

                /** 任务ID */
                Long taskId,

                /** 告警标题 */
                String title,

                /** 告警内容 */
                String content,

                /** 告警状态（如待处理、已处理、已忽略等） */
                String status,

                /** 处理人ID */
                String handlerId,

                /** 处理人姓名 */
                String handlerName,

                /** 处理备注 */
                String handleComment,

                /** 处理时间 */
                LocalDateTime handleTime,

                /** 创建时间 */
                LocalDateTime createTime) {
}