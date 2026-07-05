package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record AdoptionApplication(
        String applicationId,
        String userId,
        String catId,
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
