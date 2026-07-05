package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateRequest(
        @NotBlank String oldPassword,
        @NotBlank String newPassword
) {
}
