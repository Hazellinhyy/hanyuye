package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

public record ProfileUpdateRequest(
        @NotBlank String userName,
        @NotBlank String phone,
        @NotBlank String college,
        String petExperience
) {
}
