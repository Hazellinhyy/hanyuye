package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.FollowupRequest;
import com.hfut.cat_adoption_system.model.FollowupRecord;
import com.hfut.cat_adoption_system.model.FollowupResult;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 回访管理控制器
 * 
 * 提供认养回访相关的功能，支持志愿者和管理员角色：
 * - 查询回访记录列表
 * - 添加回访记录
 * 
 * 回访是认养流程的重要环节，用于跟踪已认养猫咪的生活状况
 */
@RestController
@RequestMapping("/api/followups")
public class FollowupController {

    /** 认养服务：处理回访相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public FollowupController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询回访记录列表（志愿者/管理员权限）
     * 
     * @param applicationId 认养申请ID筛选（可选）
     * @param result        回访结果筛选（可选）
     */
    @GetMapping
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<List<FollowupRecord>> listFollowups(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String applicationId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) FollowupResult result) {
        return ApiResponse.ok(service.listFollowups(applicationId, result));
    }

    /**
     * 添加回访记录（志愿者/管理员权限）
     * 记录认养后的回访情况，包括猫咪健康状况、生活环境等信息
     * 
     * @param request 回访请求（包含申请ID、回访时间、回访结果、备注等）
     */
    @PostMapping
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<FollowupRecord> addFollowup(@Valid @RequestBody FollowupRequest request) {
        return ApiResponse.created(service.addFollowup(request));
    }
}