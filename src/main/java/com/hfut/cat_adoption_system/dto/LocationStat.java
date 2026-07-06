package com.hfut.cat_adoption_system.dto;

/**
 * 猫咪发现地点统计DTO
 * 
 * 用于封装猫咪发现地点的统计数据，记录每个发现地点对应的猫咪数量，
 * 便于统计分析猫咪的来源分布情况。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record LocationStat(
    /** 猫咪发现地点（如街道、小区、公园等） */
    String foundPlace,
    /** 该地点发现的猫咪数量 */
    long count) {
}