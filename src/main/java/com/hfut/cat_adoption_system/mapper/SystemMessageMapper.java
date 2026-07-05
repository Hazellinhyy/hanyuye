package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.dto.SystemMessageInfo;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SystemMessageMapper {
    @Insert("""
            INSERT INTO system_message
            (user_id, receiver_id, message_type, title, content, related_type, related_id,
             biz_type, biz_id, read_flag, read_status, status, deleted, create_by, update_by,
             create_time, update_time)
            VALUES
            (#{receiverId}, #{receiverId}, #{bizType}, #{title}, #{content}, #{bizType}, #{bizId},
             #{bizType}, #{bizId}, 0, 'UNREAD', 'VALID', 0, #{senderId}, #{senderId},
             #{createdAt}, #{createdAt})
            """)
    int insert(@Param("receiverId") String receiverId,
               @Param("title") String title,
               @Param("content") String content,
               @Param("bizType") String bizType,
               @Param("bizId") String bizId,
               @Param("senderId") String senderId,
               @Param("createdAt") LocalDateTime createdAt);

    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "receiver_id", javaType = String.class),
            @Arg(column = "receiver_name", javaType = String.class),
            @Arg(column = "title", javaType = String.class),
            @Arg(column = "content", javaType = String.class),
            @Arg(column = "biz_type", javaType = String.class),
            @Arg(column = "biz_id", javaType = String.class),
            @Arg(column = "read_status", javaType = String.class),
            @Arg(column = "create_time", javaType = LocalDateTime.class)
    })
    @Select("""
            <script>
            SELECT sm.id, sm.receiver_id, u.user_name AS receiver_name,
                   sm.title, sm.content,
                   sm.biz_type,
                   sm.biz_id,
                   sm.read_status,
                   sm.create_time
            FROM system_message sm
            LEFT JOIN t_user u ON u.user_id = sm.receiver_id
            WHERE COALESCE(sm.deleted, 0) = 0
            <if test="receiverId != null and receiverId != ''">AND sm.receiver_id = #{receiverId}</if>
            <if test="readStatus != null and readStatus != ''">AND sm.read_status = #{readStatus}</if>
            <if test="bizType != null and bizType != ''">AND sm.biz_type = #{bizType}</if>
            ORDER BY sm.create_time DESC, sm.id DESC
            </script>
            """)
    List<SystemMessageInfo> findMessages(@Param("receiverId") String receiverId,
                                         @Param("readStatus") String readStatus,
                                         @Param("bizType") String bizType);

    @Select("""
            SELECT COUNT(*) FROM system_message
            WHERE receiver_id = #{receiverId}
              AND COALESCE(deleted, 0) = 0
              AND read_status = 'UNREAD'
            """)
    long countUnread(String receiverId);

    @Update("""
            UPDATE system_message
            SET read_status = 'READ', read_time = #{readAt}, update_time = #{readAt}
            WHERE id = #{id} AND receiver_id = #{receiverId} AND COALESCE(deleted, 0) = 0
            """)
    int markRead(@Param("id") Long id, @Param("receiverId") String receiverId, @Param("readAt") LocalDateTime readAt);

    @Update("""
            UPDATE system_message
            SET read_status = 'READ', read_time = #{readAt}, update_time = #{readAt}
            WHERE receiver_id = #{receiverId} AND COALESCE(deleted, 0) = 0
            """)
    int markAllRead(@Param("receiverId") String receiverId, @Param("readAt") LocalDateTime readAt);
}
