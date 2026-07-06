package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.AdoptionApplicationRequest;
import com.hfut.cat_adoption_system.dto.ApplicationCancelRequest;
import com.hfut.cat_adoption_system.dto.ApplicationReviewDetail;
import com.hfut.cat_adoption_system.model.ApplicationStatus;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdoptionApplicationController {
    private final CatAdoptionService service;

    public AdoptionApplicationController(CatAdoptionService service) {
        this.service = service;
    }

    @PostMapping("/api/adoption/applications")
    @RequireRole(Role.STUDENT)
    public ApiResponse<ApplicationReviewDetail> submit(@Valid @RequestBody AdoptionApplicationRequest request) {
        return ApiResponse.created(service.submitAdoptionApplication(request));
    }

    @GetMapping("/api/my/adoption/applications")
    @RequireRole(Role.STUDENT)
    public ApiResponse<List<ApplicationReviewDetail>> myApplications(@RequestParam(required = false) ApplicationStatus status) {
        return ApiResponse.ok(service.listMyAdoptionApplications(status));
    }

    @GetMapping("/api/my/adoption/applications/{id}")
    @RequireRole(Role.STUDENT)
    public ApiResponse<ApplicationReviewDetail> myApplication(@PathVariable String id) {
        return ApiResponse.ok(service.getMyAdoptionApplication(id));
    }

    @PutMapping("/api/my/adoption/applications/{id}/cancel")
    @RequireRole(Role.STUDENT)
    public ApiResponse<ApplicationReviewDetail> cancel(@PathVariable String id,
                                                       @Valid @RequestBody ApplicationCancelRequest request) {
        return ApiResponse.ok(service.cancelMyAdoptionApplication(id, request));
    }
}
