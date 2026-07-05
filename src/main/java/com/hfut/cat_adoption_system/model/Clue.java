package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record Clue(
        String id,
        String clueNo,
        String reporterId,
        String reporterName,
        String reporterPhone,
        String foundLocation,
        String foundArea,
        LocalDateTime foundTime,
        String photoUrl,
        String description,
        String urgencyLevel,
        String status,
        String verifyUserId,
        String verifyUserName,
        String verifyResult,
        String verifyComment,
        LocalDateTime verifyTime,
        String createdCatId,
        LocalDateTime createTime,
        LocalDateTime updateTime,
        boolean deleted
) {
}
