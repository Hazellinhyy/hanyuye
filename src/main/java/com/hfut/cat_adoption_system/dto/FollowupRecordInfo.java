package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

public record FollowupRecordInfo(
        Long id,
        Long taskId,
        String applicationId,
        Long agreementId,
        String catId,
        String adopterId,
        String content,
        String catCondition,
        String environmentDesc,
        String photoUrl,
        Boolean abnormalFlag,
        String abnormalDesc,
        String volunteerComment,
        String submitterId,
        String submitterName,
        String submitterRole,
        LocalDateTime submitTime
) {
}
