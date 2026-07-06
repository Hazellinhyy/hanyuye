package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 回访异常标记请求DTO
 * 
 * 用于标记回访任务异常时提交的请求参数
 */
public record FollowupAbnormalRequest(
                /** 异常描述，必填字段 */
                @NotBlank String abnormalDesc,
                /** 志愿者处理意见（可选） */
                String volunteerComment) {
}