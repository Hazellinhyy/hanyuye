package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 认养协议交接请求DTO
 * 
 * 用于记录认养协议的线下交接信息，完成猫咪的实际交付
 */
public record AgreementHandoverRequest(
                /** 交接时间，必填字段 */
                @NotNull LocalDateTime handoverTime,
                /** 交接地点，必填字段 */
                @NotBlank String handoverLocation,
                /** 执行交接的志愿者ID，必填字段 */
                @NotBlank String handoverUserId,
                /** 领养人是否确认交接 */
                boolean adopterConfirmed,
                /** 志愿者是否确认交接 */
                boolean volunteerConfirmed,
                /** 备注说明（可选） */
                String remark) {
}