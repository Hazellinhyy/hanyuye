package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.AdminUserRequest;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.model.User;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequireRole(Role.ADMIN)
public class AdminUserController {
    private final CatAdoptionService service;

    public AdminUserController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<User>> list(@RequestParam(required = false) Role role,
                                        @RequestParam(required = false) Boolean enabled,
                                        @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.listAdminUsers(role, enabled, keyword));
    }

    @PostMapping
    public ApiResponse<User> create(@Valid @RequestBody AdminUserRequest request) {
        return ApiResponse.created(service.createAdminUser(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<User> update(@PathVariable String id, @Valid @RequestBody AdminUserRequest request) {
        return ApiResponse.ok(service.updateAdminUser(id, request));
    }

    @PutMapping("/{id}/role")
    public ApiResponse<User> updateRole(@PathVariable String id, @RequestParam Role role) {
        return ApiResponse.ok(service.updateAdminUserRole(id, role));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<User> updateStatus(@PathVariable String id, @RequestParam boolean enabled) {
        return ApiResponse.ok(service.updateAdminUserStatus(id, enabled));
    }
}
