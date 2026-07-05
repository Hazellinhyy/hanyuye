package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.VerifyResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClueVerifyRequest(
        @NotNull VerifyResult verifyResult,
        @NotBlank String verifyComment
) {
}
