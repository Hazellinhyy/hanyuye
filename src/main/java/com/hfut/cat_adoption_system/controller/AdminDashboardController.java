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

@RestController
@RequestMapping("/api/admin/dashboard")
@RequireRole({Role.VOLUNTEER, Role.ADMIN})
public class AdminDashboardController {
    private final CatAdoptionService service;

    public AdminDashboardController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public ApiResponse<AdminDashboardSummary> summary() {
        return ApiResponse.ok(service.adminDashboardSummary());
    }

    @GetMapping("/cat-status")
    public ApiResponse<List<DashboardDistributionItem>> catStatus() {
        return ApiResponse.ok(service.dashboardDistribution("cat-status"));
    }

    @GetMapping("/application-status")
    public ApiResponse<List<DashboardDistributionItem>> applicationStatus() {
        return ApiResponse.ok(service.dashboardDistribution("application-status"));
    }

    @GetMapping("/followup-status")
    public ApiResponse<List<DashboardDistributionItem>> followupStatus() {
        return ApiResponse.ok(service.dashboardDistribution("followup-status"));
    }

    @GetMapping("/warning-type")
    public ApiResponse<List<DashboardDistributionItem>> warningType() {
        return ApiResponse.ok(service.dashboardDistribution("warning-type"));
    }
}
