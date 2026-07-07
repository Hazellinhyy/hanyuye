package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 忘记密码请求DTO。
 *
 * 用户未登录时通过账号、手机号和身份证号校验身份，校验通过后重置密码。
 */
public record ForgotPasswordRequest(
                /** 登录账号、学号、手机号或用户编号 */
                @NotBlank(message = "账号不能为空") String account,

                /** 注册时填写的手机号 */
                @NotBlank(message = "手机号不能为空") String phone,

                /** 注册时填写的身份证号 */
                @NotBlank(message = "身份证号不能为空") String idCard,

                /** 新密码 */
                @NotBlank(message = "新密码不能为空") String newPassword) {
}
