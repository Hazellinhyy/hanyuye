package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ReportReviewRequest;
import com.hfut.cat_adoption_system.dto.RescueReportRequest;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.RescueReport;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 救援报告控制器
 * 
 * 提供流浪猫救援报告的管理功能：
 * - 查询救援报告列表（志愿者/管理员权限）
 * - 提交救援报告（登录用户）
 * - 审核救援报告并创建猫咪档案（志愿者/管理员权限）
 */
@RestController
@RequestMapping("/api/reports")
public class RescueController {

    /** 认养服务：处理救援报告相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public RescueController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询救援报告列表（志愿者/管理员权限）
     * 返回所有救援报告供审核
     */
    @GetMapping
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<List<RescueReport>> listReports() {
        return ApiResponse.ok(service.listReports());
    }

    /**
     * 提交救援报告（登录用户）
     * 用户发现流浪猫后可提交救援报告，包含猫咪位置、状况等信息
     * 
     * @param request 救援报告请求（包含位置、描述、照片等）
     */
    @PostMapping
    public ApiResponse<RescueReport> submitReport(@Valid @RequestBody RescueReportRequest request) {
        return ApiResponse.created(service.submitReport(request));
    }

    /**
     * 审核救援报告并创建猫咪档案（志愿者/管理员权限）
     * 审核通过后自动创建猫咪档案，进入领养流程
     * 
     * @param reportId 救援报告ID
     * @param request  审核请求（包含审核结果和猫咪信息）
     * @return Cat 创建成功的猫咪档案
     */
    @PatchMapping("/{reportId}/review")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<Cat> reviewReport(@PathVariable String reportId,
            @Valid @RequestBody ReportReviewRequest request) {
        return ApiResponse.ok(service.reviewReport(reportId, request));
    }
}