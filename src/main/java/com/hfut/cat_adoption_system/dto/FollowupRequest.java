package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.FollowupResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FollowupRequest(
        @NotBlank String applicationId,
        @NotBlank String method,
        @NotBlank String catCondition,
        @NotBlank String environmentDescription,
        @NotNull FollowupResult result,
        String photoUrl,
        String suggestion,
        @NotBlank String operatorName
) {
}
