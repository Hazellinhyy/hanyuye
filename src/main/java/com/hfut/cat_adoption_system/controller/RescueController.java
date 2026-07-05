package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ReportReviewRequest;
import com.hfut.cat_adoption_system.dto.RescueReportRequest;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.RescueReport;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class RescueController {
    private final CatAdoptionService service;

    public RescueController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<List<RescueReport>> listReports() {
        return ApiResponse.ok(service.listReports());
    }

    @PostMapping
    public ApiResponse<RescueReport> submitReport(@Valid @RequestBody RescueReportRequest request) {
        return ApiResponse.created(service.submitReport(request));
    }

    @PatchMapping("/{reportId}/review")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<Cat> reviewReport(@PathVariable String reportId,
                                         @Valid @RequestBody ReportReviewRequest request) {
        return ApiResponse.ok(service.reviewReport(reportId, request));
    }
}
