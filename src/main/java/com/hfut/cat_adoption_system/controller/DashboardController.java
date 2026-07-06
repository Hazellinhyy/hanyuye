package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.PublicApi;
import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.DashboardDistributionItem;
import com.hfut.cat_adoption_system.dto.DashboardStats;
import com.hfut.cat_adoption_system.dto.LocationStat;
import com.hfut.cat_adoption_system.model.AuditLog;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 仪表盘数据控制器
 * 
 * 提供系统数据统计和仪表盘展示相关的接口：
 * - 获取整体统计数据
 * - 获取操作日志（管理员权限）
 * - 获取位置统计数据
 * - 获取各类状态分布数据（猫咪状态、申请状态、回访状态）
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    /** 认养服务：处理仪表盘数据统计相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public DashboardController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 获取仪表盘统计数据（公开接口）
     * 返回系统整体统计信息，包括猫咪总数、申请数、认养成功数等核心指标
     */
    @GetMapping("/stats")
    @PublicApi
    public ApiResponse<DashboardStats> stats() {
        return ApiResponse.ok(service.dashboard());
    }

    /**
     * 获取操作日志（管理员权限）
     * 返回系统的审计日志列表，记录关键操作记录
     */
    @GetMapping("/logs")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<AuditLog>> logs() {
        return ApiResponse.ok(service.listAuditLogs());
    }

    /**
     * 获取位置统计数据（公开接口）
     * 返回猫咪分布的地理位置统计信息
     */
    @GetMapping("/locations")
    @PublicApi
    public ApiResponse<List<LocationStat>> locations() {
        return ApiResponse.ok(service.locationStats());
    }

    /**
     * 获取猫咪状态分布（公开接口）
     * 返回不同状态猫咪的数量分布（如待领养、领养中、已领养、治疗中等）
     */
    @GetMapping("/cat-status")
    @PublicApi
    public ApiResponse<List<DashboardDistributionItem>> catStatus() {
        return ApiResponse.ok(service.dashboardDistribution("cat-status"));
    }

    /**
     * 获取申请状态分布（公开接口）
     * 返回认养申请各状态的数量分布（如待审核、审核通过、审核拒绝等）
     */
    @GetMapping("/application-status")
    @PublicApi
    public ApiResponse<List<DashboardDistributionItem>> applicationStatus() {
        return ApiResponse.ok(service.dashboardDistribution("application-status"));
    }

    /**
     * 获取回访状态分布（公开接口）
     * 返回认养回访各状态的数量分布（如待回访、回访中、回访完成等）
     */
    @GetMapping("/followup-status")
    @PublicApi
    public ApiResponse<List<DashboardDistributionItem>> followupStatus() {
        return ApiResponse.ok(service.dashboardDistribution("followup-status"));
    }
}