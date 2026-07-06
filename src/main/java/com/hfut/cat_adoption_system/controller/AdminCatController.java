package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.CatRequest;
import com.hfut.cat_adoption_system.dto.CatStatusChangeRequest;
import com.hfut.cat_adoption_system.dto.CatTimelineEvent;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.CatStatus;
import com.hfut.cat_adoption_system.model.HealthLevel;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员猫咪管理控制器
 * 
 * 提供猫咪档案的管理功能，包括：
 * - 查询猫咪列表（支持状态、健康等级、性别、关键词筛选）
 * - 查看猫咪详情
 * - 查看猫咪时间线（记录猫咪的重要事件）
 * - 创建猫咪档案
 * - 更新猫咪信息
 * - 变更猫咪状态
 * - 删除猫咪档案（管理员权限）
 */
@RestController
@RequestMapping("/api/admin/cats")
public class AdminCatController {

    /** 认养服务：处理猫咪相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdminCatController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询猫咪列表（管理员/志愿者/医院用户视角）
     * 
     * @param status      猫咪状态筛选（可选）
     * @param healthLevel 健康等级筛选（可选）
     * @param gender      性别筛选（可选）
     * @param keyword     关键词搜索（可选）
     * @param page        页码（可选）
     * @param size        每页数量（可选）
     */
    @GetMapping
    @RequireRole({ Role.VOLUNTEER, Role.HOSPITAL, Role.ADMIN })
    public ApiResponse<List<Cat>> listCats(@RequestParam(required = false) CatStatus status,
            @RequestParam(required = false) HealthLevel healthLevel,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(service.listAdminCats(status, healthLevel, gender, keyword, page, size));
    }

    /**
     * 查询猫咪详情
     * 
     * @param id 猫咪ID
     */
    @GetMapping("/{id}")
    @RequireRole({ Role.VOLUNTEER, Role.HOSPITAL, Role.ADMIN })
    public ApiResponse<Cat> getCat(@PathVariable String id) {
        return ApiResponse.ok(service.getCat(id));
    }

    /**
     * 查询猫咪时间线
     * 获取猫咪的重要事件记录，包括：入院、医疗、认养、回访等关键节点
     * 
     * @param id 猫咪ID
     */
    @GetMapping("/{id}/timeline")
    @RequireRole({ Role.VOLUNTEER, Role.HOSPITAL, Role.ADMIN })
    public ApiResponse<List<CatTimelineEvent>> timeline(@PathVariable String id) {
        return ApiResponse.ok(service.catTimeline(id));
    }

    /**
     * 创建猫咪档案（志愿者/管理员权限）
     * 
     * @param request 创建请求（包含猫咪基本信息）
     */
    @PostMapping
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<Cat> createCat(@Valid @RequestBody CatRequest request) {
        return ApiResponse.created(service.saveCat(request));
    }

    /**
     * 更新猫咪信息（志愿者/管理员权限）
     * 
     * @param id      猫咪ID
     * @param request 更新请求（包含猫咪基本信息）
     */
    @PutMapping("/{id}")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<Cat> updateCat(@PathVariable String id, @Valid @RequestBody CatRequest request) {
        return ApiResponse.ok(service.updateCat(id, request));
    }

    /**
     * 变更猫咪状态（志愿者/管理员权限）
     * 状态变更包括：待领养、领养中、已领养、治疗中、观察中等
     * 
     * @param id      猫咪ID
     * @param request 状态变更请求（包含目标状态和变更原因）
     */
    @PutMapping("/{id}/status")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<Cat> changeStatus(@PathVariable String id,
            @Valid @RequestBody CatStatusChangeRequest request) {
        return ApiResponse.ok(service.changeCatStatus(id, request));
    }

    /**
     * 删除猫咪档案（管理员权限）
     * 
     * @param id 猫咪ID
     */
    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> deleteCat(@PathVariable String id) {
        service.deleteCat(id);
        return ApiResponse.ok(true);
    }
}