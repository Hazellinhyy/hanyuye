package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.Notice;
import com.hfut.cat_adoption_system.dto.NoticeAdminInfo;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.time.LocalDateTime;

@Mapper
public interface NoticeMapper {
    @ConstructorArgs({
            @Arg(column = "notice_id", javaType = String.class),
            @Arg(column = "title", javaType = String.class),
            @Arg(column = "content", javaType = String.class),
            @Arg(column = "publisher", javaType = String.class),
            @Arg(column = "pinned", javaType = boolean.class),
            @Arg(column = "enabled", javaType = boolean.class),
            @Arg(column = "published_at", javaType = java.time.LocalDateTime.class)
    })
    @Select("""
            <script>
            SELECT n.notice_id, n.title, n.content, u.user_name AS publisher, n.pinned,
                   CASE WHEN n.publish_status = 'PUBLISHED' THEN 1 ELSE 0 END AS enabled,
                   n.publish_time AS published_at
            FROM t_notice n
            JOIN t_user u ON u.user_id = n.publisher_id
            WHERE COALESCE(n.deleted, 0) = 0
            <if test="enabledOnly">AND n.publish_status = 'PUBLISHED'</if>
            ORDER BY n.pinned DESC, n.publish_time DESC
            </script>
            """)
    List<Notice> findAll(@Param("enabledOnly") boolean enabledOnly);

    @ConstructorArgs({
            @Arg(column = "notice_id", javaType = String.class),
            @Arg(column = "title", javaType = String.class),
            @Arg(column = "content", javaType = String.class),
            @Arg(column = "publisher", javaType = String.class),
            @Arg(column = "pinned", javaType = boolean.class),
            @Arg(column = "enabled", javaType = boolean.class),
            @Arg(column = "published_at", javaType = java.time.LocalDateTime.class)
    })
    @Select("""
            SELECT n.notice_id, n.title, n.content, u.user_name AS publisher, n.pinned,
                   CASE WHEN n.publish_status = 'PUBLISHED' THEN 1 ELSE 0 END AS enabled,
                   n.publish_time AS published_at
            FROM t_notice n
            JOIN t_user u ON u.user_id = n.publisher_id
            WHERE n.notice_id = #{noticeId}
            """)
    Notice findById(String noticeId);

    @Insert("""
            INSERT INTO t_notice (notice_id, title, content, publisher_id, pinned, publish_status, publish_time,
                                  notice_type, sort_order, deleted, create_time, update_time)
            VALUES (#{noticeId}, #{title}, #{content},
                    COALESCE((SELECT user_id FROM t_user WHERE user_name = #{publisher} LIMIT 1), 'UDEMOADMIN'),
                    #{pinned}, CASE WHEN #{enabled} = 1 THEN 'PUBLISHED' ELSE 'OFFLINE' END, #{publishedAt},
                    'SYSTEM', 0, 0, #{publishedAt}, #{publishedAt})
            """)
    void insert(Notice notice);

    @Update("""
            UPDATE t_notice SET title = #{title}, content = #{content},
                publisher_id = COALESCE((SELECT user_id FROM t_user WHERE user_name = #{publisher} LIMIT 1), publisher_id),
                pinned = #{pinned}, publish_status = CASE WHEN #{enabled} = 1 THEN 'PUBLISHED' ELSE 'OFFLINE' END
            WHERE notice_id = #{noticeId}
            """)
    int update(Notice notice);

    @Update("UPDATE t_notice SET publish_status = CASE WHEN #{enabled} = 1 THEN 'PUBLISHED' ELSE 'OFFLINE' END WHERE notice_id = #{noticeId}")
    int updateEnabled(@Param("noticeId") String noticeId, @Param("enabled") boolean enabled);

    @Update("UPDATE t_notice SET pinned = #{pinned} WHERE notice_id = #{noticeId}")
    int updatePinned(@Param("noticeId") String noticeId, @Param("pinned") boolean pinned);

    @Delete("DELETE FROM t_notice WHERE notice_id = #{noticeId}")
    int delete(String noticeId);

    @ConstructorArgs({
            @Arg(column = "id", javaType = String.class),
            @Arg(column = "title", javaType = String.class),
            @Arg(column = "content", javaType = String.class),
            @Arg(column = "notice_type", javaType = String.class),
            @Arg(column = "publish_status", javaType = String.class),
            @Arg(column = "publisher_id", javaType = String.class),
            @Arg(column = "publisher_name", javaType = String.class),
            @Arg(column = "publish_time", javaType = LocalDateTime.class),
            @Arg(column = "sort_order", javaType = Integer.class),
            @Arg(column = "create_time", javaType = LocalDateTime.class),
            @Arg(column = "update_time", javaType = LocalDateTime.class)
    })
    @Select("""
            <script>
            SELECT notice_id AS id, title, content, COALESCE(notice_type, 'SYSTEM') AS notice_type,
                   CASE WHEN COALESCE(publish_status, '') &lt;&gt; '' THEN publish_status
                        ELSE 'DRAFT' END AS publish_status,
                   publisher_id, u.user_name AS publisher_name,
                   publish_time,
                   COALESCE(sort_order, 0) AS sort_order,
                   create_time,
                   update_time
            FROM t_notice n
            LEFT JOIN t_user u ON u.user_id = n.publisher_id
            WHERE COALESCE(n.deleted, 0) = 0
            <if test="publishStatus != null and publishStatus != ''">
              AND n.publish_status = #{publishStatus}
            </if>
            <if test="noticeType != null and noticeType != ''">AND COALESCE(n.notice_type, 'SYSTEM') = #{noticeType}</if>
            ORDER BY COALESCE(n.sort_order, 0) DESC, n.publish_time DESC
            </script>
            """)
    List<NoticeAdminInfo> findAdmin(@Param("publishStatus") String publishStatus, @Param("noticeType") String noticeType);

    @Update("""
            UPDATE t_notice
            SET notice_type = #{noticeType}, publish_status = #{publishStatus}, publisher_id = #{publisherId},
                sort_order = #{sortOrder},
                update_time = #{updatedAt}
            WHERE notice_id = #{noticeId}
            """)
    int updateAdminFields(@Param("noticeId") String noticeId,
                          @Param("noticeType") String noticeType,
                          @Param("publishStatus") String publishStatus,
                          @Param("publisherId") String publisherId,
                          @Param("sortOrder") Integer sortOrder,
                          @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE t_notice
            SET publish_status = #{publishStatus}, publish_time = #{publishTime}, update_time = #{updatedAt}
            WHERE notice_id = #{noticeId}
            """)
    int updatePublishStatus(@Param("noticeId") String noticeId,
                            @Param("publishStatus") String publishStatus,
                            @Param("enabled") boolean enabled,
                            @Param("publishTime") LocalDateTime publishTime,
                            @Param("updatedAt") LocalDateTime updatedAt);

    @Update("UPDATE t_notice SET deleted = 1, publish_status = 'OFFLINE', update_time = #{updatedAt} WHERE notice_id = #{noticeId}")
    int logicalDelete(@Param("noticeId") String noticeId, @Param("updatedAt") LocalDateTime updatedAt);
}
