package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

/**
 * 认养申请请求DTO
 * 
 * 用于学生提交认养申请的请求参数，包含申请人的各项资质信息
 */
public record ApplicationRequest(
                /** 申请认养的猫咪ID，必填字段 */
                @NotBlank String catId,
                /** 住房条件信息，必填字段 */
                @NotBlank String housingInfo,
                /** 家人态度/支持情况，必填字段 */
                @NotBlank String familyAttitude,
                /** 养宠经验描述，必填字段 */
                @NotBlank String petExperience,
                /** 经济能力说明，必填字段 */
                @NotBlank String economicAbility,
                /** 是否同意认养承诺，必须为true才能提交申请 */
                @AssertTrue(message = "必须同意认养承诺") boolean promiseAccepted) {
}