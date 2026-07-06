package com.hfut.cat_adoption_system.dto;

/**
 * 管理员仪表盘汇总数据DTO
 * 
 * 封装管理员仪表盘展示的各项统计指标，用于系统数据总览
 */
public record AdminDashboardSummary(
                /** 猫咪总数 */
                long catCount,
                /** 可认养猫咪数量 */
                long adoptableCount,
                /** 观察中猫咪数量 */
                long observingCount,
                /** 治疗中猫咪数量 */
                long medicalCount,
                /** 暂停认养猫咪数量 */
                long suspendedCount,
                /** 待审核线索数量 */
                long pendingClueCount,
                /** 已创建猫咪档案的线索数量 */
                long createdCatClueCount,
                /** 待初审认养申请数量 */
                long pendingInitialApplicationCount,
                /** 待终审认养申请数量 */
                long pendingFinalApplicationCount,
                /** 待交接认养申请数量 */
                long pendingHandoverApplicationCount,
                /** 已完成交接认养申请数量 */
                long handedOverApplicationCount,
                /** 待回访任务数量 */
                long pendingFollowupTaskCount,
                /** 已完成回访任务数量 */
                long completedFollowupTaskCount,
                /** 逾期回访任务数量 */
                long overdueFollowupTaskCount,
                /** 异常回访任务数量 */
                long abnormalFollowupTaskCount,
                /** 待处理告警数量 */
                long pendingWarningCount,
                /** 已处理告警数量 */
                long handledWarningCount,
                /** 回访完成率 */
                double followupCompletionRate,
                /** 关注猫咪数量 */
                long followingCatCount,
                /** 高风险认养申请数量 */
                long highRiskApplicationCount,
                /** 未读消息数量 */
                long unreadMessageCount) {
}