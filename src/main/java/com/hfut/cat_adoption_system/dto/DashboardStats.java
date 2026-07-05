package com.hfut.cat_adoption_system.dto;

public record DashboardStats(
        long catCount,
        long adoptableCount,
        long adoptedCount,
        long pendingApplicationCount,
        long medicalCount,
        long followupCount,
        double sterilizationRate,
        double vaccinationRate,
        double adoptionSuccessRate
) {
}
