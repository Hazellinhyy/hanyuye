package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.FollowupRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FollowupMapper {
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
    List<FollowupRecord> findAll(@Param("applicationId") String applicationId, @Param("result") com.hfut.cat_adoption_system.model.FollowupResult result);

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

    @Select("""
            SELECT id FROM followup_task
            WHERE application_id = #{applicationId}
              AND create_time = #{createdAt}
              AND COALESCE(deleted, 0) = 0
            ORDER BY id DESC LIMIT 1
            """)
    Long findLegacyTaskId(@Param("applicationId") String applicationId,
                          @Param("createdAt") LocalDateTime createdAt);

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

    default void insert(FollowupRecord record) {
        boolean abnormal = record.result() != com.hfut.cat_adoption_system.model.FollowupResult.NORMAL;
        String abnormalDesc = abnormal ? record.result().name() : null;
        insertLegacyTask(record.applicationId(), record.followupTime().toLocalDate(),
                record.method(), abnormal, null, record.followupTime());
        Long taskId = findLegacyTaskId(record.applicationId(), record.followupTime());
        insertLegacyRecord(taskId, record, abnormal, abnormalDesc, null);
    }

    @Select("SELECT COUNT(*) FROM followup_record WHERE COALESCE(deleted, 0) = 0")
    long countAll();
}
