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

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final CatAdoptionService service;

    public DashboardController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping("/stats")
    @PublicApi
    public ApiResponse<DashboardStats> stats() {
        return ApiResponse.ok(service.dashboard());
    }

    @GetMapping("/logs")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<AuditLog>> logs() {
        return ApiResponse.ok(service.listAuditLogs());
    }

    @GetMapping("/locations")
    @PublicApi
    public ApiResponse<List<LocationStat>> locations() {
        return ApiResponse.ok(service.locationStats());
    }

    @GetMapping("/cat-status")
    @PublicApi
    public ApiResponse<List<DashboardDistributionItem>> catStatus() {
        return ApiResponse.ok(service.dashboardDistribution("cat-status"));
    }

    @GetMapping("/application-status")
    @PublicApi
    public ApiResponse<List<DashboardDistributionItem>> applicationStatus() {
        return ApiResponse.ok(service.dashboardDistribution("application-status"));
    }

    @GetMapping("/followup-status")
    @PublicApi
    public ApiResponse<List<DashboardDistributionItem>> followupStatus() {
        return ApiResponse.ok(service.dashboardDistribution("followup-status"));
    }
}
