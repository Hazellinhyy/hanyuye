package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * 猫咪线索提交请求DTO
 * 
 * 用于学生提交流浪猫线索的请求参数
 */
public record ClueSubmitRequest(
                /** 发现地点，必填字段 */
                @NotBlank String foundLocation,
                /** 发现区域 */
                String foundArea,
                /** 发现时间 */
                LocalDateTime foundTime,
                /** 照片URL，必填字段 */
                @NotBlank String photoUrl,
                /** 描述信息，必填字段（猫咪状态、特征等） */
                @NotBlank String description,
                /** 紧急程度，必填字段（如：紧急、一般等） */
                @NotBlank String urgencyLevel) {
}