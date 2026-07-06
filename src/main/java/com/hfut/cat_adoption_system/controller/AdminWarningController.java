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

/**
 * 管理员告警管理控制器
 * 
 * 提供系统告警的管理功能，包括：
 * - 查询告警列表（支持类型、级别、状态、关键词筛选）
 * - 查看告警详情
 * - 处理告警（管理员权限）
 * - 删除告警（管理员权限）
 * 
 * 类级别权限：志愿者( VOLUNTEER )和管理员( ADMIN )均可访问
 */
@RestController
@RequestMapping("/api/admin/warnings")
@RequireRole({ Role.VOLUNTEER, Role.ADMIN })
public class AdminWarningController {

    /** 认养服务：处理告警相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdminWarningController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询告警列表
     * 
     * @param warningType  告警类型筛选（可选）
     * @param warningLevel 告警级别筛选（可选）
     * @param status       处理状态筛选（可选）
     * @param keyword      关键词搜索（可选）
     */
    @GetMapping
    public ApiResponse<List<WarningInfo>> list(@RequestParam(required = false) String warningType,
            @RequestParam(required = false) String warningLevel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.listWarnings(warningType, warningLevel, status, keyword));
    }

    /**
     * 查询告警详情
     * 
     * @param id 告警ID
     */
    @GetMapping("/{id}")
    public ApiResponse<WarningInfo> detail(@PathVariable Long id) {
        return ApiResponse.ok(service.getWarning(id));
    }

    /**
     * 处理告警（管理员权限）
     * 将告警标记为已处理，需要填写处理结果
     * 
     * @param id      告警ID
     * @param request 处理请求（包含处理结果）
     */
    @PutMapping("/{id}/handle")
    @RequireRole(Role.ADMIN)
    public ApiResponse<WarningInfo> handle(@PathVariable Long id,
            @Valid @RequestBody WarningHandleRequest request) {
        return ApiResponse.ok(service.handleWarning(id, request));
    }

    /**
     * 删除告警（管理员权限）
     * 
     * @param id 告警ID
     */
    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        service.deleteWarning(id);
        return ApiResponse.ok(true);
    }
}