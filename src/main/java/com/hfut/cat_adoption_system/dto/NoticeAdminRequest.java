package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 通知管理请求DTO
 * 
 * 用于接收管理员创建或更新通知的请求参数，
 * 通过Jakarta Validation注解实现表单校验。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record NoticeAdminRequest(
                /** 通知标题（必填） */
                @NotBlank(message = "通知标题不能为空") String title,

                /** 通知内容（必填） */
                @NotBlank(message = "通知内容不能为空") String content,

                /** 通知类型（如系统公告、活动通知、紧急通知等） */
                String noticeType,

                /** 发布状态（如草稿、已发布、已下架等） */
                String publishStatus,

                /** 排序序号 */
                Integer sortOrder,

                /** 通知封面图片URL */
                String imageUrl,

                /** 目标角色（指定哪些角色可以查看该通知） */
                String targetRoles,

                /** 是否同时发送消息通知 */
                Boolean sendMessage) {
}