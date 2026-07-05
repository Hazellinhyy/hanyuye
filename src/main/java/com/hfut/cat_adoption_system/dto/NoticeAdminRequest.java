package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

public record NoticeAdminRequest(
        @NotBlank String title,
        @NotBlank String content,
        String noticeType,
        String publishStatus,
        Integer sortOrder,
        Boolean sendMessage
) {
}
