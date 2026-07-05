package com.hfut.cat_adoption_system.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DonationRecord(
        String donationId,
        String userId,
        String userName,
        BigDecimal amount,
        String channelId,
        String donorMessage,
        String donateStatus,
        LocalDateTime createdAt
) {
}
