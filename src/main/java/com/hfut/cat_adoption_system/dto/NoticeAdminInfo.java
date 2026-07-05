package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

public record NoticeAdminInfo(
        String id,
        String title,
        String content,
        String noticeType,
        String publishStatus,
        String publisherId,
        String publisherName,
        LocalDateTime publishTime,
        Integer sortOrder,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
