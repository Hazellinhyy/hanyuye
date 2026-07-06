package com.hfut.cat_adoption_system.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    @Update("""
            UPDATE operation_log
            SET after_data = CASE
                WHEN after_data IN ('已完成','待处理','处理中','已处理','已忽略','已删除','已生成','已发布','已下架','已取消','已作废','待交接','已交接','草稿') THEN after_data
                WHEN after_data IN ('COMPLETED','VERIFIED_VALID','CREATED_CAT','ADOPTABLE','APPLYING','ADOPTED','FOLLOWING','INITIAL_REJECTED','FINAL_REJECTED') THEN '已完成'
                WHEN after_data IN ('PENDING','PENDING_VERIFY','PENDING_INITIAL','PENDING_FINAL','OVERDUE','FOLLOWUP_ABNORMAL','FOLLOWUP_OVERDUE','HIGH_RISK_APPLICATION','MEDICAL_ABNORMAL') THEN '待处理'
                WHEN after_data = 'PROCESSING' THEN '处理中'
                WHEN after_data = 'HANDLED' THEN '已处理'
                WHEN after_data = 'IGNORED' THEN '已忽略'
                WHEN after_data = 'DELETED' THEN '已删除'
                WHEN after_data = 'GENERATED' THEN '已生成'
                WHEN after_data = 'PUBLISHED' THEN '已发布'
                WHEN after_data = 'OFFLINE' THEN '已下架'
                WHEN after_data = 'CANCELLED' THEN '已取消'
                WHEN after_data = 'VOID' THEN '已作废'
                WHEN after_data = 'PENDING_HANDOVER' THEN '待交接'
                WHEN after_data = 'HANDED_OVER' THEN '已交接'
                WHEN after_data = 'DRAFT' THEN '草稿'
                WHEN operation_type LIKE '%删除%' OR operation_type LIKE '%Delete%' THEN '已删除'
                WHEN operation_type LIKE '%作废%' OR operation_type LIKE '%Void%' THEN '已作废'
                WHEN operation_type LIKE '%取消%' OR operation_type LIKE '%Cancel%' THEN '已取消'
                WHEN operation_type LIKE '%生成%' OR operation_type LIKE '%Generate%' THEN '已生成'
                WHEN operation_type LIKE '%发布%' OR operation_type LIKE '%Publish%' THEN '已发布'
                WHEN operation_type LIKE '%下架%' OR operation_type LIKE '%Offline%' THEN '已下架'
                WHEN operation_type LIKE '%交接%' OR operation_type LIKE '%handover%' THEN '已交接'
                WHEN operation_type LIKE '%处理%' OR operation_type LIKE '%Handle%' THEN '已处理'
                ELSE '已完成'
            END,
            update_time = CURRENT_TIMESTAMP
            WHERE COALESCE(deleted, 0) = 0
              AND (after_data IS NULL OR after_data NOT IN ('已完成','待处理','处理中','已处理','已忽略','已删除','已生成','已发布','已下架','已取消','已作废','待交接','已交接','草稿'))
            """)
    int normalizeStoredStatuses();

    @Update("""
            UPDATE operation_log l
            LEFT JOIN followup_task ft ON l.target_type = 'FOLLOWUP_TASK' AND CAST(ft.id AS CHAR) = l.target_id
            LEFT JOIN warning_record wr ON l.target_type = 'WARNING' AND CAST(wr.id AS CHAR) = l.target_id
            LEFT JOIN adoption_agreement ag ON l.target_type = 'AGREEMENT' AND CAST(ag.id AS CHAR) = l.target_id
            SET l.target_id = CASE
                WHEN l.target_type = 'FOLLOWUP_TASK' THEN ft.application_id
                WHEN l.target_type = 'WARNING' THEN REPLACE(REPLACE(REPLACE(REPLACE(wr.title,
                    'Abnormal follow-up:', '回访异常：'),
                    'Follow-up overdue:', '回访逾期：'),
                    'High risk application warning', '高风险申请预警'),
                    'Medical abnormal warning', '健康异常预警')
                WHEN l.target_type = 'AGREEMENT' THEN COALESCE(ag.agreement_no, ag.application_id)
                ELSE l.target_id
            END,
            l.update_time = CURRENT_TIMESTAMP
            WHERE COALESCE(l.deleted, 0) = 0
              AND ((l.target_type = 'FOLLOWUP_TASK' AND ft.application_id IS NOT NULL)
                OR (l.target_type = 'WARNING' AND wr.title IS NOT NULL)
                OR (l.target_type = 'AGREEMENT' AND (ag.agreement_no IS NOT NULL OR ag.application_id IS NOT NULL)))
            """)
    int normalizeStoredTargets();

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
                   l.target_type AS biz_type,
                   CASE
                     WHEN l.target_type = 'FOLLOWUP_TASK' THEN COALESCE(ft.application_id, l.target_id)
                     WHEN l.target_type = 'WARNING' THEN COALESCE(wr.title, l.remark, l.target_id)
                     ELSE l.target_id
                   END AS biz_id,
                   l.before_data, l.after_data, l.request_ip, l.remark, l.create_time
            FROM operation_log l
            LEFT JOIN t_user u ON u.user_id = l.operator_id
            LEFT JOIN followup_task ft ON l.target_type = 'FOLLOWUP_TASK' AND CAST(ft.id AS CHAR) = l.target_id
            LEFT JOIN warning_record wr ON l.target_type = 'WARNING' AND CAST(wr.id AS CHAR) = l.target_id
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
                   l.target_type AS biz_type,
                   CASE
                     WHEN l.target_type = 'FOLLOWUP_TASK' THEN COALESCE(ft.application_id, l.target_id)
                     WHEN l.target_type = 'WARNING' THEN COALESCE(wr.title, l.remark, l.target_id)
                     ELSE l.target_id
                   END AS biz_id,
                   l.before_data, l.after_data, l.request_ip, l.remark, l.create_time
            FROM operation_log l
            LEFT JOIN t_user u ON u.user_id = l.operator_id
            LEFT JOIN followup_task ft ON l.target_type = 'FOLLOWUP_TASK' AND CAST(ft.id AS CHAR) = l.target_id
            LEFT JOIN warning_record wr ON l.target_type = 'WARNING' AND CAST(wr.id AS CHAR) = l.target_id
            WHERE l.id = #{id} AND COALESCE(l.deleted, 0) = 0
            """)
    OperationLogInfo findById(Long id);
}
