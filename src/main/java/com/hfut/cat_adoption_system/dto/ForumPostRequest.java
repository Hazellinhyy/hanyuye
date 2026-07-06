package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 论坛帖子请求DTO
 * 
 * 用于创建或更新论坛帖子的请求参数
 */
public record ForumPostRequest(
                /** 帖子类型名称 */
                String typeName,
                /** 帖子标题，必填字段 */
                @NotBlank String title,
                /** 帖子内容，必填字段 */
                @NotBlank String content,
                /** 封面图片URL */
                String coverUrl) {
}