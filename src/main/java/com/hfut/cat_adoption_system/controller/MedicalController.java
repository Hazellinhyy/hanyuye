package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.MedicalRecordRequest;
import com.hfut.cat_adoption_system.model.MedicalRecord;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalController {
    private final CatAdoptionService service;

    public MedicalController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    @RequireRole({Role.VOLUNTEER, Role.HOSPITAL, Role.ADMIN})
    public ApiResponse<List<MedicalRecord>> listMedicalRecords(@RequestParam(required = false) String catId) {
        return ApiResponse.ok(service.listMedicalRecords(catId));
    }

    @PostMapping
    @RequireRole({Role.HOSPITAL, Role.ADMIN})
    public ApiResponse<MedicalRecord> addMedicalRecord(@Valid @RequestBody MedicalRecordRequest request) {
        return ApiResponse.created(service.addMedicalRecord(request));
    }
}
