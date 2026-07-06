package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 回访记录提交请求DTO
 * 
 * 用于提交认养回访记录的请求参数，包含猫咪状况、居住环境等回访信息
 */
public record FollowupRecordSubmitRequest(
                /** 回访内容，必填字段 */
                @NotBlank String content,
                /** 猫咪状况，必填字段 */
                @NotBlank String catCondition,
                /** 居住环境描述，必填字段 */
                @NotBlank String environmentDesc,
                /** 回访照片URL（可选） */
                String photoUrl,
                /** 是否异常 */
                boolean abnormalFlag,
                /** 异常描述（异常时填写） */
                String abnormalDesc) {
}