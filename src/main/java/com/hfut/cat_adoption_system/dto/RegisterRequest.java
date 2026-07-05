package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank String userName,
        @NotBlank String schoolNo,
        @NotBlank String password,
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "必须是有效手机号") String phone,
        @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "必须是18位身份证号") String idCard,
        @NotBlank String college,
        String petExperience,
        Role role
) {
}
