package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record RescueReport(
        String reportId,
        String reporterName,
        String reporterPhone,
        String foundPlace,
        String color,
        String gender,
        String healthDescription,
        boolean urgent,
        String photoUrl,
        String status,
        LocalDateTime reportedAt
) {
}
