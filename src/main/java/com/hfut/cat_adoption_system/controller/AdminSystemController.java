package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.PublicApi;
import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.*;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class AdminSystemController {
    private final CatAdoptionService service;

    public AdminSystemController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping("/api/dicts/{dictType}")
    @PublicApi
    public ApiResponse<List<DictItemInfo>> publicDict(@PathVariable String dictType) {
        return ApiResponse.ok(service.listDictItems(dictType, true));
    }

    @GetMapping("/api/admin/dicts")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<DictItemInfo>> adminDicts(@RequestParam(required = false) String dictType) {
        return ApiResponse.ok(service.listDictItems(dictType, false));
    }

    @PostMapping("/api/admin/dicts")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> createDict(@Valid @RequestBody DictItemRequest request) {
        service.createDictItem(request);
        return ApiResponse.created(true);
    }

    @PutMapping("/api/admin/dicts/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> updateDict(@PathVariable Long id, @Valid @RequestBody DictItemRequest request) {
        service.updateDictItem(id, request);
        return ApiResponse.ok(true);
    }

    @PutMapping("/api/admin/dicts/{id}/enabled")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> updateDictEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        service.updateDictEnabled(id, enabled);
        return ApiResponse.ok(true);
    }

    @DeleteMapping("/api/admin/dicts/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> deleteDict(@PathVariable Long id) {
        service.deleteDictItem(id);
        return ApiResponse.ok(true);
    }

    @GetMapping("/api/admin/logs")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<OperationLogInfo>> logs(@RequestParam(required = false) String operatorKeyword,
                                                    @RequestParam(required = false) String operationType,
                                                    @RequestParam(required = false) String bizType,
                                                    @RequestParam(required = false) LocalDateTime startTime,
                                                    @RequestParam(required = false) LocalDateTime endTime,
                                                    @RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) Integer page,
                                                    @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(service.listOperationLogs(operatorKeyword, operationType, bizType, startTime, endTime, keyword, page, size));
    }

    @GetMapping("/api/admin/logs/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<OperationLogInfo> logDetail(@PathVariable Long id) {
        return ApiResponse.ok(service.getOperationLog(id));
    }

    @GetMapping("/api/admin/search")
    @RequireRole({Role.VOLUNTEER, Role.HOSPITAL, Role.ADMIN})
    public ApiResponse<List<AdminSearchGroup>> search(@RequestParam String keyword) {
        return ApiResponse.ok(service.adminSearch(keyword));
    }

    @GetMapping("/api/admin/notices")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<NoticeAdminInfo>> adminNotices(@RequestParam(required = false) String publishStatus,
                                                           @RequestParam(required = false) String noticeType) {
        return ApiResponse.ok(service.listAdminNotices(publishStatus, noticeType));
    }

    @PostMapping("/api/admin/notices")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> createNotice(@Valid @RequestBody NoticeAdminRequest request) {
        service.createAdminNotice(request);
        return ApiResponse.created(true);
    }

    @PutMapping("/api/admin/notices/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> updateNotice(@PathVariable String id, @Valid @RequestBody NoticeAdminRequest request) {
        service.updateAdminNotice(id, request);
        return ApiResponse.ok(true);
    }

    @PutMapping("/api/admin/notices/{id}/publish")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> publishNotice(@PathVariable String id) {
        service.publishNotice(id);
        return ApiResponse.ok(true);
    }

    @PutMapping("/api/admin/notices/{id}/offline")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> offlineNotice(@PathVariable String id) {
        service.offlineNotice(id);
        return ApiResponse.ok(true);
    }

    @DeleteMapping("/api/admin/notices/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> deleteNotice(@PathVariable String id) {
        service.deleteAdminNotice(id);
        return ApiResponse.ok(true);
    }
}
