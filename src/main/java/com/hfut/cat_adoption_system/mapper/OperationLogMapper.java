package com.hfut.cat_adoption_system.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import com.hfut.cat_adoption_system.dto.OperationLogInfo;

@Mapper
public interface OperationLogMapper {
    @Insert("""
            INSERT INTO operation_log (operator_id, operation_type, target_type, target_id,
                                       before_data, after_data, remark, create_time, update_time)
            VALUES (#{operatorId}, #{operationType}, #{targetType}, #{targetId},
                    #{beforeData}, #{afterData}, #{remark}, #{createdAt}, #{createdAt})
            """)
    void insert(@Param("operatorId") String operatorId,
                @Param("operatorName") String operatorName,
                @Param("operationType") String operationType,
                @Param("targetType") String targetType,
                @Param("targetId") String targetId,
                @Param("beforeData") String beforeData,
                @Param("afterData") String afterData,
                @Param("remark") String remark,
                @Param("createdAt") LocalDateTime createdAt);

    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "operator_id", javaType = String.class),
            @Arg(column = "operator_name", javaType = String.class),
            @Arg(column = "operation_type", javaType = String.class),
            @Arg(column = "biz_type", javaType = String.class),
            @Arg(column = "biz_id", javaType = String.class),
            @Arg(column = "before_data", javaType = String.class),
            @Arg(column = "after_data", javaType = String.class),
            @Arg(column = "request_ip", javaType = String.class),
            @Arg(column = "remark", javaType = String.class),
            @Arg(column = "create_time", javaType = LocalDateTime.class)
    })
    @Select("""
            <script>
            SELECT l.id, l.operator_id, COALESCE(u.user_name, l.operator_id) AS operator_name, l.operation_type,
                   l.target_type AS biz_type, l.target_id AS biz_id,
                   l.before_data, l.after_data, l.request_ip, l.remark, l.create_time
            FROM operation_log l
            LEFT JOIN t_user u ON u.user_id = l.operator_id
            WHERE COALESCE(l.deleted, 0) = 0
            <if test="operatorKeyword != null and operatorKeyword != ''">
              AND (l.operator_id LIKE CONCAT('%', #{operatorKeyword}, '%') OR u.user_name LIKE CONCAT('%', #{operatorKeyword}, '%'))
            </if>
            <if test="operationType != null and operationType != ''">AND l.operation_type = #{operationType}</if>
            <if test="bizType != null and bizType != ''">AND l.target_type = #{bizType}</if>
            <if test="startTime != null">AND l.create_time &gt;= #{startTime}</if>
            <if test="endTime != null">AND l.create_time &lt;= #{endTime}</if>
            <if test="keyword != null and keyword != ''">
              AND (l.target_id LIKE CONCAT('%', #{keyword}, '%') OR l.remark LIKE CONCAT('%', #{keyword}, '%')
                   OR l.before_data LIKE CONCAT('%', #{keyword}, '%') OR l.after_data LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY l.create_time DESC, l.id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<OperationLogInfo> findLogs(@Param("operatorKeyword") String operatorKeyword,
                                    @Param("operationType") String operationType,
                                    @Param("bizType") String bizType,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime,
                                    @Param("keyword") String keyword,
                                    @Param("limit") int limit,
                                    @Param("offset") int offset);

    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "operator_id", javaType = String.class),
            @Arg(column = "operator_name", javaType = String.class),
            @Arg(column = "operation_type", javaType = String.class),
            @Arg(column = "biz_type", javaType = String.class),
            @Arg(column = "biz_id", javaType = String.class),
            @Arg(column = "before_data", javaType = String.class),
            @Arg(column = "after_data", javaType = String.class),
            @Arg(column = "request_ip", javaType = String.class),
            @Arg(column = "remark", javaType = String.class),
            @Arg(column = "create_time", javaType = LocalDateTime.class)
    })
    @Select("""
            SELECT l.id, l.operator_id, COALESCE(u.user_name, l.operator_id) AS operator_name, l.operation_type,
                   l.target_type AS biz_type, l.target_id AS biz_id,
                   l.before_data, l.after_data, l.request_ip, l.remark, l.create_time
            FROM operation_log l
            LEFT JOIN t_user u ON u.user_id = l.operator_id
            WHERE l.id = #{id} AND COALESCE(l.deleted, 0) = 0
            """)
    OperationLogInfo findById(Long id);
}
