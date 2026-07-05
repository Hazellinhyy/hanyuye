package com.hfut.cat_adoption_system.dto;

public record FollowupRefreshResult(
        int updatedTaskCount,
        int generatedWarningCount
) {
}
