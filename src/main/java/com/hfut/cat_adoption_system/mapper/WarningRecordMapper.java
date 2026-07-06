package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.dto.WarningInfo;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface WarningRecordMapper {
    @Insert("""
            INSERT INTO warning_record
            (warning_type, warning_level, cat_id, user_id, application_id, task_id, title, content,
             handle_status, status, deleted, create_by, update_by, create_time, update_time)
            VALUES
            (#{warningType}, #{warningLevel}, #{catId}, #{userId}, #{applicationId}, #{taskId}, #{title}, #{content},
             'PENDING', 'VALID', 0, #{operatorId}, #{operatorId}, #{createdAt}, #{createdAt})
            """)
    int insert(@Param("warningType") String warningType,
               @Param("warningLevel") String warningLevel,
               @Param("bizType") String bizType,
               @Param("bizId") String bizId,
               @Param("catId") String catId,
               @Param("userId") String userId,
               @Param("applicationId") String applicationId,
               @Param("taskId") Long taskId,
               @Param("title") String title,
               @Param("content") String content,
               @Param("operatorId") String operatorId,
               @Param("createdAt") LocalDateTime createdAt);

    @Select("""
            SELECT COUNT(*) FROM warning_record
            WHERE task_id = #{taskId}
              AND warning_type = #{warningType}
              AND COALESCE(deleted, 0) = 0
              AND handle_status IN ('PENDING', 'PROCESSING')
            """)
    int countOpenByTaskAndType(@Param("taskId") Long taskId, @Param("warningType") String warningType);

    @Select("""
            SELECT COUNT(*) FROM warning_record
            WHERE cat_id = #{catId}
              AND COALESCE(deleted, 0) = 0
              AND handle_status IN ('PENDING', 'PROCESSING')
            """)
    int countOpenByCat(String catId);

    @Select("SELECT COUNT(*) FROM warning_record WHERE handle_status = #{status} AND COALESCE(deleted, 0) = 0")
    long countByStatus(String status);

    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "warning_type", javaType = String.class),
            @Arg(column = "warning_level", javaType = String.class),
            @Arg(column = "biz_type", javaType = String.class),
            @Arg(column = "biz_id", javaType = String.class),
            @Arg(column = "cat_id", javaType = String.class),
            @Arg(column = "cat_name", javaType = String.class),
            @Arg(column = "user_id", javaType = String.class),
            @Arg(column = "user_name", javaType = String.class),
            @Arg(column = "application_id", javaType = String.class),
            @Arg(column = "task_id", javaType = Long.class),
            @Arg(column = "title", javaType = String.class),
            @Arg(column = "content", javaType = String.class),
            @Arg(column = "status", javaType = String.class),
            @Arg(column = "handler_id", javaType = String.class),
            @Arg(column = "handler_name", javaType = String.class),
            @Arg(column = "handle_comment", javaType = String.class),
            @Arg(column = "handle_time", javaType = LocalDateTime.class),
            @Arg(column = "create_time", javaType = LocalDateTime.class)
    })
    @Select("""
            <script>
            SELECT wr.id, wr.warning_type, wr.warning_level,
                   wr.warning_type AS biz_type,
                   CAST(wr.id AS CHAR) AS biz_id,
                   wr.cat_id, c.cat_name, wr.user_id, u.user_name, wr.application_id, wr.task_id,
                   wr.title, wr.content,
                   wr.handle_status AS status, wr.handler_id, hu.user_name AS handler_name,
                   wr.handle_comment,
                   wr.handle_time,
                   wr.create_time
            FROM warning_record wr
            LEFT JOIN t_cat c ON c.cat_id = wr.cat_id
            LEFT JOIN t_user u ON u.user_id = wr.user_id
            LEFT JOIN t_user hu ON hu.user_id = wr.handler_id
            WHERE COALESCE(wr.deleted, 0) = 0
            <if test="id != null">AND wr.id = #{id}</if>
            <if test="warningType != null and warningType != ''">AND wr.warning_type = #{warningType}</if>
            <if test="warningLevel != null and warningLevel != ''">AND wr.warning_level = #{warningLevel}</if>
            <if test="status != null and status != ''">AND wr.handle_status = #{status}</if>
            <if test="taskId != null">AND wr.task_id = #{taskId}</if>
            <if test="keyword != null and keyword != ''">
              AND (wr.title LIKE CONCAT('%', #{keyword}, '%')
                   OR wr.cat_id LIKE CONCAT('%', #{keyword}, '%')
                   OR c.cat_name LIKE CONCAT('%', #{keyword}, '%')
                   OR u.user_name LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY wr.create_time DESC, wr.id DESC
            </script>
            """)
    List<WarningInfo> findWarnings(@Param("id") Long id,
                                   @Param("warningType") String warningType,
                                   @Param("warningLevel") String warningLevel,
                                   @Param("status") String status,
                                   @Param("taskId") Long taskId,
                                   @Param("keyword") String keyword);

    default WarningInfo findById(Long id) {
        List<WarningInfo> rows = findWarnings(id, null, null, null, null, null);
        return rows.isEmpty() ? null : rows.get(0);
    }

    default List<WarningInfo> findByTaskId(Long taskId) {
        return findWarnings(null, null, null, null, taskId, null);
    }

    @Update("""
            UPDATE warning_record
            SET handle_status = #{targetStatus},
                handler_id = #{handlerId},
                handle_comment = #{handleComment},
                handle_time = #{handledAt},
                update_by = #{handlerId},
                update_time = #{handledAt}
            WHERE id = #{id} AND COALESCE(deleted, 0) = 0
            """)
    int updateHandleStatus(@Param("id") Long id,
                           @Param("targetStatus") String targetStatus,
                           @Param("handleComment") String handleComment,
                           @Param("handlerId") String handlerId,
                           @Param("handledAt") LocalDateTime handledAt);

    @Update("""
            UPDATE warning_record
            SET deleted = 1,
                update_by = #{operatorId},
                update_time = #{updatedAt}
            WHERE id = #{id} AND COALESCE(deleted, 0) = 0
            """)
    int deleteWarning(@Param("id") Long id,
                      @Param("operatorId") String operatorId,
                      @Param("updatedAt") LocalDateTime updatedAt);
}
