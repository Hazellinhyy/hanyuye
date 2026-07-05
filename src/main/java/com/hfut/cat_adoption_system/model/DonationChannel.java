package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record DonationChannel(
        String channelId,
        String channelName,
        String qrUrl,
        String description,
        boolean enabled,
        LocalDateTime updatedAt
) {
}
