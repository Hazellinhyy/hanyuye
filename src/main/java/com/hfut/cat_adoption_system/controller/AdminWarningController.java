package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.WarningHandleRequest;
import com.hfut.cat_adoption_system.dto.WarningInfo;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/warnings")
@RequireRole({Role.VOLUNTEER, Role.ADMIN})
public class AdminWarningController {
    private final CatAdoptionService service;

    public AdminWarningController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<WarningInfo>> list(@RequestParam(required = false) String warningType,
                                               @RequestParam(required = false) String warningLevel,
                                               @RequestParam(required = false) String status,
                                               @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.listWarnings(warningType, warningLevel, status, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<WarningInfo> detail(@PathVariable Long id) {
        return ApiResponse.ok(service.getWarning(id));
    }

    @PutMapping("/{id}/handle")
    @RequireRole(Role.ADMIN)
    public ApiResponse<WarningInfo> handle(@PathVariable Long id,
                                           @Valid @RequestBody WarningHandleRequest request) {
        return ApiResponse.ok(service.handleWarning(id, request));
    }
}
