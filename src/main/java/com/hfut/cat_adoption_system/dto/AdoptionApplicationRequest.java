package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

/**
 * 认养申请请求DTO
 * 
 * 用于学生提交认养申请的请求参数，包含申请人的各项资质信息
 */
public record AdoptionApplicationRequest(
                /** 申请认养的猫咪ID，必填字段 */
                @NotBlank String catId,
                /** 居住条件描述，必填字段（如住房类型、是否允许养宠等） */
                @NotBlank String livingCondition,
                /** 养宠经验描述，必填字段 */
                @NotBlank String petExperience,
                /** 家人支持情况，必填字段 */
                @NotBlank String familySupport,
                /** 经济能力说明，必填字段（能否承担猫咪的日常开销） */
                @NotBlank String costAffordability,
                /** 是否接受后续回访 */
                boolean acceptFollowup,
                /** 是否同意认养承诺，必须为true才能提交申请 */
                @AssertTrue(message = "必须同意认养承诺") boolean commitmentAccepted,
                /** 承诺文本内容，必填字段 */
                @NotBlank String commitmentText,
                /** 额外理由（可选） */
                String extraReason) {
}