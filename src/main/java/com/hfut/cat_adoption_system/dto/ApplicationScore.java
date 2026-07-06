package com.hfut.cat_adoption_system.dto;

/**
 * 认养申请评分结果DTO
 * 
 * 封装认养申请的AI评分结果，用于辅助审核决策
 */
public record ApplicationScore(
                /** 评分分数 */
                int score,
                /** 风险等级（如：低风险、中风险、高风险） */
                String riskLevel,
                /** 评分理由说明 */
                String reasons) {
}