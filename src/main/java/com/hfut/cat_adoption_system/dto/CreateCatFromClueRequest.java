package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.HealthLevel;

/**
 * 从线索创建猫咪档案请求DTO
 * 
 * 当流浪猫线索审核通过后，基于线索信息创建正式的猫咪档案
 */
public record CreateCatFromClueRequest(
                /** 猫咪名称 */
                String name,
                /** 性别 */
                String gender,
                /** 年龄估计 */
                String ageEstimate,
                /** 毛色 */
                String coatColor,
                /** 性格特点 */
                String personality,
                /** 健康状态 */
                HealthLevel healthStatus,
                /** 是否已绝育 */
                Boolean sterilizedStatus,
                /** 是否已接种疫苗 */
                Boolean vaccineStatus,
                /** 额外描述信息 */
                String extraDescription) {
}