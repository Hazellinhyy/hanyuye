package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

public record SystemMessageInfo(
        Long id,
        String receiverId,
        String receiverName,
        String title,
        String content,
        String bizType,
        String bizId,
        String readStatus,
        LocalDateTime createTime
) {
}
