package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 猫咪照片请求DTO
 * 
 * 用于创建或更新猫咪照片信息的请求参数
 */
public record CatPhotoRequest(
                /** 猫咪ID，必填字段 */
                @NotBlank String catId,
                /** 照片URL，必填字段 */
                @NotBlank String photoUrl,
                /** 拍摄角度代码，必填字段（如正面、侧面等） */
                @NotBlank String angleCode,
                /** 照片场景描述 */
                String photoScene,
                /** 是否设为封面照片 */
                boolean cover,
                /** 识别权重，必填字段（用于AI识别匹配） */
                @NotNull BigDecimal recognitionWeight,
                /** 特征备注（描述照片中的特征信息） */
                String featureNote) {
}