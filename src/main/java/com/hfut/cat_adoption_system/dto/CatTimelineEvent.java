package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

public record CatTimelineEvent(
        String eventType,
        String title,
        String description,
        LocalDateTime eventTime
) {
}
