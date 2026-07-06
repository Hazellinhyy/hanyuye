package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 捐赠请求DTO
 * 
 * 用于创建捐赠记录的请求参数
 */
public record DonationRequest(
                /** 捐赠金额，必填字段，最小金额0.01元 */
                @NotNull @DecimalMin("0.01") BigDecimal amount,
                /** 捐赠留言（可选） */
                String donorMessage) {
}