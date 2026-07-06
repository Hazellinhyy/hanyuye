package com.hfut.cat_adoption_system.dto;

/**
 * 医院仪表盘汇总数据DTO
 * 
 * 该类用于封装医院端仪表盘展示的各项统计指标数据，
 * 为医院用户提供系统运营状况的概览信息。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record HospitalDashboardSummary(
                /** 正在接受治疗的猫咪数量 */
                long medicalCatCount,

                /** 处于观察期的猫咪数量 */
                long observingCatCount,

                /** 异常医疗记录数量（如紧急情况、异常指标等） */
                long abnormalRecordCount,

                /** 当前用户创建的医疗记录数量 */
                long myRecordCount,

                /** 医疗记录总数量 */
                long totalRecordCount,

                /** 待接种疫苗的猫咪数量 */
                long pendingVaccineCount,

                /** 待绝育手术的猫咪数量 */
                long pendingSterilizationCount) {
}