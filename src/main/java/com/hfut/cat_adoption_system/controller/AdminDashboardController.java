package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.AdminDashboardSummary;
import com.hfut.cat_adoption_system.dto.DashboardDistributionItem;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员仪表盘控制器
 * 
 * 提供管理员后台仪表盘的数据统计功能，包括：
 * - 汇总统计（猫咪总数、申请数、待处理任务数等）
 * - 猫咪状态分布
 * - 认养申请状态分布
 * - 随访任务状态分布
 * - 告警类型分布
 * 
 * 类级别权限：志愿者( VOLUNTEER )和管理员( ADMIN )均可访问
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequireRole({ Role.VOLUNTEER, Role.ADMIN })
public class AdminDashboardController {

    /** 认养服务：处理仪表盘统计数据的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdminDashboardController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 获取仪表盘汇总数据
     * 返回关键业务指标的统计汇总，包括猫咪总数、申请数、待审核数、待随访数等
     */
    @GetMapping("/summary")
    public ApiResponse<AdminDashboardSummary> summary() {
        return ApiResponse.ok(service.adminDashboardSummary());
    }

    /**
     * 获取猫咪状态分布
     * 返回不同状态猫咪的数量分布（如待领养、领养中、已领养、治疗中等）
     */
    @GetMapping("/cat-status")
    public ApiResponse<List<DashboardDistributionItem>> catStatus() {
        return ApiResponse.ok(service.dashboardDistribution("cat-status"));
    }

    /**
     * 获取认养申请状态分布
     * 返回不同状态认养申请的数量分布（如待初审、初审通过、待终审、已通过等）
     */
    @GetMapping("/application-status")
    public ApiResponse<List<DashboardDistributionItem>> applicationStatus() {
        return ApiResponse.ok(service.dashboardDistribution("application-status"));
    }

    /**
     * 获取随访任务状态分布
     * 返回不同状态随访任务的数量分布（如待处理、进行中、已完成等）
     */
    @GetMapping("/followup-status")
    public ApiResponse<List<DashboardDistributionItem>> followupStatus() {
        return ApiResponse.ok(service.dashboardDistribution("followup-status"));
    }

    /**
     * 获取告警类型分布
     * 返回不同类型告警的数量分布（如健康告警、逾期告警等）
     */
    @GetMapping("/warning-type")
    public ApiResponse<List<DashboardDistributionItem>> warningType() {
        return ApiResponse.ok(service.dashboardDistribution("warning-type"));
    }
}
