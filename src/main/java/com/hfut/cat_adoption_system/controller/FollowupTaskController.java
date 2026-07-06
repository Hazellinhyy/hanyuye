package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.FollowupAbnormalRequest;
import com.hfut.cat_adoption_system.dto.FollowupRecordInfo;
import com.hfut.cat_adoption_system.dto.FollowupRecordSubmitRequest;
import com.hfut.cat_adoption_system.dto.FollowupRefreshResult;
import com.hfut.cat_adoption_system.dto.FollowupTaskInfo;
import com.hfut.cat_adoption_system.model.FollowupTaskStatus;
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

import java.time.LocalDate;
import java.util.List;

/**
 * 回访任务控制器
 * 
 * 提供回访任务的管理功能，分为学生端和管理员端：
 * 
 * 【学生端接口】（学生角色）：
 * - 查询我的回访任务列表
 * - 查询回访任务详情
 * - 提交回访记录
 * - 查询我的回访记录列表
 * 
 * 【管理员端接口】（志愿者/管理员角色）：
 * - 查询所有回访任务列表
 * - 查询回访任务详情
 * - 查询回访记录列表
 * - 管理员提交回访记录
 * - 标记回访异常
 * - 刷新逾期任务
 */
@RestController
public class FollowupTaskController {

    /** 认养服务：处理回访任务相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public FollowupTaskController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询我的回访任务列表（学生权限）
     * 
     * @param status 任务状态筛选（可选）
     */
    @GetMapping("/api/my/followup/tasks")
    @RequireRole(Role.STUDENT)
    public ApiResponse<List<FollowupTaskInfo>> myTasks(@RequestParam(required = false) FollowupTaskStatus status) {
        return ApiResponse.ok(service.listMyFollowupTasks(status));
    }

    /**
     * 查询回访任务详情（学生权限）
     * 
     * @param id 任务ID
     */
    @GetMapping("/api/my/followup/tasks/{id}")
    @RequireRole(Role.STUDENT)
    public ApiResponse<FollowupTaskInfo> myTask(@PathVariable Long id) {
        return ApiResponse.ok(service.getMyFollowupTask(id));
    }

    /**
     * 提交回访记录（学生权限）
     * 学生完成回访后提交回访记录，记录猫咪的生活状况
     * 
     * @param id      任务ID
     * @param request 回访记录提交请求（包含照片、描述等）
     */
    @PostMapping("/api/my/followup/tasks/{id}/records")
    @RequireRole(Role.STUDENT)
    public ApiResponse<FollowupTaskInfo> submitRecord(@PathVariable Long id,
            @Valid @RequestBody FollowupRecordSubmitRequest request) {
        return ApiResponse.created(service.submitFollowupRecord(id, request));
    }

    /**
     * 查询我的回访记录列表（学生权限）
     * 
     * @param id 任务ID
     */
    @GetMapping("/api/my/followup/tasks/{id}/records")
    @RequireRole(Role.STUDENT)
    public ApiResponse<List<FollowupRecordInfo>> myRecords(@PathVariable Long id) {
        return ApiResponse.ok(service.listMyFollowupRecords(id));
    }

    /**
     * 查询所有回访任务列表（志愿者/管理员权限）
     * 
     * @param status   任务状态筛选（可选）
     * @param taskType 任务类型筛选（可选）
     * @param planDate 计划日期筛选（可选）
     * @param keyword  关键词搜索（可选）
     */
    @GetMapping("/api/admin/followup/tasks")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<List<FollowupTaskInfo>> adminTasks(@RequestParam(required = false) FollowupTaskStatus status,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) LocalDate planDate,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.listAdminFollowupTasks(status, taskType, planDate, keyword));
    }

    /**
     * 查询回访任务详情（志愿者/管理员权限）
     * 
     * @param id 任务ID
     */
    @GetMapping("/api/admin/followup/tasks/{id}")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<FollowupTaskInfo> adminTask(@PathVariable Long id) {
        return ApiResponse.ok(service.getAdminFollowupTask(id));
    }

    /**
     * 查询回访记录列表（志愿者/管理员权限）
     * 
     * @param id 任务ID
     */
    @GetMapping("/api/admin/followup/tasks/{id}/records")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<List<FollowupRecordInfo>> adminRecords(@PathVariable Long id) {
        return ApiResponse.ok(service.listAdminFollowupRecords(id));
    }

    /**
     * 管理员提交回访记录（志愿者/管理员权限）
     * 管理员或志愿者可直接为回访任务添加记录
     * 
     * @param id      任务ID
     * @param request 回访记录提交请求
     */
    @PostMapping("/api/admin/followup/tasks/{id}/records")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<FollowupTaskInfo> submitAdminRecord(@PathVariable Long id,
            @Valid @RequestBody FollowupRecordSubmitRequest request) {
        return ApiResponse.created(service.submitAdminFollowupRecord(id, request));
    }

    /**
     * 标记回访异常（志愿者/管理员权限）
     * 当回访情况异常时，管理员可标记异常并填写异常描述和处理意见
     * 
     * @param id      任务ID
     * @param request 异常标记请求（包含异常描述和志愿者备注）
     */
    @PutMapping("/api/admin/followup/tasks/{id}/mark-abnormal")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<FollowupTaskInfo> markAbnormal(@PathVariable Long id,
            @Valid @RequestBody FollowupAbnormalRequest request) {
        return ApiResponse.ok(service.markFollowupAbnormal(id, request.abnormalDesc(), request.volunteerComment()));
    }

    /**
     * 刷新逾期任务（管理员权限）
     * 自动检测并更新逾期的回访任务状态
     */
    @PostMapping("/api/admin/followup/tasks/refresh-overdue")
    @RequireRole(Role.ADMIN)
    public ApiResponse<FollowupRefreshResult> refreshOverdue() {
        return ApiResponse.ok(service.refreshOverdueTasks());
    }
}