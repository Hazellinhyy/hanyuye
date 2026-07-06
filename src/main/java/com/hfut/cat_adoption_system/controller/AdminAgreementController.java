package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ActionReasonRequest;
import com.hfut.cat_adoption_system.dto.AgreementEditRequest;
import com.hfut.cat_adoption_system.dto.AgreementHandoverRequest;
import com.hfut.cat_adoption_system.dto.AgreementInfo;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员认养协议管理控制器
 * 
 * 提供认养协议的管理功能，包括：
 * - 查询待交接协议列表
 * - 查看协议详情
 * - 修改协议信息（管理员权限）
 * - 完成协议交接（管理员权限）
 * - 取消协议（管理员权限）
 * - 删除协议（管理员权限）
 * 
 * 类级别权限：志愿者( VOLUNTEER )和管理员( ADMIN )均可访问
 */
@RestController
@RequestMapping("/api/admin/agreements")
@RequireRole({Role.VOLUNTEER, Role.ADMIN})
public class AdminAgreementController {
    
    /** 认养服务：处理认养协议的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdminAgreementController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询待交接协议列表
     * @param status 协议状态筛选（可选）
     * @param keyword 关键词搜索（可选）
     */
    @GetMapping("/pending")
    public ApiResponse<List<AgreementInfo>> pending(@RequestParam(required = false) String status,
                                                    @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.listPendingAgreements(status, keyword));
    }

    /**
     * 查询协议详情
     * @param id 协议ID
     */
    @GetMapping("/{id}")
    public ApiResponse<AgreementInfo> detail(@PathVariable Long id) {
        return ApiResponse.ok(service.getAgreement(id));
    }

    /**
     * 修改协议信息（管理员权限）
     * @param id 协议ID
     * @param request 修改请求（包含协议字段更新信息）
     */
    @PutMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<AgreementInfo> update(@PathVariable Long id,
                                             @Valid @RequestBody AgreementEditRequest request) {
        return ApiResponse.ok(service.updateAgreement(id, request));
    }

    /**
     * 完成协议交接（管理员权限）
     * 认养申请通过终审后，管理员执行交接操作，完成猫咪交付
     * @param id 协议ID
     * @param request 交接请求（包含交接时间、地点等信息）
     */
    @PutMapping("/{id}/handover")
    @RequireRole(Role.ADMIN)
    public ApiResponse<AgreementInfo> handover(@PathVariable Long id,
                                               @Valid @RequestBody AgreementHandoverRequest request) {
        return ApiResponse.ok(service.completeHandover(id, request));
    }

    /**
     * 取消协议（管理员权限）
     * 将协议状态改为已取消，需要填写取消原因
     * @param id 协议ID
     * @param request 操作请求（包含取消原因）
     */
    @PutMapping("/{id}/cancel")
    @RequireRole(Role.ADMIN)
    public ApiResponse<AgreementInfo> cancel(@PathVariable Long id,
                                             @Valid @RequestBody ActionReasonRequest request) {
        return ApiResponse.ok(service.cancelAgreement(id, request));
    }

    /**
     * 删除协议（管理员权限）
     * 物理删除协议记录，需要填写删除原因（谨慎使用）
     * @param id 协议ID
     * @param request 操作请求（包含删除原因）
     */
    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> delete(@PathVariable Long id,
                                       @Valid @RequestBody ActionReasonRequest request) {
        service.deleteAgreement(id, request);
        return ApiResponse.ok(true);
    }
}