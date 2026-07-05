package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RescueReportRequest(
        @NotBlank String reporterName,
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "必须是有效手机号") String reporterPhone,
        @NotBlank String foundPlace,
        String color,
        String gender,
        @NotBlank String healthDescription,
        boolean urgent,
        @NotBlank String photoUrl
) {
}
