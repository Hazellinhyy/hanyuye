package com.hfut.cat_adoption_system.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 回访任务信息DTO
 * 
 * 封装回访任务的完整信息，包含任务基本信息、关联的猫咪和领养人信息、任务状态及回访记录详情
 */
public record FollowupTaskInfo(
                /** 任务ID */
                Long id,
                /** 关联的认养申请ID */
                String applicationId,
                /** 关联的协议ID */
                Long agreementId,
                /** 协议编号 */
                String agreementNo,
                /** 猫咪ID */
                String catId,
                /** 猫咪名称 */
                String catName,
                /** 猫咪封面图片URL */
                String catCoverUrl,
                /** 领养人ID */
                String adopterId,
                /** 领养人姓名 */
                String adopterName,
                /** 领养人手机号 */
                String adopterPhone,
                /** 计划回访日期 */
                LocalDate planDate,
                /** 实际回访日期 */
                LocalDate actualDate,
                /** 任务类型 */
                String taskType,
                /** 任务状态 */
                String status,
                /** 是否被标记为异常 */
                Boolean abnormalFlag,
                /** 处理人ID */
                String handlerId,
                /** 处理人姓名 */
                String handlerName,
                /** 任务创建时间 */
                LocalDateTime createTime,
                /** 是否允许提交反馈 */
                boolean feedbackEnabled,
                /** 关联的回访记录ID */
                Long recordId,
                /** 回访记录内容 */
                String recordContent,
                /** 猫咪状况描述 */
                String catCondition,
                /** 居住环境描述 */
                String environmentDesc,
                /** 回访照片URL */
                String photoUrl,
                /** 回访记录是否标记异常 */
                Boolean recordAbnormalFlag,
                /** 异常描述 */
                String abnormalDesc,
                /** 志愿者处理意见 */
                String volunteerComment,
                /** 回访记录提交时间 */
                LocalDateTime submitTime,
                /** 告警次数 */
                Integer warningCount) {
}