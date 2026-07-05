package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

public record ReviewRequest(
        boolean approved,
        @NotBlank String reviewNote,
        String interviewNote
) {
}
