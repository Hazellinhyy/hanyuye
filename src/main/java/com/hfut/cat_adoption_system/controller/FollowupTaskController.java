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

@RestController
public class FollowupTaskController {
    private final CatAdoptionService service;

    public FollowupTaskController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping("/api/my/followup/tasks")
    @RequireRole(Role.STUDENT)
    public ApiResponse<List<FollowupTaskInfo>> myTasks(@RequestParam(required = false) FollowupTaskStatus status) {
        return ApiResponse.ok(service.listMyFollowupTasks(status));
    }

    @GetMapping("/api/my/followup/tasks/{id}")
    @RequireRole(Role.STUDENT)
    public ApiResponse<FollowupTaskInfo> myTask(@PathVariable Long id) {
        return ApiResponse.ok(service.getMyFollowupTask(id));
    }

    @PostMapping("/api/my/followup/tasks/{id}/records")
    @RequireRole(Role.STUDENT)
    public ApiResponse<FollowupTaskInfo> submitRecord(@PathVariable Long id,
                                                       @Valid @RequestBody FollowupRecordSubmitRequest request) {
        return ApiResponse.created(service.submitFollowupRecord(id, request));
    }

    @GetMapping("/api/my/followup/tasks/{id}/records")
    @RequireRole(Role.STUDENT)
    public ApiResponse<List<FollowupRecordInfo>> myRecords(@PathVariable Long id) {
        return ApiResponse.ok(service.listMyFollowupRecords(id));
    }

    @GetMapping("/api/admin/followup/tasks")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<List<FollowupTaskInfo>> adminTasks(@RequestParam(required = false) FollowupTaskStatus status,
                                                          @RequestParam(required = false) String taskType,
                                                          @RequestParam(required = false) LocalDate planDate,
                                                          @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.listAdminFollowupTasks(status, taskType, planDate, keyword));
    }

    @GetMapping("/api/admin/followup/tasks/{id}")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<FollowupTaskInfo> adminTask(@PathVariable Long id) {
        return ApiResponse.ok(service.getAdminFollowupTask(id));
    }

    @GetMapping("/api/admin/followup/tasks/{id}/records")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<List<FollowupRecordInfo>> adminRecords(@PathVariable Long id) {
        return ApiResponse.ok(service.listAdminFollowupRecords(id));
    }

    @PostMapping("/api/admin/followup/tasks/{id}/records")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<FollowupTaskInfo> submitAdminRecord(@PathVariable Long id,
                                                           @Valid @RequestBody FollowupRecordSubmitRequest request) {
        return ApiResponse.created(service.submitAdminFollowupRecord(id, request));
    }

    @PutMapping("/api/admin/followup/tasks/{id}/mark-abnormal")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<FollowupTaskInfo> markAbnormal(@PathVariable Long id,
                                                      @Valid @RequestBody FollowupAbnormalRequest request) {
        return ApiResponse.ok(service.markFollowupAbnormal(id, request.abnormalDesc(), request.volunteerComment()));
    }

    @PostMapping("/api/admin/followup/tasks/refresh-overdue")
    @RequireRole(Role.ADMIN)
    public ApiResponse<FollowupRefreshResult> refreshOverdue() {
        return ApiResponse.ok(service.refreshOverdueTasks());
    }
}
