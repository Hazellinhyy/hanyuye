package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.SystemMessageInfo;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统消息控制器
 * 
 * 提供系统消息的管理功能，分为用户端和管理员端：
 * 
 * 【用户端接口】（登录用户）：
 * - 查询我的消息列表
 * - 查询未读消息数量
 * - 标记单条消息已读
 * - 标记所有消息已读
 * 
 * 【管理员端接口】（管理员权限）：
 * - 查询所有消息列表（支持按接收人、业务类型、阅读状态筛选）
 */
@RestController
public class SystemMessageController {

    /** 认养服务：处理系统消息相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public SystemMessageController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询我的消息列表（登录用户）
     * 
     * @param readStatus 阅读状态筛选（可选）
     */
    @GetMapping("/api/user/messages")
    public ApiResponse<List<SystemMessageInfo>> myMessages(@RequestParam(required = false) String readStatus) {
        return ApiResponse.ok(service.listMyMessages(readStatus));
    }

    /**
     * 查询未读消息数量（登录用户）
     * 返回当前用户的未读消息总数
     */
    @GetMapping("/api/user/messages/unread-count")
    public ApiResponse<Long> unreadCount() {
        return ApiResponse.ok(service.unreadMessageCount());
    }

    /**
     * 标记单条消息已读（登录用户）
     * 
     * @param id 消息ID
     */
    @PutMapping("/api/user/messages/{id}/read")
    public ApiResponse<Boolean> markRead(@PathVariable Long id) {
        service.markMessageRead(id);
        return ApiResponse.ok(true);
    }

    /**
     * 标记所有消息已读（登录用户）
     * 将当前用户的所有未读消息标记为已读
     */
    @PutMapping("/api/user/messages/read-all")
    public ApiResponse<Boolean> markAllRead() {
        service.markAllMessagesRead();
        return ApiResponse.ok(true);
    }

    /**
     * 查询所有消息列表（管理员权限）
     * 
     * @param receiverId 接收人ID筛选（可选）
     * @param bizType    业务类型筛选（可选）
     * @param readStatus 阅读状态筛选（可选）
     */
    @GetMapping("/api/admin/messages")
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<SystemMessageInfo>> adminMessages(@RequestParam(required = false) String receiverId,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) String readStatus) {
        return ApiResponse.ok(service.listAdminMessages(receiverId, bizType, readStatus));
    }
}