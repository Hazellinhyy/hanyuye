package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

public record WarningInfo(
        Long id,
        String warningType,
        String warningLevel,
        String bizType,
        String bizId,
        String catId,
        String catName,
        String userId,
        String userName,
        String applicationId,
        Long taskId,
        String title,
        String content,
        String status,
        String handlerId,
        String handlerName,
        String handleComment,
        LocalDateTime handleTime,
        LocalDateTime createTime
) {
}
