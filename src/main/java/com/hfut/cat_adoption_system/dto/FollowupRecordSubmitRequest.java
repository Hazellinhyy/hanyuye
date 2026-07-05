package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

public record FollowupRecordSubmitRequest(
        @NotBlank String content,
        @NotBlank String catCondition,
        @NotBlank String environmentDesc,
        String photoUrl,
        boolean abnormalFlag,
        String abnormalDesc
) {
}
