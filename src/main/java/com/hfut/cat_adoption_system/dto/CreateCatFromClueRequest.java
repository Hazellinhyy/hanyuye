package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.HealthLevel;

public record CreateCatFromClueRequest(
        String name,
        String gender,
        String ageEstimate,
        String coatColor,
        String personality,
        HealthLevel healthStatus,
        Boolean sterilizedStatus,
        Boolean vaccineStatus,
        String extraDescription
) {
}
