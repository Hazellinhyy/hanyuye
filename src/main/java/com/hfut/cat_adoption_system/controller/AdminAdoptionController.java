package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ActionReasonRequest;
import com.hfut.cat_adoption_system.dto.ApplicationAuditRequest;
import com.hfut.cat_adoption_system.dto.ApplicationReviewDetail;
import com.hfut.cat_adoption_system.model.ApplicationStatus;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员认养申请管理控制器
 * 
 * 提供认养申请的审核管理功能，包括：
 * - 查询认养申请列表（支持状态、风险等级、关键词筛选）
 * - 查看申请详情
 * - 志愿者初审（通过/拒绝）
 * - 管理员终审（通过/拒绝）
 * - 作废申请（管理员权限）
 * 
 * 类级别权限：志愿者( VOLUNTEER )和管理员( ADMIN )均可访问
 */
@RestController
@RequestMapping("/api/admin/adoption/applications")
@RequireRole({ Role.VOLUNTEER, Role.ADMIN })
public class AdminAdoptionController {

    /** 认养服务：处理认养申请的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdminAdoptionController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询认养申请列表（管理员/志愿者视角）
     * 
     * @param status    申请状态筛选（可选）
     * @param riskLevel 风险等级筛选（可选）
     * @param keyword   关键词搜索（可选）
     * @param page      页码（可选）
     * @param size      每页数量（可选）
     */
    @GetMapping
    public ApiResponse<List<ApplicationReviewDetail>> list(@RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(service.listAdminAdoptionApplications(status, riskLevel, keyword, page, size));
    }

    /**
     * 查询认养申请详情
     * 
     * @param id 申请ID
     */
    @GetMapping("/{id}")
    public ApiResponse<ApplicationReviewDetail> detail(@PathVariable String id) {
        return ApiResponse.ok(service.getAdminAdoptionApplication(id));
    }

    /**
     * 认养申请初审（志愿者权限）
     * 志愿者对认养申请进行初步审核，审核通过后进入终审流程
     * 
     * @param id      申请ID
     * @param request 审核请求（包含审核结果和备注）
     */
    @PutMapping("/{id}/initial-audit")
    @RequireRole(Role.VOLUNTEER)
    public ApiResponse<ApplicationReviewDetail> initialAudit(@PathVariable String id,
            @Valid @RequestBody ApplicationAuditRequest request) {
        return ApiResponse.ok(service.initialAudit(id, request));
    }

    /**
     * 认养申请终审（管理员权限）
     * 管理员对通过初审的申请进行最终审核，决定是否批准认养
     * 
     * @param id      申请ID
     * @param request 审核请求（包含审核结果和备注）
     */
    @PutMapping("/{id}/final-audit")
    @RequireRole(Role.ADMIN)
    public ApiResponse<ApplicationReviewDetail> finalAudit(@PathVariable String id,
            @Valid @RequestBody ApplicationAuditRequest request) {
        return ApiResponse.ok(service.finalAudit(id, request));
    }

    /**
     * 作废认养申请（管理员权限）
     * 管理员可将申请标记为作废状态，需要填写作废原因
     * 
     * @param id      申请ID
     * @param request 操作请求（包含作废原因）
     */
    @PutMapping("/{id}/void")
    @RequireRole(Role.ADMIN)
    public ApiResponse<ApplicationReviewDetail> voidApplication(@PathVariable String id,
            @Valid @RequestBody ActionReasonRequest request) {
        return ApiResponse.ok(service.voidAdminAdoptionApplication(id, request));
    }
}