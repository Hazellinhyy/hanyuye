package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ApplicationReviewDetail(
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
        LocalDateTime reviewedAt,
        List<ApplicationAuditRecord> audits,
        AgreementInfo agreementInfo,
        List<FollowupTaskInfo> followupTasks
) {
}
