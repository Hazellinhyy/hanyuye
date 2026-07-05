package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record ForumPost(
        Integer postId,
        String userId,
        String userName,
        String typeName,
        String title,
        String content,
        String coverUrl,
        Integer hits,
        Integer praiseCount,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
