package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

public record ActionReasonRequest(
        @NotBlank String reason
) {
}
