package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 告警处理请求DTO
 * 
 * 用于接收告警处理的请求参数，包含目标状态和处理备注信息，
 * 通过Jakarta Validation注解实现表单校验。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record WarningHandleRequest(
                /** 目标状态（必填） */
                @NotBlank(message = "目标状态不能为空") String targetStatus,

                /** 处理备注（必填） */
                @NotBlank(message = "处理备注不能为空") String handleComment) {
}