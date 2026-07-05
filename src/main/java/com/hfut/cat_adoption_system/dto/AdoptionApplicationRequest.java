package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public record AdoptionApplicationRequest(
        @NotBlank String catId,
        @NotBlank String livingCondition,
        @NotBlank String petExperience,
        @NotBlank String familySupport,
        @NotBlank String costAffordability,
        boolean acceptFollowup,
        @AssertTrue(message = "必须同意认养承诺") boolean commitmentAccepted,
        @NotBlank String commitmentText,
        String extraReason
) {
}
