package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.CatStatus;
import com.hfut.cat_adoption_system.model.HealthLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CatRequest(
        String catName,
        @NotBlank String foundPlace,
        LocalDate foundDate,
        String gender,
        String color,
        String ageEstimate,
        String personality,
        @NotNull HealthLevel healthLevel,
        boolean sterilized,
        boolean vaccinated,
        @NotNull CatStatus status,
        @NotBlank
        String coverUrl,
        List<String> tags,
        String description
) {
}
