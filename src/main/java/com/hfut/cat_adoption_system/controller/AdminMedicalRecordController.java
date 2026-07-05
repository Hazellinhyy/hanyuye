package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ActionReasonRequest;
import com.hfut.cat_adoption_system.dto.AdminMedicalRecordRequest;
import com.hfut.cat_adoption_system.model.MedicalRecord;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/medical-records")
@RequireRole({Role.HOSPITAL, Role.ADMIN})
public class AdminMedicalRecordController {
    private final CatAdoptionService service;

    public AdminMedicalRecordController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ApiResponse<MedicalRecord> detail(@PathVariable String id) {
        return ApiResponse.ok(service.getMedicalRecord(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<MedicalRecord> update(@PathVariable String id,
                                             @Valid @RequestBody AdminMedicalRecordRequest request) {
        return ApiResponse.ok(service.updateAdminMedicalRecord(id, request));
    }

    @PutMapping("/{id}/void")
    public ApiResponse<Boolean> voidRecord(@PathVariable String id,
                                           @Valid @RequestBody ActionReasonRequest request) {
        service.voidAdminMedicalRecord(id, request);
        return ApiResponse.ok(true);
    }
}
