package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.HospitalDashboardSummary;
import com.hfut.cat_adoption_system.model.MedicalRecord;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 医院用户管理控制器
 * 
 * 提供医院用户专属的数据统计和医疗记录管理功能，包括：
 * - 获取医院仪表盘汇总数据
 * - 查询医疗记录列表
 * 
 * 类级别权限：医院用户( HOSPITAL )和管理员( ADMIN )均可访问
 */
@RestController
@RequestMapping("/api/admin/hospital")
@RequireRole({ Role.HOSPITAL, Role.ADMIN })
public class AdminHospitalController {

    /** 认养服务：处理医院相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdminHospitalController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 获取医院仪表盘汇总数据
     * 返回医院相关的统计信息，包括就诊猫咪数量、治疗中数量、待随访数量等
     */
    @GetMapping("/summary")
    public ApiResponse<HospitalDashboardSummary> summary() {
        return ApiResponse.ok(service.hospitalDashboardSummary());
    }

    /**
     * 查询医疗记录列表
     * 
     * @param limit 返回数量限制（可选，默认返回全部）
     */
    @GetMapping("/records")
    public ApiResponse<List<MedicalRecord>> records(@RequestParam(required = false) Integer limit) {
        return ApiResponse.ok(service.listHospitalMedicalRecords(limit));
    }
}
