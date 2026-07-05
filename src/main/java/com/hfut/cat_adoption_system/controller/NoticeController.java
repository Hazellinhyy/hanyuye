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

@RestController
@RequestMapping("/api/notices")
public class NoticeController {
    private final CatAdoptionService service;

    public NoticeController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    @PublicApi
    public ApiResponse<List<Notice>> listNotices(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "false") boolean includeDisabled) {
        return ApiResponse.ok(service.listNotices(includeDisabled));
    }

    @GetMapping("/{noticeId}")
    @PublicApi
    public ApiResponse<Notice> getNotice(@PathVariable String noticeId) {
        return ApiResponse.ok(service.getNotice(noticeId));
    }

    @PostMapping
    @RequireRole(Role.ADMIN)
    public ApiResponse<Notice> createNotice(@Valid @RequestBody NoticeRequest request) {
        return ApiResponse.created(service.createNotice(request));
    }

    @PutMapping("/{noticeId}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Notice> updateNotice(@PathVariable String noticeId, @Valid @RequestBody NoticeRequest request) {
        return ApiResponse.ok(service.updateNotice(noticeId, request));
    }

    @PatchMapping("/{noticeId}/enabled")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Notice> updateEnabled(@PathVariable String noticeId, @org.springframework.web.bind.annotation.RequestParam boolean enabled) {
        return ApiResponse.ok(service.updateNoticeEnabled(noticeId, enabled));
    }

    @PatchMapping("/{noticeId}/pinned")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Notice> updatePinned(@PathVariable String noticeId, @org.springframework.web.bind.annotation.RequestParam boolean pinned) {
        return ApiResponse.ok(service.updateNoticePinned(noticeId, pinned));
    }

    @DeleteMapping("/{noticeId}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> deleteNotice(@PathVariable String noticeId) {
        service.deleteNotice(noticeId);
        return ApiResponse.ok(true);
    }
}
