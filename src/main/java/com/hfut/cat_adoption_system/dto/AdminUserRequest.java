package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 管理员用户管理请求DTO
 * 
 * 用于管理员创建或编辑用户的请求参数
 */
public record AdminUserRequest(
                /** 用户名，必填字段 */
                @NotBlank String userName,
                /** 学号/工号，必填字段 */
                @NotBlank String schoolNo,
                /** 密码（创建时必填，更新时可空） */
                String password,
                /** 手机号，格式校验：1开头的11位数字 */
                @Pattern(regexp = "^1[3-9]\\d{9}$", message = "必须是有效手机号") String phone,
                /** 身份证号 */
                String idCard,
                /** 学院，必填字段 */
                @NotBlank String college,
                /** 养宠经验描述 */
                String petExperience,
                /** 用户角色，必填字段（如STUDENT、VOLUNTEER、ADMIN等） */
                @NotNull Role role,
                /** 是否启用该用户 */
                Boolean enabled) {
}