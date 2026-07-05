package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DonationRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        String donorMessage
) {
}
