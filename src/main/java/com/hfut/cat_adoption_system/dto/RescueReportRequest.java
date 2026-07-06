package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 流浪猫救助报告请求DTO
 * 
 * 用于接收用户提交的流浪猫救助报告请求参数，包含发现人信息、猫咪位置、
 * 猫咪特征、健康状况等必要信息，通过Jakarta Validation注解实现表单校验。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record RescueReportRequest(
                /** 报告人姓名（必填） */
                @NotBlank(message = "报告人姓名不能为空") String reporterName,

                /** 报告人手机号码（需符合11位手机号格式） */
                @Pattern(regexp = "^1[3-9]\\d{9}$", message = "必须是有效手机号") String reporterPhone,

                /** 发现地点（必填） */
                @NotBlank(message = "发现地点不能为空") String foundPlace,

                /** 猫咪毛色 */
                String color,

                /** 猫咪性别 */
                String gender,

                /** 健康状况描述（必填） */
                @NotBlank(message = "健康状况描述不能为空") String healthDescription,

                /** 是否紧急情况 */
                boolean urgent,

                /** 猫咪照片URL（必填） */
                @NotBlank(message = "猫咪照片不能为空") String photoUrl) {
}