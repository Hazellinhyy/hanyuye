package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record Comment(
        Integer commentId,
        String userId,
        String nickname,
        String sourceType,
        Integer sourceId,
        Integer replyToId,
        String content,
        LocalDateTime createdAt
) {
}
