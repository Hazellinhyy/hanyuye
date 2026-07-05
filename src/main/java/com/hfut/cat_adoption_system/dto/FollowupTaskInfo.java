package com.hfut.cat_adoption_system.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FollowupTaskInfo(
        Long id,
        String applicationId,
        Long agreementId,
        String agreementNo,
        String catId,
        String catName,
        String catCoverUrl,
        String adopterId,
        String adopterName,
        String adopterPhone,
        LocalDate planDate,
        LocalDate actualDate,
        String taskType,
        String status,
        Boolean abnormalFlag,
        String handlerId,
        String handlerName,
        LocalDateTime createTime,
        boolean feedbackEnabled,
        Long recordId,
        String recordContent,
        String catCondition,
        String environmentDesc,
        String photoUrl,
        Boolean recordAbnormalFlag,
        String abnormalDesc,
        String volunteerComment,
        LocalDateTime submitTime,
        Integer warningCount
) {
}
