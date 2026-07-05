package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationDetail(
        String applicationId,
        String userId,
        String userName,
        String phone,
        String catId,
        String catName,
        String coverUrl,
        String housingInfo,
        String familyAttitude,
        String petExperience,
        String economicAbility,
        boolean promiseAccepted,
        ApplicationStatus status,
        String reviewNote,
        String interviewNote,
        String agreementNo,
        LocalDateTime appliedAt,
        LocalDateTime reviewedAt,
        LocalDateTime handedOverAt
) {
}
