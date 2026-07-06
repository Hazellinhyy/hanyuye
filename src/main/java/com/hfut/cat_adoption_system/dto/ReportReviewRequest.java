package com.hfut.cat_adoption_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * 报告审核请求DTO
 * 
 * 用于接收流浪猫发现报告的审核请求参数，包含审核结果、操作员信息和猫咪信息，
 * 通过Jakarta Validation注解实现表单校验。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record ReportReviewRequest(
                /** 是否通过审核 */
                boolean approved,

                /** 审核操作员姓名（必填） */
                @NotBlank(message = "操作员姓名不能为空") String operatorName,

                /** 猫咪信息（需进行嵌套校验） */
                @Valid CatRequest cat) {
}