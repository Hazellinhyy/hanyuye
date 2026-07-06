package com.hfut.cat_adoption_system.dto;

/**
 * 回访任务刷新结果DTO
 * 
 * 封装回访任务刷新操作的结果统计，用于更新逾期任务状态并生成告警
 */
public record FollowupRefreshResult(
                /** 更新的任务数量（如状态变更的任务数） */
                int updatedTaskCount,
                /** 生成的告警数量（如逾期任务触发的告警） */
                int generatedWarningCount) {
}