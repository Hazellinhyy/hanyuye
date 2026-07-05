package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AdminUserRequest(
        @NotBlank String userName,
        @NotBlank String schoolNo,
        String password,
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "必须是有效手机号") String phone,
        String idCard,
        @NotBlank String college,
        String petExperience,
        @NotNull Role role,
        Boolean enabled
) {
}
