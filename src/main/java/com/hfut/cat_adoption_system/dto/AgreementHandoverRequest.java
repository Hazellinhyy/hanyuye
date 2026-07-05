package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AgreementHandoverRequest(
        @NotNull LocalDateTime handoverTime,
        @NotBlank String handoverLocation,
        @NotBlank String handoverUserId,
        boolean adopterConfirmed,
        boolean volunteerConfirmed,
        String remark
) {
}
