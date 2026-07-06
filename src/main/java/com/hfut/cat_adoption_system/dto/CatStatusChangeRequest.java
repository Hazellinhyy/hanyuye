package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.CatStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 猫咪状态变更请求DTO
 * 
 * 用于变更猫咪状态的请求参数，需提供目标状态和变更原因
 */
public record CatStatusChangeRequest(
                /** 目标状态，必填字段 */
                @NotNull CatStatus targetStatus,
                /** 变更原因，必填字段 */
                @NotBlank String reason) {
}