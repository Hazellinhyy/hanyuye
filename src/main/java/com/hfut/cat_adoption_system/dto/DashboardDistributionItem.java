package com.hfut.cat_adoption_system.dto;

/**
 * 仪表盘分布数据项DTO
 * 
 * 用于封装仪表盘统计中的各类分布数据，如状态分布、类型分布等
 */
public record DashboardDistributionItem(
                /** 名称（如状态名称、类型名称等） */
                String name,
                /** 数量 */
                long count) {
}