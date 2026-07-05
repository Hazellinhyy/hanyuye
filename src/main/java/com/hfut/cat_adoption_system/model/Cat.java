package com.hfut.cat_adoption_system.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record Cat(
        String catId,
        String catName,
        String foundPlace,
        LocalDate foundDate,
        String gender,
        String color,
        String ageEstimate,
        String personality,
        HealthLevel healthLevel,
        boolean sterilized,
        boolean vaccinated,
        CatStatus status,
        String coverUrl,
        List<String> tags,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
