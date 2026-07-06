package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 通知请求DTO
 * 
 * 用于接收创建或更新通知的请求参数，适用于普通用户端或简化版通知管理。
 * 通过Jakarta Validation注解实现表单校验。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record NoticeRequest(
                /** 通知标题（必填） */
                @NotBlank(message = "通知标题不能为空") String title,

                /** 通知内容（必填） */
                @NotBlank(message = "通知内容不能为空") String content,

                /** 发布者名称（必填） */
                @NotBlank(message = "发布者不能为空") String publisher,

                /** 是否置顶显示 */
                boolean pinned,

                /** 是否启用（是否在前端展示） */
                boolean enabled) {
}