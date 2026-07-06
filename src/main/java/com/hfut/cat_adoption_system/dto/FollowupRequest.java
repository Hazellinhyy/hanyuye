package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.FollowupResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 回访请求DTO
 * 
 * 用于志愿者或管理员添加认养回访记录的请求参数，记录猫咪状况和领养环境
 */
public record FollowupRequest(
                /** 认养申请ID，必填字段 */
                @NotBlank String applicationId,
                /** 回访方式，必填字段（如：电话、上门、微信等） */
                @NotBlank String method,
                /** 猫咪状况，必填字段 */
                @NotBlank String catCondition,
                /** 环境描述，必填字段（领养人居住环境） */
                @NotBlank String environmentDescription,
                /** 回访结果，必填字段（如：正常、异常、需关注等） */
                @NotNull FollowupResult result,
                /** 回访照片URL（可选） */
                String photoUrl,
                /** 建议意见（可选） */
                String suggestion,
                /** 操作人姓名，必填字段 */
                @NotBlank String operatorName) {
}