package com.hfut.cat_adoption_system.dto;

public record AdminDashboardSummary(
        long catCount,
        long adoptableCount,
        long observingCount,
        long medicalCount,
        long suspendedCount,
        long pendingClueCount,
        long createdCatClueCount,
        long pendingInitialApplicationCount,
        long pendingFinalApplicationCount,
        long pendingHandoverApplicationCount,
        long handedOverApplicationCount,
        long pendingFollowupTaskCount,
        long completedFollowupTaskCount,
        long overdueFollowupTaskCount,
        long abnormalFollowupTaskCount,
        long pendingWarningCount,
        long handledWarningCount,
        double followupCompletionRate,
        long followingCatCount,
        long highRiskApplicationCount,
        long unreadMessageCount
) {
}
