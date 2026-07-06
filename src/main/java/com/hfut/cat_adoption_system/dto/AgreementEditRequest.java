package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 协议编辑请求DTO
 * 
 * 用于编辑认养协议内容的请求参数
 */
public record AgreementEditRequest(
                /** 协议内容，必填字段 */
                @NotBlank String agreementContent,
                /** 备注说明（可选） */
                String remark) {
}