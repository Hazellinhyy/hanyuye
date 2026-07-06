package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.MedicalRecordRequest;
import com.hfut.cat_adoption_system.model.MedicalRecord;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 医疗记录控制器
 * 
 * 提供猫咪医疗记录的管理功能：
 * - 查询医疗记录列表（志愿者/医院/管理员权限）
 * - 添加医疗记录（医院/管理员权限）
 */
@RestController
@RequestMapping("/api/medical-records")
public class MedicalController {

    /** 认养服务：处理医疗记录相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public MedicalController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询医疗记录列表（志愿者/医院/管理员权限）
     * 
     * @param catId 猫咪ID筛选（可选）
     */
    @GetMapping
    @RequireRole({ Role.VOLUNTEER, Role.HOSPITAL, Role.ADMIN })
    public ApiResponse<List<MedicalRecord>> listMedicalRecords(@RequestParam(required = false) String catId) {
        return ApiResponse.ok(service.listMedicalRecords(catId));
    }

    /**
     * 添加医疗记录（医院/管理员权限）
     * 
     * @param request 医疗记录请求（包含猫咪ID、就诊日期、诊断结果、治疗方案等）
     */
    @PostMapping
    @RequireRole({ Role.HOSPITAL, Role.ADMIN })
    public ApiResponse<MedicalRecord> addMedicalRecord(@Valid @RequestBody MedicalRecordRequest request) {
        return ApiResponse.created(service.addMedicalRecord(request));
    }
}