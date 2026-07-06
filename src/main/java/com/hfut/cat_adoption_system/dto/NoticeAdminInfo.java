package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

/**
 * 通知管理信息DTO
 * 
 * 用于封装管理员端展示的通知信息，包含通知的基本属性、发布状态、发布者信息等，
 * 支持管理员对通知进行管理和展示。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record NoticeAdminInfo(
                /** 通知ID */
                String id,

                /** 通知标题 */
                String title,

                /** 通知内容 */
                String content,

                /** 通知类型（如系统公告、活动通知、紧急通知等） */
                String noticeType,

                /** 发布状态（如草稿、已发布、已下架等） */
                String publishStatus,

                /** 发布者ID */
                String publisherId,

                /** 发布者姓名 */
                String publisherName,

                /** 发布时间 */
                LocalDateTime publishTime,

                /** 排序序号 */
                Integer sortOrder,

                /** 通知封面图片URL */
                String imageUrl,

                /** 目标角色（指定哪些角色可以查看该通知） */
                String targetRoles,

                /** 创建时间 */
                LocalDateTime createTime,

                /** 更新时间 */
                LocalDateTime updateTime) {
}