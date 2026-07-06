package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ApplicationDetail;
import com.hfut.cat_adoption_system.dto.ApplicationRequest;
import com.hfut.cat_adoption_system.dto.ReviewRequest;
import com.hfut.cat_adoption_system.model.AdoptionApplication;
import com.hfut.cat_adoption_system.model.ApplicationStatus;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 认养申请控制器
 * 
 * 提供认养申请的核心业务功能，支持多角色操作：
 * - 查询申请列表（志愿者/管理员）
 * - 查询申请详情列表（志愿者/管理员）
 * - 提交认养申请（学生）
 * - 审核申请（志愿者/管理员）
 * - 完成交接（管理员）
 * - 撤回申请
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    /** 认养服务：处理认养申请相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public ApplicationController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询认养申请列表（志愿者/管理员权限）
     * 
     * @param status 申请状态筛选（可选）
     */
    @GetMapping
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<List<AdoptionApplication>> listApplications(
            @RequestParam(required = false) ApplicationStatus status) {
        return ApiResponse.ok(service.listApplications(status));
    }

    /**
     * 查询认养申请详情列表（志愿者/管理员权限）
     * 返回包含完整信息的申请详情，用于审核决策
     * 
     * @param status 申请状态筛选（可选）
     */
    @GetMapping("/details")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<List<ApplicationDetail>> listApplicationDetails(
            @RequestParam(required = false) ApplicationStatus status) {
        return ApiResponse.ok(service.listApplicationDetails(status));
    }

    /**
     * 提交认养申请（学生权限）
     * 
     * @param request 申请请求（包含申请人信息、意向猫咪等）
     */
    @PostMapping
    @RequireRole(Role.STUDENT)
    public ApiResponse<AdoptionApplication> submitApplication(@Valid @RequestBody ApplicationRequest request) {
        return ApiResponse.created(service.submitApplication(request));
    }

    /**
     * 审核认养申请（志愿者/管理员权限）
     * 对申请进行审核，可通过或拒绝
     * 
     * @param applicationId 申请ID
     * @param request       审核请求（包含审核结果和备注）
     */
    @PatchMapping("/{applicationId}/review")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<AdoptionApplication> review(@PathVariable String applicationId,
            @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok(service.reviewApplication(applicationId, request));
    }

    /**
     * 完成认养交接（管理员权限）
     * 申请通过审核后，执行交接操作完成猫咪交付
     * 
     * @param applicationId 申请ID
     */
    @PatchMapping("/{applicationId}/handover")
    @RequireRole(Role.ADMIN)
    public ApiResponse<AdoptionApplication> handover(@PathVariable String applicationId) {
        return ApiResponse.ok(service.handover(applicationId));
    }

    /**
     * 撤回认养申请
     * 申请人可在审核完成前撤回自己的申请
     * 
     * @param applicationId 申请ID
     */
    @PatchMapping("/{applicationId}/withdraw")
    public ApiResponse<AdoptionApplication> withdraw(@PathVariable String applicationId) {
        return ApiResponse.ok(service.withdraw(applicationId));
    }
}