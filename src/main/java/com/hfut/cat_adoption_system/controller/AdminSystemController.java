package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.*;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员系统管理控制器
 * 
 * 提供系统级别的管理功能，包括：
 * - 操作日志查询（管理员权限）
 * - 全局搜索（多模块联合搜索）
 * - 公告管理（管理员权限）
 */
@RestController
public class AdminSystemController {

    /** 认养服务：处理系统管理相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdminSystemController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询操作日志列表（管理员权限）
     * 
     * @param operatorKeyword 操作人关键词筛选（可选）
     * @param operationType   操作类型筛选（可选）
     * @param bizType         业务类型筛选（可选）
     * @param startTime       开始时间筛选（可选）
     * @param endTime         结束时间筛选（可选）
     * @param status          状态筛选（可选）
     * @param keyword         关键词搜索（可选）
     * @param page            页码（可选）
     * @param size            每页数量（可选）
     */
    @GetMapping("/api/admin/logs")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<OperationLogInfo>> logs(@RequestParam(required = false) String operatorKeyword,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        // 兼容旧版搜索参数：当status为空时使用keyword作为状态筛选
        String statusFilter = status == null || status.isBlank() ? keyword : status;
        return ApiResponse.ok(service.listOperationLogs(operatorKeyword, operationType, bizType, startTime, endTime,
                statusFilter, page, size));
    }

    /**
     * 查询操作日志详情（管理员权限）
     * 
     * @param id 日志ID
     */
    @GetMapping("/api/admin/logs/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<OperationLogInfo> logDetail(@PathVariable Long id) {
        return ApiResponse.ok(service.getOperationLog(id));
    }

    /**
     * 全局搜索
     * 在多个业务模块中联合搜索关键词，返回分类结果
     * 
     * @param keyword 搜索关键词
     */
    @GetMapping("/api/admin/search")
    @RequireRole({ Role.VOLUNTEER, Role.HOSPITAL, Role.ADMIN })
    public ApiResponse<List<AdminSearchGroup>> search(@RequestParam String keyword) {
        return ApiResponse.ok(service.adminSearch(keyword));
    }

    /**
     * 查询公告列表（管理员权限）
     * 
     * @param publishStatus 发布状态筛选（可选）
     * @param noticeType    公告类型筛选（可选）
     */
    @GetMapping("/api/admin/notices")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<NoticeAdminInfo>> adminNotices(@RequestParam(required = false) String publishStatus,
            @RequestParam(required = false) String noticeType) {
        return ApiResponse.ok(service.listAdminNotices(publishStatus, noticeType));
    }

    /**
     * 创建公告（管理员权限）
     * 
     * @param request 公告创建请求（包含标题、内容、类型等）
     */
    @PostMapping("/api/admin/notices")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> createNotice(@Valid @RequestBody NoticeAdminRequest request) {
        service.createAdminNotice(request);
        return ApiResponse.created(true);
    }

    /**
     * 更新公告（管理员权限）
     * 
     * @param id      公告ID
     * @param request 公告更新请求
     */
    @PutMapping("/api/admin/notices/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> updateNotice(@PathVariable String id, @Valid @RequestBody NoticeAdminRequest request) {
        service.updateAdminNotice(id, request);
        return ApiResponse.ok(true);
    }

    /**
     * 发布公告（管理员权限）
     * 将公告状态改为已发布，使其对用户可见
     * 
     * @param id 公告ID
     */
    @PutMapping("/api/admin/notices/{id}/publish")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> publishNotice(@PathVariable String id) {
        service.publishNotice(id);
        return ApiResponse.ok(true);
    }

    /**
     * 公告下线（管理员权限）
     * 将公告状态改为已下线，使其对用户不可见
     * 
     * @param id 公告ID
     */
    @PutMapping("/api/admin/notices/{id}/offline")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> offlineNotice(@PathVariable String id) {
        service.offlineNotice(id);
        return ApiResponse.ok(true);
    }

    /**
     * 删除公告（管理员权限）
     * 
     * @param id 公告ID
     */
    @DeleteMapping("/api/admin/notices/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> deleteNotice(@PathVariable String id) {
        service.deleteAdminNotice(id);
        return ApiResponse.ok(true);
    }
}