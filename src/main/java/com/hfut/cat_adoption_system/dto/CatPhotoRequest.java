package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CatPhotoRequest(
        @NotBlank String catId,
        @NotBlank String photoUrl,
        @NotBlank String angleCode,
        String photoScene,
        boolean cover,
        @NotNull BigDecimal recognitionWeight,
        String featureNote
) {
}
