package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ActionReasonRequest;
import com.hfut.cat_adoption_system.dto.AgreementEditRequest;
import com.hfut.cat_adoption_system.dto.AgreementHandoverRequest;
import com.hfut.cat_adoption_system.dto.AgreementInfo;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/agreements")
@RequireRole({Role.VOLUNTEER, Role.ADMIN})
public class AdminAgreementController {
    private final CatAdoptionService service;

    public AdminAgreementController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping("/pending")
    public ApiResponse<List<AgreementInfo>> pending(@RequestParam(required = false) String status,
                                                    @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.listPendingAgreements(status, keyword));
    }

    @PostMapping("/{applicationId}/generate")
    @RequireRole(Role.ADMIN)
    public ApiResponse<AgreementInfo> generate(@PathVariable String applicationId) {
        return ApiResponse.created(service.generateAgreement(applicationId));
    }

    @GetMapping("/{id}")
    public ApiResponse<AgreementInfo> detail(@PathVariable Long id) {
        return ApiResponse.ok(service.getAgreement(id));
    }

    @PutMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<AgreementInfo> update(@PathVariable Long id,
                                             @Valid @RequestBody AgreementEditRequest request) {
        return ApiResponse.ok(service.updateAgreement(id, request));
    }

    @PutMapping("/{id}/handover")
    @RequireRole(Role.ADMIN)
    public ApiResponse<AgreementInfo> handover(@PathVariable Long id,
                                               @Valid @RequestBody AgreementHandoverRequest request) {
        return ApiResponse.ok(service.completeHandover(id, request));
    }

    @PutMapping("/{id}/cancel")
    @RequireRole(Role.ADMIN)
    public ApiResponse<AgreementInfo> cancel(@PathVariable Long id,
                                             @Valid @RequestBody ActionReasonRequest request) {
        return ApiResponse.ok(service.cancelAgreement(id, request));
    }
}
