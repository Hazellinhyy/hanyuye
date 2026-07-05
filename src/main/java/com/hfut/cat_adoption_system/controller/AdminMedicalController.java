package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.AdminMedicalRecordRequest;
import com.hfut.cat_adoption_system.model.MedicalRecord;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cats/{catId}/medical-records")
public class AdminMedicalController {
    private final CatAdoptionService service;

    public AdminMedicalController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    @RequireRole({Role.VOLUNTEER, Role.HOSPITAL, Role.ADMIN})
    public ApiResponse<List<MedicalRecord>> records(@PathVariable String catId) {
        return ApiResponse.ok(service.listMedicalRecords(catId));
    }

    @PostMapping
    @RequireRole({Role.HOSPITAL, Role.ADMIN})
    public ApiResponse<MedicalRecord> addRecord(@PathVariable String catId,
                                                @Valid @RequestBody AdminMedicalRecordRequest request) {
        return ApiResponse.created(service.addAdminMedicalRecord(catId, request));
    }
}
