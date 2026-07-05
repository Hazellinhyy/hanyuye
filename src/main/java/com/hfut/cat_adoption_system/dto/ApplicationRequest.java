package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public record ApplicationRequest(
        @NotBlank String catId,
        @NotBlank String housingInfo,
        @NotBlank String familyAttitude,
        @NotBlank String petExperience,
        @NotBlank String economicAbility,
        @AssertTrue(message = "必须同意认养承诺") boolean promiseAccepted
) {
}
