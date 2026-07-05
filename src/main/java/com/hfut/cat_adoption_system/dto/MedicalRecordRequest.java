package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.HealthLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MedicalRecordRequest(
        @NotBlank String catId,
        LocalDate checkDate,
        @NotBlank String hospital,
        @NotNull HealthLevel healthLevel,
        boolean vaccinated,
        boolean sterilized,
        String treatment,
        String doctorNote
) {
}
