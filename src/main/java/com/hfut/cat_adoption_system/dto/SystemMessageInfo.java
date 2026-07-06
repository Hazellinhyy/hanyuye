package com.hfut.cat_adoption_system.dto;

import java.time.LocalDateTime;

/**
 * 系统消息信息DTO
 * 
 * 用于封装系统消息的详细信息，包含消息接收者、标题、内容、业务类型等，
 * 支持用户查看和管理系统消息。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record SystemMessageInfo(
                /** 消息ID */
                Long id,

                /** 接收者ID */
                String receiverId,

                /** 接收者姓名 */
                String receiverName,

                /** 消息标题 */
                String title,

                /** 消息内容 */
                String content,

                /** 业务类型（如审核通知、领养通知、系统公告等） */
                String bizType,

                /** 业务ID（关联业务对象的唯一标识） */
                String bizId,

                /** 阅读状态（如未读、已读等） */
                String readStatus,

                /** 创建时间 */
                LocalDateTime createTime) {
}