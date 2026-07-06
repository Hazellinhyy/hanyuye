package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.HealthLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * 医疗记录请求DTO
 * 
 * 用于接收创建或更新医疗记录的请求参数，
 * 通过Jakarta Validation注解实现表单校验。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record MedicalRecordRequest(
                /** 猫咪ID（必填） */
                @NotBlank(message = "猫咪ID不能为空") String catId,

                /** 检查日期 */
                LocalDate checkDate,

                /** 医院名称（必填） */
                @NotBlank(message = "医院名称不能为空") String hospital,

                /** 健康等级（必填） */
                @NotNull(message = "健康等级不能为空") HealthLevel healthLevel,

                /** 是否已接种疫苗 */
                boolean vaccinated,

                /** 是否已绝育 */
                boolean sterilized,

                /** 治疗方案描述 */
                String treatment,

                /** 医生备注信息 */
                String doctorNote) {
}