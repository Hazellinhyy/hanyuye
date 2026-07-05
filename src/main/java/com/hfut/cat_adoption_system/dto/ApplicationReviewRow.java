package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationReviewRow(
        String applicationId,
        String userId,
        String userName,
        String phone,
        String catId,
        String catName,
        String coverUrl,
        String livingCondition,
        String petExperience,
        String familySupport,
        String costAffordability,
        boolean acceptFollowup,
        String commitmentText,
        String extraReason,
        Integer score,
        String riskLevel,
        String scoreReasons,
        ApplicationStatus status,
        String reviewNote,
        LocalDateTime appliedAt,
        LocalDateTime reviewedAt
) {
}
