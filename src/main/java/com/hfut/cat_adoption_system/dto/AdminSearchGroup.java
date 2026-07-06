package com.hfut.cat_adoption_system.dto;

import java.util.List;

/**
 * 管理员搜索分组结果DTO
 * 
 * 用于封装管理员全局搜索的分组结果，将搜索结果按类型分组展示
 */
public record AdminSearchGroup(
                /** 分组类型（如：cat、application、clue等） */
                String type,
                /** 分组标签（用于前端展示的名称） */
                String label,
                /** 该分组下的搜索结果列表 */
                List<AdminSearchResult> items) {
}