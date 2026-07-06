package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 密码更新请求DTO
 * 
 * 用于接收用户修改密码的请求参数，通过Jakarta Validation注解实现表单校验。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record PasswordUpdateRequest(
                /** 原密码（必填） */
                @NotBlank(message = "原密码不能为空") String oldPassword,

                /** 新密码（必填） */
                @NotBlank(message = "新密码不能为空") String newPassword) {
}