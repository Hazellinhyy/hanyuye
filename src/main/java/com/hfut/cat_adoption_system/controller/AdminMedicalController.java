package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.AdminMedicalRecordRequest;
import com.hfut.cat_adoption_system.model.MedicalRecord;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员猫咪医疗记录管理控制器
 * 
 * 提供猫咪医疗记录的管理功能，包括：
 * - 查询指定猫咪的医疗记录列表
 * - 添加猫咪医疗记录（医院/管理员权限）
 * 
 * 路径参数 {catId} 指定猫咪ID
 */
@RestController
@RequestMapping("/api/admin/cats/{catId}/medical-records")
public class AdminMedicalController {

    /** 认养服务：处理医疗记录相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdminMedicalController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询猫咪医疗记录列表
     * 
     * @param catId 猫咪ID（路径参数）
     */
    @GetMapping
    @RequireRole({ Role.VOLUNTEER, Role.HOSPITAL, Role.ADMIN })
    public ApiResponse<List<MedicalRecord>> records(@PathVariable String catId) {
        return ApiResponse.ok(service.listMedicalRecords(catId));
    }

    /**
     * 添加猫咪医疗记录（医院/管理员权限）
     * 
     * @param catId   猫咪ID（路径参数）
     * @param request 医疗记录请求（包含就诊日期、诊断结果、治疗方案等）
     */
    @PostMapping
    @RequireRole({ Role.HOSPITAL, Role.ADMIN })
    public ApiResponse<MedicalRecord> addRecord(@PathVariable String catId,
            @Valid @RequestBody AdminMedicalRecordRequest request) {
        return ApiResponse.created(service.addAdminMedicalRecord(catId, request));
    }
}
