package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

public record AgreementInfo(
        Long id,
        String agreementNo,
        String applicationId,
        String catId,
        String catName,
        String adopterId,
        String adopterName,
        String adopterPhone,
        String agreementContent,
        String status,
        LocalDateTime generatedTime,
        LocalDateTime handoverTime,
        String handoverLocation,
        String handoverUserId,
        String handoverUserName,
        Boolean adopterConfirmed,
        Boolean volunteerConfirmed,
        String remark,
        LocalDateTime finalApprovedAt,
        String applicationStatus
) {
}
