package com.hfut.cat_adoption_system.dto;

/**
 * 管理员搜索结果DTO
 * 
 * 封装管理员全局搜索的单个结果项，用于前端展示搜索结果列表
 */
public record AdminSearchResult(
                /** 记录ID */
                String id,
                /** 标题（主要显示内容） */
                String title,
                /** 副标题（辅助说明信息） */
                String subtitle,
                /** 目标页面路由标识（用于前端跳转） */
                String targetHash,
                /** 状态标签 */
                String status) {
}