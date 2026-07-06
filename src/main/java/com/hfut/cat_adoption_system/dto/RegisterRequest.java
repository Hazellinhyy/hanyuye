package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 用户注册请求DTO
 * 
 * 用于接收用户注册的请求参数，通过Jakarta Validation注解实现表单校验，
 * 包含用户名、学号、密码、手机号、身份证号等必要信息。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record RegisterRequest(
                /** 用户姓名（必填） */
                @NotBlank(message = "用户姓名不能为空") String userName,

                /** 学号（必填） */
                @NotBlank(message = "学号不能为空") String schoolNo,

                /** 密码（必填） */
                @NotBlank(message = "密码不能为空") String password,

                /** 手机号码（需符合11位手机号格式） */
                @Pattern(regexp = "^1[3-9]\\d{9}$", message = "必须是有效手机号") String phone,

                /** 身份证号（需符合18位身份证格式） */
                @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "必须是18位身份证号") String idCard,

                /** 所在学院（必填） */
                @NotBlank(message = "所在学院不能为空") String college,

                /** 养宠经验描述 */
                String petExperience,

                /** 用户角色（如普通用户、医院用户、管理员等） */
                Role role) {
}