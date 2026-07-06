package com.hfut.cat_adoption_system.dto;

/**
 * 仪表盘统计数据DTO
 * 
 * 封装仪表盘展示的核心统计指标，用于系统数据总览
 */
public record DashboardStats(
                /** 猫咪总数 */
                long catCount,
                /** 可认养猫咪数量 */
                long adoptableCount,
                /** 已认养猫咪数量 */
                long adoptedCount,
                /** 待处理认养申请数量 */
                long pendingApplicationCount,
                /** 治疗中猫咪数量 */
                long medicalCount,
                /** 回访任务数量 */
                long followupCount,
                /** 绝育率 */
                double sterilizationRate,
                /** 疫苗接种率 */
                double vaccinationRate,
                /** 认养成功率 */
                double adoptionSuccessRate) {
}