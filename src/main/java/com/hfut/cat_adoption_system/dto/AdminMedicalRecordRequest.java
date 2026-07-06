package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 管理员医疗记录请求DTO
 * 
 * 用于管理员创建或更新猫咪医疗记录的请求参数
 */
public record AdminMedicalRecordRequest(
                /** 记录类型，必填字段（如：体检、疫苗接种、治疗、手术等） */
                @NotBlank String recordType,
                /** 记录日期 */
                LocalDate recordDate,
                /** 就诊描述，必填字段（症状、检查项目等） */
                @NotBlank String description,
                /** 健康结果，必填字段（诊断结果、治疗方案等） */
                @NotBlank String healthResult,
                /** 疫苗状态 */
                String vaccineStatus,
                /** 绝育状态 */
                String sterilizedStatus,
                /** 费用金额 */
                BigDecimal cost,
                /** 附件URL（检查报告、病历照片等） */
                String attachmentUrl,
                /** 是否异常标志 */
                boolean abnormalFlag) {
}