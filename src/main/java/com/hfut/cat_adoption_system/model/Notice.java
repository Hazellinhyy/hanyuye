package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record Notice(
        String noticeId,
        String title,
        String content,
        String publisher,
        boolean pinned,
        boolean enabled,
        LocalDateTime publishedAt
) {
}
