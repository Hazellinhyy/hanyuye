package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ActionReasonRequest;
import com.hfut.cat_adoption_system.dto.AdminMedicalRecordRequest;
import com.hfut.cat_adoption_system.model.MedicalRecord;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员医疗记录管理控制器
 * 
 * 提供医疗记录的管理功能，包括：
 * - 查看医疗记录详情
 * - 更新医疗记录信息
 * - 作废医疗记录
 * 
 * 类级别权限：医院用户( HOSPITAL )和管理员( ADMIN )均可访问
 */
@RestController
@RequestMapping("/api/admin/medical-records")
@RequireRole({ Role.HOSPITAL, Role.ADMIN })
public class AdminMedicalRecordController {

    /** 认养服务：处理医疗记录相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdminMedicalRecordController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询医疗记录详情
     * 
     * @param id 医疗记录ID
     */
    @GetMapping("/{id}")
    public ApiResponse<MedicalRecord> detail(@PathVariable String id) {
        return ApiResponse.ok(service.getMedicalRecord(id));
    }

    /**
     * 更新医疗记录信息
     * 
     * @param id      医疗记录ID
     * @param request 更新请求（包含就诊日期、诊断结果、治疗方案等）
     */
    @PutMapping("/{id}")
    public ApiResponse<MedicalRecord> update(@PathVariable String id,
            @Valid @RequestBody AdminMedicalRecordRequest request) {
        return ApiResponse.ok(service.updateAdminMedicalRecord(id, request));
    }

    /**
     * 作废医疗记录
     * 将医疗记录标记为作废状态，需要填写作废原因
     * 
     * @param id      医疗记录ID
     * @param request 操作请求（包含作废原因）
     */
    @PutMapping("/{id}/void")
    public ApiResponse<Boolean> voidRecord(@PathVariable String id,
            @Valid @RequestBody ActionReasonRequest request) {
        service.voidAdminMedicalRecord(id, request);
        return ApiResponse.ok(true);
    }
}