package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.AdoptionApplicationRequest;
import com.hfut.cat_adoption_system.dto.ApplicationCancelRequest;
import com.hfut.cat_adoption_system.dto.ApplicationReviewDetail;
import com.hfut.cat_adoption_system.model.ApplicationStatus;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学生认养申请控制器
 * 
 * 提供学生用户的认养申请相关功能，包括：
 * - 提交认养申请
 * - 查询我的申请列表
 * - 查询我的申请详情
 * - 取消认养申请
 * 
 * 所有接口均需要学生角色权限
 */
@RestController
public class AdoptionApplicationController {

    /** 认养服务：处理认养申请相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdoptionApplicationController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 提交认养申请（学生权限）
     * 
     * @param request 认养申请请求（包含猫咪ID、个人信息等）
     */
    @PostMapping("/api/adoption/applications")
    @RequireRole(Role.STUDENT)
    public ApiResponse<ApplicationReviewDetail> submit(@Valid @RequestBody AdoptionApplicationRequest request) {
        return ApiResponse.created(service.submitAdoptionApplication(request));
    }

    /**
     * 查询我的认养申请列表（学生权限）
     * 
     * @param status 申请状态筛选（可选）
     */
    @GetMapping("/api/my/adoption/applications")
    @RequireRole(Role.STUDENT)
    public ApiResponse<List<ApplicationReviewDetail>> myApplications(
            @RequestParam(required = false) ApplicationStatus status) {
        return ApiResponse.ok(service.listMyAdoptionApplications(status));
    }

    /**
     * 查询我的认养申请详情（学生权限）
     * 
     * @param id 申请ID
     */
    @GetMapping("/api/my/adoption/applications/{id}")
    @RequireRole(Role.STUDENT)
    public ApiResponse<ApplicationReviewDetail> myApplication(@PathVariable String id) {
        return ApiResponse.ok(service.getMyAdoptionApplication(id));
    }

    /**
     * 取消认养申请（学生权限）
     * 在申请审核通过前，学生可以取消自己的认养申请
     * 
     * @param id      申请ID
     * @param request 取消请求（包含取消原因）
     */
    @PutMapping("/api/my/adoption/applications/{id}/cancel")
    @RequireRole(Role.STUDENT)
    public ApiResponse<ApplicationReviewDetail> cancel(@PathVariable String id,
            @Valid @RequestBody ApplicationCancelRequest request) {
        return ApiResponse.ok(service.cancelMyAdoptionApplication(id, request));
    }
}