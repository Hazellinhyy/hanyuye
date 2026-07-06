package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户登录请求DTO
 * 
 * 用于接收用户登录时提交的账号和密码信息，
 * 通过Jakarta Validation注解实现表单校验。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record LoginRequest(
                /** 用户账号（不能为空） */
                @NotBlank(message = "账号不能为空") String account,

                /** 用户密码（不能为空） */
                @NotBlank(message = "密码不能为空") String password) {
}