package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.dto.FollowupRecordInfo;
import com.hfut.cat_adoption_system.dto.FollowupTaskInfo;
import com.hfut.cat_adoption_system.model.FollowupTaskStatus;
import com.hfut.cat_adoption_system.model.FollowupTaskType;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FollowupTaskMapper {
    @Insert("""
            INSERT INTO followup_task
            (application_id, agreement_id, plan_date, task_type, status, abnormal_flag,
             deleted, create_by, update_by, create_time, update_time)
            VALUES
            (#{applicationId}, #{agreementId}, #{planDate}, #{taskType}, #{status}, 0,
             0, #{operatorId}, #{operatorId}, #{createdAt}, #{createdAt})
            """)
    int insertTask(@Param("applicationId") String applicationId,
                   @Param("agreementId") Long agreementId,
                   @Param("catId") String catId,
                   @Param("adopterId") String adopterId,
                   @Param("planDate") LocalDate planDate,
                   @Param("taskType") FollowupTaskType taskType,
                   @Param("status") FollowupTaskStatus status,
                   @Param("operatorId") String operatorId,
                   @Param("createdAt") LocalDateTime createdAt);

    @Select("SELECT COUNT(*) FROM followup_task WHERE application_id = #{applicationId} AND COALESCE(deleted, 0) = 0")
    int countByApplicationId(String applicationId);

    @Select("SELECT COUNT(*) FROM followup_task WHERE COALESCE(deleted, 0) = 0")
    long countAllActive();

    @Select("SELECT COUNT(*) FROM followup_task WHERE status = #{status} AND COALESCE(deleted, 0) = 0")
    long countByStatus(FollowupTaskStatus status);

    @Select("""
            SELECT COUNT(*) FROM followup_task
            JOIN t_application a ON a.application_id = followup_task.application_id
            WHERE a.cat_id = #{catId} AND COALESCE(followup_task.deleted, 0) = 0 AND followup_task.status = #{status}
            """)
    int countByCatAndStatus(@Param("catId") String catId, @Param("status") FollowupTaskStatus status);

    @Select("""
            SELECT COUNT(*) FROM followup_task
            JOIN t_application a ON a.application_id = followup_task.application_id
            WHERE a.cat_id = #{catId}
              AND COALESCE(followup_task.deleted, 0) = 0
              AND followup_task.status IN ('PENDING', 'OVERDUE', 'ABNORMAL')
            """)
    int countUnclosedByCat(String catId);

    @Select("""
            SELECT id FROM followup_task
            WHERE COALESCE(deleted, 0) = 0
              AND status = 'PENDING'
              AND plan_date < #{today}
            ORDER BY plan_date ASC, id ASC
            """)
    List<Long> findPendingOverdueIds(LocalDate today);

    @Update("""
            UPDATE followup_task
            SET status = #{status}, actual_date = #{actualDate}, abnormal_flag = #{abnormalFlag},
                handler_id = #{handlerId}, update_by = #{operatorId}, update_time = #{updatedAt}
            WHERE id = #{id} AND COALESCE(deleted, 0) = 0
            """)
    int updateTaskStatus(@Param("id") Long id,
                         @Param("status") FollowupTaskStatus status,
                         @Param("actualDate") LocalDate actualDate,
                         @Param("abnormalFlag") boolean abnormalFlag,
                         @Param("handlerId") String handlerId,
                         @Param("operatorId") String operatorId,
                         @Param("updatedAt") LocalDateTime updatedAt);

    @Insert("""
            INSERT INTO followup_record
            (task_id, application_id, cat_id, user_id, content, cat_condition, environment_description, photo_url,
             abnormal_flag, abnormal_description, volunteer_comment,
             submit_time, status, deleted, create_by, update_by, create_time, update_time)
            VALUES
            (#{taskId}, #{applicationId}, #{catId}, #{adopterId}, #{content}, #{catCondition}, #{environmentDesc}, #{photoUrl},
             #{abnormalFlag}, #{abnormalDesc}, #{volunteerComment},
             #{submitTime}, 'SUBMITTED', 0, #{operatorId}, #{operatorId}, #{submitTime}, #{submitTime})
            """)
    int insertRecord(@Param("taskId") Long taskId,
                     @Param("applicationId") String applicationId,
                     @Param("agreementId") Long agreementId,
                     @Param("catId") String catId,
                     @Param("adopterId") String adopterId,
                     @Param("content") String content,
                     @Param("catCondition") String catCondition,
                     @Param("environmentDesc") String environmentDesc,
                     @Param("photoUrl") String photoUrl,
                     @Param("abnormalFlag") boolean abnormalFlag,
                     @Param("abnormalDesc") String abnormalDesc,
                     @Param("volunteerComment") String volunteerComment,
                     @Param("operatorId") String operatorId,
                     @Param("submitTime") LocalDateTime submitTime);

    @Select("SELECT COUNT(*) FROM followup_record WHERE task_id = #{taskId} AND COALESCE(deleted, 0) = 0")
    int countRecordByTaskId(Long taskId);

    @Update("""
            UPDATE followup_record
            SET volunteer_comment = #{volunteerComment}, update_by = #{operatorId}, update_time = #{updatedAt}
            WHERE task_id = #{taskId} AND COALESCE(deleted, 0) = 0
            """)
    int updateRecordVolunteerComment(@Param("taskId") Long taskId,
                                     @Param("volunteerComment") String volunteerComment,
                                     @Param("operatorId") String operatorId,
                                     @Param("updatedAt") LocalDateTime updatedAt);

    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "task_id", javaType = Long.class),
            @Arg(column = "application_id", javaType = String.class),
            @Arg(column = "agreement_id", javaType = Long.class),
            @Arg(column = "cat_id", javaType = String.class),
            @Arg(column = "adopter_id", javaType = String.class),
            @Arg(column = "content", javaType = String.class),
            @Arg(column = "cat_condition", javaType = String.class),
            @Arg(column = "environment_desc", javaType = String.class),
            @Arg(column = "photo_url", javaType = String.class),
            @Arg(column = "abnormal_flag", javaType = Boolean.class),
            @Arg(column = "abnormal_desc", javaType = String.class),
            @Arg(column = "volunteer_comment", javaType = String.class),
            @Arg(column = "submitter_id", javaType = String.class),
            @Arg(column = "submitter_name", javaType = String.class),
            @Arg(column = "submitter_role", javaType = String.class),
            @Arg(column = "submit_time", javaType = LocalDateTime.class)
    })
    @Select("""
            SELECT fr.id, fr.task_id, ft.application_id, ft.agreement_id, a.cat_id,
                   a.user_id AS adopter_id,
                   fr.content, fr.cat_condition,
                   fr.environment_description AS environment_desc,
                   photo_url, abnormal_flag,
                   fr.abnormal_description AS abnormal_desc,
                   volunteer_comment,
                   fr.create_by AS submitter_id,
                   ru.user_name AS submitter_name,
                   ru.role_code AS submitter_role,
                   submit_time
            FROM followup_record fr
            JOIN followup_task ft ON ft.id = fr.task_id
            JOIN t_application a ON a.application_id = ft.application_id
            LEFT JOIN t_user ru ON ru.user_id = fr.create_by
            WHERE fr.task_id = #{taskId} AND COALESCE(fr.deleted, 0) = 0
            ORDER BY fr.id DESC LIMIT 1
            """)
    FollowupRecordInfo findRecordByTaskId(Long taskId);

    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "task_id", javaType = Long.class),
            @Arg(column = "application_id", javaType = String.class),
            @Arg(column = "agreement_id", javaType = Long.class),
            @Arg(column = "cat_id", javaType = String.class),
            @Arg(column = "adopter_id", javaType = String.class),
            @Arg(column = "content", javaType = String.class),
            @Arg(column = "cat_condition", javaType = String.class),
            @Arg(column = "environment_desc", javaType = String.class),
            @Arg(column = "photo_url", javaType = String.class),
            @Arg(column = "abnormal_flag", javaType = Boolean.class),
            @Arg(column = "abnormal_desc", javaType = String.class),
            @Arg(column = "volunteer_comment", javaType = String.class),
            @Arg(column = "submitter_id", javaType = String.class),
            @Arg(column = "submitter_name", javaType = String.class),
            @Arg(column = "submitter_role", javaType = String.class),
            @Arg(column = "submit_time", javaType = LocalDateTime.class)
    })
    @Select("""
            SELECT fr.id, fr.task_id, ft.application_id, ft.agreement_id, a.cat_id,
                   a.user_id AS adopter_id,
                   fr.content, fr.cat_condition,
                   fr.environment_description AS environment_desc,
                   fr.photo_url, fr.abnormal_flag,
                   fr.abnormal_description AS abnormal_desc,
                   fr.volunteer_comment,
                   fr.create_by AS submitter_id,
                   ru.user_name AS submitter_name,
                   ru.role_code AS submitter_role,
                   fr.submit_time
            FROM followup_record fr
            JOIN followup_task ft ON ft.id = fr.task_id
            JOIN t_application a ON a.application_id = ft.application_id
            LEFT JOIN t_user ru ON ru.user_id = fr.create_by
            WHERE fr.task_id = #{taskId} AND COALESCE(fr.deleted, 0) = 0
            ORDER BY fr.submit_time DESC, fr.id DESC
            """)
    List<FollowupRecordInfo> findRecordsByTaskId(Long taskId);

    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "application_id", javaType = String.class),
            @Arg(column = "agreement_id", javaType = Long.class),
            @Arg(column = "agreement_no", javaType = String.class),
            @Arg(column = "cat_id", javaType = String.class),
            @Arg(column = "cat_name", javaType = String.class),
            @Arg(column = "cat_cover_url", javaType = String.class),
            @Arg(column = "adopter_id", javaType = String.class),
            @Arg(column = "adopter_name", javaType = String.class),
            @Arg(column = "adopter_phone", javaType = String.class),
            @Arg(column = "plan_date", javaType = LocalDate.class),
            @Arg(column = "actual_date", javaType = LocalDate.class),
            @Arg(column = "task_type", javaType = String.class),
            @Arg(column = "status", javaType = String.class),
            @Arg(column = "abnormal_flag", javaType = Boolean.class),
            @Arg(column = "handler_id", javaType = String.class),
            @Arg(column = "handler_name", javaType = String.class),
            @Arg(column = "create_time", javaType = LocalDateTime.class),
            @Arg(column = "feedback_enabled", javaType = boolean.class),
            @Arg(column = "record_id", javaType = Long.class),
            @Arg(column = "record_content", javaType = String.class),
            @Arg(column = "cat_condition", javaType = String.class),
            @Arg(column = "environment_desc", javaType = String.class),
            @Arg(column = "photo_url", javaType = String.class),
            @Arg(column = "record_abnormal_flag", javaType = Boolean.class),
            @Arg(column = "abnormal_desc", javaType = String.class),
            @Arg(column = "volunteer_comment", javaType = String.class),
            @Arg(column = "submit_time", javaType = LocalDateTime.class),
            @Arg(column = "warning_count", javaType = Integer.class)
    })
    @Select("""
            <script>
            SELECT ft.id, ft.application_id, ft.agreement_id, ag.agreement_no, a.cat_id, c.cat_name,
                   c.cover_url AS cat_cover_url,
                   a.user_id AS adopter_id,
                   u.user_name AS adopter_name, u.phone AS adopter_phone,
                   ft.plan_date, ft.actual_date,
                   ft.task_type,
                   ft.status, ft.abnormal_flag, ft.handler_id, hu.user_name AS handler_name,
                   ft.create_time,
                   CASE WHEN ft.status IN ('PENDING', 'OVERDUE', 'ABNORMAL') THEN 1 ELSE 0 END AS feedback_enabled,
                   fr.id AS record_id, fr.content AS record_content, fr.cat_condition,
                   fr.environment_description AS environment_desc,
                   fr.photo_url, fr.abnormal_flag AS record_abnormal_flag,
                   fr.abnormal_description AS abnormal_desc,
                   fr.volunteer_comment, fr.submit_time,
                   (SELECT COUNT(*) FROM warning_record wr WHERE wr.task_id = ft.id AND COALESCE(wr.deleted, 0) = 0) AS warning_count
            FROM followup_task ft
            JOIN t_application a ON a.application_id = ft.application_id
            JOIN t_cat c ON c.cat_id = a.cat_id
            JOIN t_user u ON u.user_id = a.user_id
            LEFT JOIN adoption_agreement ag ON ag.id = ft.agreement_id
            LEFT JOIN t_user hu ON hu.user_id = ft.handler_id
            LEFT JOIN followup_record fr ON fr.id = (
                SELECT fr2.id
                FROM followup_record fr2
                WHERE fr2.task_id = ft.id AND COALESCE(fr2.deleted, 0) = 0
                ORDER BY fr2.submit_time DESC, fr2.id DESC
                LIMIT 1
            )
            WHERE COALESCE(ft.deleted, 0) = 0
            <if test="id != null">AND ft.id = #{id}</if>
            <if test="adopterId != null and adopterId != ''">AND a.user_id = #{adopterId}</if>
            <if test="applicationId != null and applicationId != ''">AND ft.application_id = #{applicationId}</if>
            <if test="status != null">AND ft.status = #{status}</if>
            <if test="taskType != null and taskType != ''">AND ft.task_type = #{taskType}</if>
            <if test="planDate != null">AND ft.plan_date = #{planDate}</if>
            <if test="keyword != null and keyword != ''">
              AND (ft.application_id LIKE CONCAT('%', #{keyword}, '%')
                   OR ag.agreement_no LIKE CONCAT('%', #{keyword}, '%')
                   OR a.cat_id LIKE CONCAT('%', #{keyword}, '%')
                   OR c.cat_name LIKE CONCAT('%', #{keyword}, '%')
                   OR u.user_name LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY ft.plan_date ASC, ft.id ASC
            </script>
            """)
    List<FollowupTaskInfo> findTasks(@Param("id") Long id,
                                     @Param("adopterId") String adopterId,
                                     @Param("applicationId") String applicationId,
                                     @Param("status") FollowupTaskStatus status,
                                     @Param("taskType") String taskType,
                                     @Param("planDate") LocalDate planDate,
                                     @Param("keyword") String keyword);

    default List<FollowupTaskInfo> findTasks(String adopterId, String applicationId, FollowupTaskStatus status,
                                             LocalDate planDate, String keyword) {
        return findTasks(null, adopterId, applicationId, status, null, planDate, keyword);
    }

    default FollowupTaskInfo findById(Long id) {
        List<FollowupTaskInfo> rows = findTasks(id, null, null, null, null, null, null);
        return rows.isEmpty() ? null : rows.get(0);
    }
}
