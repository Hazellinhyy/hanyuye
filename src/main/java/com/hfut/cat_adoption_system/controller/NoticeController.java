package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.PublicApi;
import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.NoticeRequest;
import com.hfut.cat_adoption_system.model.Notice;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公告管理控制器
 * 
 * 提供公告的管理功能：
 * - 查询公告列表（公开接口）
 * - 查询公告详情（公开接口）
 * - 创建公告（管理员权限）
 * - 更新公告（管理员权限）
 * - 启用/禁用公告（管理员权限）
 * - 置顶/取消置顶公告（管理员权限）
 * - 删除公告（管理员权限）
 */
@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    /** 认养服务：处理公告相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public NoticeController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询公告列表（公开接口）
     * 
     * @param includeDisabled 是否包含已禁用公告（默认false）
     */
    @GetMapping
    @PublicApi
    public ApiResponse<List<Notice>> listNotices(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "false") boolean includeDisabled) {
        return ApiResponse.ok(service.listNotices(includeDisabled));
    }

    /**
     * 查询公告详情（公开接口）
     * 
     * @param noticeId 公告ID
     */
    @GetMapping("/{noticeId}")
    @PublicApi
    public ApiResponse<Notice> getNotice(@PathVariable String noticeId) {
        return ApiResponse.ok(service.getNotice(noticeId));
    }

    /**
     * 创建公告（管理员权限）
     * 
     * @param request 公告创建请求（包含标题、内容等）
     */
    @PostMapping
    @RequireRole(Role.ADMIN)
    public ApiResponse<Notice> createNotice(@Valid @RequestBody NoticeRequest request) {
        return ApiResponse.created(service.createNotice(request));
    }

    /**
     * 更新公告（管理员权限）
     * 
     * @param noticeId 公告ID
     * @param request  公告更新请求
     */
    @PutMapping("/{noticeId}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Notice> updateNotice(@PathVariable String noticeId, @Valid @RequestBody NoticeRequest request) {
        return ApiResponse.ok(service.updateNotice(noticeId, request));
    }

    /**
     * 启用/禁用公告（管理员权限）
     * 
     * @param noticeId 公告ID
     * @param enabled  是否启用
     */
    @PatchMapping("/{noticeId}/enabled")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Notice> updateEnabled(@PathVariable String noticeId,
            @org.springframework.web.bind.annotation.RequestParam boolean enabled) {
        return ApiResponse.ok(service.updateNoticeEnabled(noticeId, enabled));
    }

    /**
     * 置顶/取消置顶公告（管理员权限）
     * 
     * @param noticeId 公告ID
     * @param pinned   是否置顶
     */
    @PatchMapping("/{noticeId}/pinned")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Notice> updatePinned(@PathVariable String noticeId,
            @org.springframework.web.bind.annotation.RequestParam boolean pinned) {
        return ApiResponse.ok(service.updateNoticePinned(noticeId, pinned));
    }

    /**
     * 删除公告（管理员权限）
     * 
     * @param noticeId 公告ID
     */
    @DeleteMapping("/{noticeId}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> deleteNotice(@PathVariable String noticeId) {
        service.deleteNotice(noticeId);
        return ApiResponse.ok(true);
    }
}