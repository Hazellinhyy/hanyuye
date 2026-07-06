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
    /**
     * 前台公告列表查询。
     *
     * 公告主表只保存公告本身，发布范围从 notice_target_role 关联表聚合回来，
     * 这样既满足三范式，也保持前端 targetRoles 字段不变。
     */
    @ConstructorArgs({
            @Arg(column = "notice_id", javaType = String.class),
            @Arg(column = "title", javaType = String.class),
            @Arg(column = "content", javaType = String.class),
            @Arg(column = "publisher", javaType = String.class),
            @Arg(column = "pinned", javaType = boolean.class),
            @Arg(column = "enabled", javaType = boolean.class),
            @Arg(column = "published_at", javaType = java.time.LocalDateTime.class),
            @Arg(column = "image_url", javaType = String.class),
            @Arg(column = "target_roles", javaType = String.class)
    })
    @Select("""
            <script>
            SELECT n.notice_id, n.title, n.content, COALESCE(u.user_name, '系统') AS publisher, n.pinned,
                   CASE WHEN n.publish_status = 'PUBLISHED' THEN 1 ELSE 0 END AS enabled,
                   n.publish_time AS published_at,
                   n.image_url,
                   COALESCE(
                       (SELECT GROUP_CONCAT(ntr.role_code ORDER BY ntr.role_code SEPARATOR ',')
                        FROM notice_target_role ntr
                        WHERE ntr.notice_id = n.notice_id),
                       'STUDENT,VOLUNTEER,HOSPITAL,ADMIN'
                   ) AS target_roles
            FROM t_notice n
            LEFT JOIN t_user u ON u.user_id = n.publisher_id
            WHERE COALESCE(n.deleted, 0) = 0
            <if test="enabledOnly">AND n.publish_status = 'PUBLISHED'</if>
            ORDER BY n.pinned DESC, COALESCE(n.sort_order, 0) DESC, n.publish_time DESC
            </script>
            """)
    List<Notice> findAll(@Param("enabledOnly") boolean enabledOnly);

    /**
     * 公告详情查询。
     *
     * 通过用户表补出发布人姓名，通过角色关联表补出可见角色，返回结构与前端旧版本一致。
     */
    @ConstructorArgs({
            @Arg(column = "notice_id", javaType = String.class),
            @Arg(column = "title", javaType = String.class),
            @Arg(column = "content", javaType = String.class),
            @Arg(column = "publisher", javaType = String.class),
            @Arg(column = "pinned", javaType = boolean.class),
            @Arg(column = "enabled", javaType = boolean.class),
            @Arg(column = "published_at", javaType = java.time.LocalDateTime.class),
            @Arg(column = "image_url", javaType = String.class),
            @Arg(column = "target_roles", javaType = String.class)
    })
    @Select("""
            SELECT n.notice_id, n.title, n.content, COALESCE(u.user_name, '系统') AS publisher, n.pinned,
                   CASE WHEN n.publish_status = 'PUBLISHED' THEN 1 ELSE 0 END AS enabled,
                   n.publish_time AS published_at,
                   n.image_url,
                   COALESCE(
                       (SELECT GROUP_CONCAT(ntr.role_code ORDER BY ntr.role_code SEPARATOR ',')
                        FROM notice_target_role ntr
                        WHERE ntr.notice_id = n.notice_id),
                       'STUDENT,VOLUNTEER,HOSPITAL,ADMIN'
                   ) AS target_roles
            FROM t_notice n
            LEFT JOIN t_user u ON u.user_id = n.publisher_id
            WHERE n.notice_id = #{noticeId}
            """)
    Notice findById(String noticeId);

    /**
     * 创建公告主记录。
     *
     * 这里只写 t_notice 主表；角色范围由 Service 层另行写入 notice_target_role。
     */
    @Insert("""
            INSERT INTO t_notice (notice_id, title, content, publisher_id, pinned, publish_status, publish_time,
                                  notice_type, sort_order, image_url, deleted, create_time, update_time)
            VALUES (#{noticeId}, #{title}, #{content},
                    COALESCE((SELECT user_id FROM t_user WHERE user_name = #{publisher} LIMIT 1), 'UDEMOADMIN'),
                    #{pinned}, CASE WHEN #{enabled} = 1 THEN 'PUBLISHED' ELSE 'OFFLINE' END, #{publishedAt},
                    'SYSTEM', 0, #{imageUrl},
                    0, COALESCE(#{publishedAt}, CURRENT_TIMESTAMP), COALESCE(#{publishedAt}, CURRENT_TIMESTAMP))
            """)
    void insert(Notice notice);

    @Update("""
            UPDATE t_notice SET title = #{title}, content = #{content},
                publisher_id = COALESCE((SELECT user_id FROM t_user WHERE user_name = #{publisher} LIMIT 1), publisher_id),
                pinned = #{pinned}, publish_status = CASE WHEN #{enabled} = 1 THEN 'PUBLISHED' ELSE 'OFFLINE' END,
                image_url = #{imageUrl}
            WHERE notice_id = #{noticeId}
            """)
    int update(Notice notice);

    @Update("UPDATE t_notice SET publish_status = CASE WHEN #{enabled} = 1 THEN 'PUBLISHED' ELSE 'OFFLINE' END WHERE notice_id = #{noticeId}")
    int updateEnabled(@Param("noticeId") String noticeId, @Param("enabled") boolean enabled);

    @Update("UPDATE t_notice SET pinned = #{pinned} WHERE notice_id = #{noticeId}")
    int updatePinned(@Param("noticeId") String noticeId, @Param("pinned") boolean pinned);

    @Delete("DELETE FROM t_notice WHERE notice_id = #{noticeId}")
    int delete(String noticeId);

    /**
     * 后台公告管理列表。
     *
     * 管理端需要草稿、已发布、已下架等状态，因此查询字段比前台更多，
     * 但角色范围仍然从 notice_target_role 聚合，避免主表保存重复字符串。
     */
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
            @Arg(column = "image_url", javaType = String.class),
            @Arg(column = "target_roles", javaType = String.class),
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
                   image_url,
                   COALESCE(
                       (SELECT GROUP_CONCAT(ntr.role_code ORDER BY ntr.role_code SEPARATOR ',')
                        FROM notice_target_role ntr
                        WHERE ntr.notice_id = n.notice_id),
                       'STUDENT,VOLUNTEER,HOSPITAL,ADMIN'
                   ) AS target_roles,
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

    /**
     * 更新后台扩展字段。
     *
     * 标题和正文由 update(Notice) 维护，类型、状态、排序、图片等管理字段在这里维护。
     */
    @Update("""
            UPDATE t_notice
            SET notice_type = #{noticeType}, publish_status = #{publishStatus}, publisher_id = #{publisherId},
                sort_order = #{sortOrder},
                image_url = #{imageUrl},
                update_time = #{updatedAt}
            WHERE notice_id = #{noticeId}
            """)
    int updateAdminFields(@Param("noticeId") String noticeId,
                          @Param("noticeType") String noticeType,
                          @Param("publishStatus") String publishStatus,
                          @Param("publisherId") String publisherId,
                          @Param("sortOrder") Integer sortOrder,
                          @Param("imageUrl") String imageUrl,
                          @Param("targetRoles") String targetRoles,
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

    /** 写入公告与可见角色的多对多关系。 */
    @Insert("INSERT IGNORE INTO notice_target_role (notice_id, role_code) VALUES (#{noticeId}, #{roleCode})")
    void insertTargetRole(@Param("noticeId") String noticeId, @Param("roleCode") String roleCode);

    /** 编辑公告时先清理旧角色，再由 Service 重新插入最新角色集合。 */
    @Delete("DELETE FROM notice_target_role WHERE notice_id = #{noticeId}")
    void deleteTargetRoles(String noticeId);
}
