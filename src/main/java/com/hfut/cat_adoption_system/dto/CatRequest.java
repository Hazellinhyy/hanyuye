package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.CatStatus;
import com.hfut.cat_adoption_system.model.HealthLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * 猫咪档案请求DTO
 * 
 * 用于创建或更新猫咪档案的请求参数
 */
public record CatRequest(
                /** 猫咪名称 */
                String catName,
                /** 发现地点，必填字段 */
                @NotBlank String foundPlace,
                /** 发现日期 */
                LocalDate foundDate,
                /** 性别 */
                String gender,
                /** 毛色 */
                String color,
                /** 年龄估计 */
                String ageEstimate,
                /** 性格特点 */
                String personality,
                /** 健康等级，必填字段 */
                @NotNull HealthLevel healthLevel,
                /** 是否已绝育 */
                boolean sterilized,
                /** 是否已接种疫苗 */
                boolean vaccinated,
                /** 猫咪状态，必填字段 */
                @NotNull CatStatus status,
                /** 封面图片URL，必填字段 */
                @NotBlank String coverUrl,
                /** 标签列表 */
                List<String> tags,
                /** 详细描述 */
                String description) {
}