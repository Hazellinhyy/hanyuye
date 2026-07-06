package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 文章请求DTO
 * 
 * 用于创建或更新文章的请求参数
 */
public record ArticleRequest(
                /** 文章标题，必填字段 */
                @NotBlank String title,
                /** 文章类型名称 */
                String typeName,
                /** 封面图片URL */
                String coverUrl,
                /** 文章摘要 */
                String summary,
                /** 文章内容，必填字段 */
                @NotBlank String content,
                /** 文章来源 */
                String source,
                /** 来源链接 */
                String sourceUrl,
                /** 标签（逗号分隔） */
                String tags,
                /** 是否发布 */
                boolean published,
                /** 是否置顶 */
                boolean pinned) {
}