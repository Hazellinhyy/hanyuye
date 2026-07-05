package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.SystemMessageInfo;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SystemMessageController {
    private final CatAdoptionService service;

    public SystemMessageController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping("/api/user/messages")
    public ApiResponse<List<SystemMessageInfo>> myMessages(@RequestParam(required = false) String readStatus) {
        return ApiResponse.ok(service.listMyMessages(readStatus));
    }

    @GetMapping("/api/user/messages/unread-count")
    public ApiResponse<Long> unreadCount() {
        return ApiResponse.ok(service.unreadMessageCount());
    }

    @PutMapping("/api/user/messages/{id}/read")
    public ApiResponse<Boolean> markRead(@PathVariable Long id) {
        service.markMessageRead(id);
        return ApiResponse.ok(true);
    }

    @PutMapping("/api/user/messages/read-all")
    public ApiResponse<Boolean> markAllRead() {
        service.markAllMessagesRead();
        return ApiResponse.ok(true);
    }

    @GetMapping("/api/admin/messages")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<SystemMessageInfo>> adminMessages(@RequestParam(required = false) String receiverId,
                                                              @RequestParam(required = false) String bizType,
                                                              @RequestParam(required = false) String readStatus) {
        return ApiResponse.ok(service.listAdminMessages(receiverId, bizType, readStatus));
    }
}
