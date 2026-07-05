package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.HospitalDashboardSummary;
import com.hfut.cat_adoption_system.model.MedicalRecord;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/hospital")
@RequireRole({Role.HOSPITAL, Role.ADMIN})
public class AdminHospitalController {
    private final CatAdoptionService service;

    public AdminHospitalController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public ApiResponse<HospitalDashboardSummary> summary() {
        return ApiResponse.ok(service.hospitalDashboardSummary());
    }

    @GetMapping("/records")
    public ApiResponse<List<MedicalRecord>> records(@RequestParam(required = false) Integer limit) {
        return ApiResponse.ok(service.listHospitalMedicalRecords(limit));
    }
}
