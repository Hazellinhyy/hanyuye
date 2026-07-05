package com.hfut.cat_adoption_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record ReportReviewRequest(
        boolean approved,
        @NotBlank String operatorName,
        @Valid CatRequest cat
) {
}
