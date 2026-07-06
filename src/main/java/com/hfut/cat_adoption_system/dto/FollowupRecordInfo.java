package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

/**
 * 回访记录信息DTO
 * 
 * 封装认养回访记录的完整信息
 */
public record FollowupRecordInfo(
                /** 回访记录ID */
                Long id,
                /** 关联的回访任务ID */
                Long taskId,
                /** 关联的认养申请ID */
                String applicationId,
                /** 关联的协议ID */
                Long agreementId,
                /** 猫咪ID */
                String catId,
                /** 领养人ID */
                String adopterId,
                /** 回访内容 */
                String content,
                /** 猫咪状况 */
                String catCondition,
                /** 居住环境描述 */
                String environmentDesc,
                /** 回访照片URL */
                String photoUrl,
                /** 是否异常 */
                Boolean abnormalFlag,
                /** 异常描述 */
                String abnormalDesc,
                /** 志愿者处理意见 */
                String volunteerComment,
                /** 提交人ID */
                String submitterId,
                /** 提交人姓名 */
                String submitterName,
                /** 提交人角色 */
                String submitterRole,
                /** 提交时间 */
                LocalDateTime submitTime) {
}