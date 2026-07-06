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

/**
 * 管理员用户管理控制器
 * 
 * 提供系统用户的管理功能，包括：
 * - 查询用户列表（支持角色、状态、关键词筛选）
 * - 创建用户
 * - 更新用户信息
 * - 更新用户角色
 * - 启用/禁用用户
 * 
 * 类级别权限：仅管理员( ADMIN )可访问
 */
@RestController
@RequestMapping("/api/admin/users")
@RequireRole(Role.ADMIN)
public class AdminUserController {

    /** 认养服务：处理用户管理相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdminUserController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询用户列表
     * 
     * @param role    角色筛选（可选）
     * @param enabled 启用状态筛选（可选）
     * @param keyword 关键词搜索（可选）
     */
    @GetMapping
    public ApiResponse<List<User>> list(@RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.listAdminUsers(role, enabled, keyword));
    }

    /**
     * 创建用户
     * 
     * @param request 用户创建请求（包含用户名、密码、角色等）
     */
    @PostMapping
    public ApiResponse<User> create(@Valid @RequestBody AdminUserRequest request) {
        return ApiResponse.created(service.createAdminUser(request));
    }

    /**
     * 更新用户信息
     * 
     * @param id      用户ID
     * @param request 用户更新请求
     */
    @PutMapping("/{id}")
    public ApiResponse<User> update(@PathVariable String id, @Valid @RequestBody AdminUserRequest request) {
        return ApiResponse.ok(service.updateAdminUser(id, request));
    }

    /**
     * 更新用户角色
     * 
     * @param id   用户ID
     * @param role 目标角色
     */
    @PutMapping("/{id}/role")
    public ApiResponse<User> updateRole(@PathVariable String id, @RequestParam Role role) {
        return ApiResponse.ok(service.updateAdminUserRole(id, role));
    }

    /**
     * 更新用户状态（启用/禁用）
     * 
     * @param id      用户ID
     * @param enabled 是否启用
     */
    @PutMapping("/{id}/status")
    public ApiResponse<User> updateStatus(@PathVariable String id, @RequestParam boolean enabled) {
        return ApiResponse.ok(service.updateAdminUserStatus(id, enabled));
    }
}