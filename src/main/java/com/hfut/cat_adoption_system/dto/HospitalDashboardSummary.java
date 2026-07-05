package com.hfut.cat_adoption_system.dto;

public record HospitalDashboardSummary(
        long medicalCatCount,
        long observingCatCount,
        long abnormalRecordCount,
        long myRecordCount,
        long totalRecordCount,
        long pendingVaccineCount,
        long pendingSterilizationCount
) {
}
