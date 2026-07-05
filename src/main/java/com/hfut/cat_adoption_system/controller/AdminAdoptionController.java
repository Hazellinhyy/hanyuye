package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ActionReasonRequest;
import com.hfut.cat_adoption_system.dto.ApplicationAuditRequest;
import com.hfut.cat_adoption_system.dto.ApplicationReviewDetail;
import com.hfut.cat_adoption_system.model.ApplicationStatus;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/adoption/applications")
@RequireRole({Role.VOLUNTEER, Role.ADMIN})
public class AdminAdoptionController {
    private final CatAdoptionService service;

    public AdminAdoptionController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<ApplicationReviewDetail>> list(@RequestParam(required = false) ApplicationStatus status,
                                                           @RequestParam(required = false) String riskLevel,
                                                           @RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false) Integer page,
                                                           @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(service.listAdminAdoptionApplications(status, riskLevel, keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<ApplicationReviewDetail> detail(@PathVariable String id) {
        return ApiResponse.ok(service.getAdminAdoptionApplication(id));
    }

    @PutMapping("/{id}/initial-audit")
    @RequireRole(Role.VOLUNTEER)
    public ApiResponse<ApplicationReviewDetail> initialAudit(@PathVariable String id,
                                                             @Valid @RequestBody ApplicationAuditRequest request) {
        return ApiResponse.ok(service.initialAudit(id, request));
    }

    @PutMapping("/{id}/final-audit")
    @RequireRole(Role.ADMIN)
    public ApiResponse<ApplicationReviewDetail> finalAudit(@PathVariable String id,
                                                           @Valid @RequestBody ApplicationAuditRequest request) {
        return ApiResponse.ok(service.finalAudit(id, request));
    }

    @PutMapping("/{id}/void")
    @RequireRole(Role.ADMIN)
    public ApiResponse<ApplicationReviewDetail> voidApplication(@PathVariable String id,
                                                                @Valid @RequestBody ActionReasonRequest request) {
        return ApiResponse.ok(service.voidAdminAdoptionApplication(id, request));
    }
}
