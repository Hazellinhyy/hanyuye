package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 审核请求DTO
 * 
 * 用于接收审核操作的请求参数，包含审核结果、审核备注和面试备注信息，
 * 通过Jakarta Validation注解实现表单校验。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record ReviewRequest(
                /** 是否通过审核 */
                boolean approved,

                /** 审核备注（必填） */
                @NotBlank(message = "审核备注不能为空") String reviewNote,

                /** 面试备注 */
                String interviewNote) {
}