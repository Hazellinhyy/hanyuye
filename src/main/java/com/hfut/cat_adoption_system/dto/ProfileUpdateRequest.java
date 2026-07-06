package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户资料更新请求DTO
 * 
 * 用于接收用户更新个人资料的请求参数，通过Jakarta Validation注解实现表单校验。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record ProfileUpdateRequest(
                /** 用户姓名（必填） */
                @NotBlank(message = "用户姓名不能为空") String userName,

                /** 联系电话（必填） */
                @NotBlank(message = "联系电话不能为空") String phone,

                /** 所在学院（必填） */
                @NotBlank(message = "所在学院不能为空") String college,

                /** 养宠经验描述 */
                String petExperience) {
}