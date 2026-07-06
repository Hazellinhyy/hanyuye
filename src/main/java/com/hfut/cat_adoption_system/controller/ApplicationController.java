package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ApplicationDetail;
import com.hfut.cat_adoption_system.dto.ApplicationRequest;
import com.hfut.cat_adoption_system.dto.ReviewRequest;
import com.hfut.cat_adoption_system.model.AdoptionApplication;
import com.hfut.cat_adoption_system.model.ApplicationStatus;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    private final CatAdoptionService service;

    public ApplicationController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<List<AdoptionApplication>> listApplications(@RequestParam(required = false) ApplicationStatus status) {
        return ApiResponse.ok(service.listApplications(status));
    }

    @GetMapping("/details")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<List<ApplicationDetail>> listApplicationDetails(@RequestParam(required = false) ApplicationStatus status) {
        return ApiResponse.ok(service.listApplicationDetails(status));
    }

    @PostMapping
    @RequireRole(Role.STUDENT)
    public ApiResponse<AdoptionApplication> submitApplication(@Valid @RequestBody ApplicationRequest request) {
        return ApiResponse.created(service.submitApplication(request));
    }

    @PatchMapping("/{applicationId}/review")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<AdoptionApplication> review(@PathVariable String applicationId,
                                                   @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok(service.reviewApplication(applicationId, request));
    }

    @PatchMapping("/{applicationId}/handover")
    @RequireRole(Role.ADMIN)
    public ApiResponse<AdoptionApplication> handover(@PathVariable String applicationId) {
        return ApiResponse.ok(service.handover(applicationId));
    }

    @PatchMapping("/{applicationId}/withdraw")
    public ApiResponse<AdoptionApplication> withdraw(@PathVariable String applicationId) {
        return ApiResponse.ok(service.withdraw(applicationId));
    }
}
