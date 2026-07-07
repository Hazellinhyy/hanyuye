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

/**
 * 操作日志数据访问层接口
 * 负责操作日志的增删改查操作，支持日志的规范化处理和多条件查询
 */
@Mapper
public interface OperationLogMapper {
        /**
         * 插入操作日志记录
         * 记录用户的操作行为，包括操作前后的数据变化
         * 
         * @param operatorId    操作人ID
         * @param operatorName  操作人姓名（备用字段，实际存储在关联表）
         * @param operationType 操作类型（如：新增、修改、删除等）
         * @param targetType    目标类型（如：AGREEMENT协议、FOLLOWUP_TASK回访任务、WARNING预警等）
         * @param targetId      目标记录ID
         * @param beforeData    操作前数据（JSON格式）
         * @param afterData     操作后数据（JSON格式或状态值）
         * @param remark        操作备注说明
         * @param createdAt     创建时间
         */
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

        /**
         * 规范化存储的状态值
         * 将数据库中存储的英文状态码（如COMPLETED、PENDING等）统一转换为中文状态（如已完成、待处理等）
         * 同时根据操作类型推断状态值，确保状态显示的一致性
         * 
         * @return 更新的记录数
         */
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

        /**
         * 规范化存储的目标ID
         * 将日志中的目标ID转换为更易理解的业务标识：
         * - 回访任务(FOLLOWUP_TASK)：转换为对应的申请ID
         * - 预警记录(WARNING)：转换为预警标题（并进行中英文转换）
         * - 收养协议(AGREEMENT)：优先使用协议编号，其次使用申请ID
         * 
         * @return 更新的记录数
         */
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

        /**
         * 多条件分页查询操作日志列表
         * 支持按操作人、操作类型、业务类型、时间范围、关键字等条件进行筛选
         * 查询结果会关联用户表获取操作人姓名，关联相关业务表获取更友好的业务标识
         * 
         * @param operatorKeyword 操作人关键字（支持ID或姓名模糊匹配）
         * @param operationType   操作类型（精确匹配）
         * @param bizType         业务类型（精确匹配）
         * @param startTime       开始时间
         * @param endTime         结束时间
         * @param keyword         综合关键字（匹配目标ID、备注、操作前后数据）
         * @param limit           每页数量
         * @param offset          分页偏移量
         * @return 操作日志列表
         */
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

        /**
         * 根据日志ID查询单条操作日志记录
         * 查询时会关联用户表获取操作人姓名，关联业务表获取友好的业务标识
         * 
         * @param id 日志记录ID
         * @return 操作日志详情
         */
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