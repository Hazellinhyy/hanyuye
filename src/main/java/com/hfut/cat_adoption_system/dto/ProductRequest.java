package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String productName,
        @NotBlank String category,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotBlank String imageUrl,
        @NotBlank String description,
        String payUrl,
        @NotNull @Min(0) Integer stock,
        boolean status
) {
}
