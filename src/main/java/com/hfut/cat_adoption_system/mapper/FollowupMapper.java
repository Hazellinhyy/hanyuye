package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.FollowupRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 回访记录Mapper接口
 * 
 * 提供领养后回访记录的查询和插入操作，支持回访任务和记录的管理。
 * 采用任务-记录分离的设计模式，一条回访记录对应一条回访任务。
 */
@Mapper
public interface FollowupMapper {

        /**
         * 查询回访记录列表
         * 
         * 支持按领养申请ID和回访结果筛选，关联任务表和用户表获取完整信息。
         * 通过CASE表达式将异常标记转换为标准化结果状态（NORMAL/RETURNED/ATTENTION）。
         * 
         * @param applicationId 领养申请ID（可选）
         * @param result        回访结果（NORMAL-正常, RETURNED-退回, ATTENTION-关注）
         * @return 回访记录列表
         */
        @Select("""
                        <script>
                        SELECT CAST(fr.id AS CHAR) AS followup_id,
                               ft.application_id,
                               fr.submit_time AS followup_time,
                               ft.task_type AS method,
                               fr.cat_condition,
                               fr.environment_description,
                               CASE
                                   WHEN fr.abnormal_flag = 0 THEN 'NORMAL'
                                   WHEN fr.abnormal_description = 'RETURNED' THEN 'RETURNED'
                                   ELSE 'ATTENTION'
                               END AS result,
                               fr.photo_url,
                               fr.volunteer_comment AS suggestion,
                               COALESCE(u.user_name, fr.create_by) AS operator_name
                        FROM followup_record fr
                        JOIN followup_task ft ON ft.id = fr.task_id
                        LEFT JOIN t_user u ON u.user_id = fr.create_by
                        WHERE COALESCE(fr.deleted, 0) = 0 AND COALESCE(ft.deleted, 0) = 0
                        <if test="applicationId != null and applicationId != ''">AND ft.application_id = #{applicationId}</if>
                        <if test="result != null">
                          AND CASE
                                WHEN fr.abnormal_flag = 0 THEN 'NORMAL'
                                WHEN fr.abnormal_description = 'RETURNED' THEN 'RETURNED'
                                ELSE 'ATTENTION'
                              END = #{result}
                        </if>
                        ORDER BY fr.submit_time DESC, fr.id DESC
                        </script>
                        """)
        List<FollowupRecord> findAll(@Param("applicationId") String applicationId,
                        @Param("result") com.hfut.cat_adoption_system.model.FollowupResult result);

        /**
         * 插入回访任务记录（兼容遗留系统设计）
         * 
         * 在旧架构中，回访任务和回访记录是分离的，此方法插入任务主表。
         * 
         * @param applicationId 领养申请ID
         * @param planDate      计划回访日期
         * @param method        回访方式（电话/上门等）
         * @param abnormalFlag  是否异常
         * @param operatorId    操作人ID
         * @param createdAt     创建时间
         * @return 插入的记录数
         */
        @Insert("""
                        INSERT INTO followup_task
                        (application_id, plan_date, actual_date, task_type, status, handler_id, completed_time,
                         abnormal_flag, deleted, create_time, update_time)
                        VALUES
                        (#{applicationId}, #{planDate}, #{planDate}, #{method}, 'COMPLETED', #{operatorId}, #{createdAt},
                         #{abnormalFlag}, 0, #{createdAt}, #{createdAt})
                        """)
        int insertLegacyTask(@Param("applicationId") String applicationId,
                        @Param("planDate") LocalDate planDate,
                        @Param("method") String method,
                        @Param("abnormalFlag") boolean abnormalFlag,
                        @Param("operatorId") String operatorId,
                        @Param("createdAt") LocalDateTime createdAt);

        /**
         * 查询刚插入的回访任务ID
         * 
         * 通过申请ID和创建时间定位刚插入的任务记录。
         * 
         * @param applicationId 领养申请ID
         * @param createdAt     创建时间
         * @return 任务ID
         */
        @Select("""
                        SELECT id FROM followup_task
                        WHERE application_id = #{applicationId}
                          AND create_time = #{createdAt}
                          AND COALESCE(deleted, 0) = 0
                        ORDER BY id DESC LIMIT 1
                        """)
        Long findLegacyTaskId(@Param("applicationId") String applicationId,
                        @Param("createdAt") LocalDateTime createdAt);

        /**
         * 插入回访记录详情（兼容遗留系统设计）
         * 
         * 在旧架构中，回访详情记录单独存储，关联到对应的任务ID。
         * 
         * @param taskId       任务ID
         * @param record       回访记录对象
         * @param abnormalFlag 是否异常标记
         * @param abnormalDesc 异常描述
         * @param operatorId   操作人ID
         * @return 插入的记录数
         */
        @Insert("""
                        INSERT INTO followup_record
                        (task_id, cat_condition, environment_description, photo_url,
                         abnormal_flag, abnormal_description, volunteer_comment,
                         status, deleted, create_by, update_by, submit_time, create_time, update_time)
                        VALUES
                        (#{taskId}, #{record.catCondition}, #{record.environmentDescription}, #{record.photoUrl},
                         #{abnormalFlag}, #{abnormalDesc}, #{record.suggestion},
                         'SUBMITTED', 0, #{operatorId}, #{operatorId}, #{record.followupTime}, #{record.followupTime}, #{record.followupTime})
                        """)
        int insertLegacyRecord(@Param("taskId") Long taskId,
                        @Param("record") FollowupRecord record,
                        @Param("abnormalFlag") boolean abnormalFlag,
                        @Param("abnormalDesc") String abnormalDesc,
                        @Param("operatorId") String operatorId);

        /**
         * 插入回访记录（组合方法）
         * 
         * 统一回访记录插入入口，自动处理任务表和记录表的关联插入：
         * 1. 根据回访结果判断是否异常
         * 2. 插入回访任务
         * 3. 查询刚插入的任务ID
         * 4. 插入回访记录详情
         * 
         * @param record 回访记录对象
         */
        default void insert(FollowupRecord record) {
                boolean abnormal = record.result() != com.hfut.cat_adoption_system.model.FollowupResult.NORMAL;
                String abnormalDesc = abnormal ? record.result().name() : null;
                insertLegacyTask(record.applicationId(), record.followupTime().toLocalDate(),
                                record.method(), abnormal, null, record.followupTime());
                Long taskId = findLegacyTaskId(record.applicationId(), record.followupTime());
                insertLegacyRecord(taskId, record, abnormal, abnormalDesc, null);
        }

        /**
         * 统计所有回访记录数量
         * 
         * @return 回访记录总数
         */
        @Select("SELECT COUNT(*) FROM followup_record WHERE COALESCE(deleted, 0) = 0")
        long countAll();
}