package com.hfut.cat_adoption_system.dto;

public record ApplicationScore(
        int score,
        String riskLevel,
        String reasons
) {
}
