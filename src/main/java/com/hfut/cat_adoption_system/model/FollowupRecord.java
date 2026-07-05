package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record FollowupRecord(
        String followupId,
        String applicationId,
        LocalDateTime followupTime,
        String method,
        String catCondition,
        String environmentDescription,
        FollowupResult result,
        String photoUrl,
        String suggestion,
        String operatorName
) {
}
