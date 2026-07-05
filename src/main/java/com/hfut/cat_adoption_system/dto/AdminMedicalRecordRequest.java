package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdminMedicalRecordRequest(
        @NotBlank String recordType,
        LocalDate recordDate,
        @NotBlank String description,
        @NotBlank String healthResult,
        String vaccineStatus,
        String sterilizedStatus,
        BigDecimal cost,
        String attachmentUrl,
        boolean abnormalFlag
) {
}
