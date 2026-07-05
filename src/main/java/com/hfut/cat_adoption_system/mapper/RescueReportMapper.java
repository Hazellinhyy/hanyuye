package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.Clue;
import com.hfut.cat_adoption_system.model.ClueStatus;
import com.hfut.cat_adoption_system.model.RescueReport;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RescueReportMapper {
    @Select("SELECT report_id, reporter_name, reporter_phone, found_place, color, gender, health_description, urgent, photo_url, report_status AS status, reported_at FROM t_rescue_report ORDER BY reported_at DESC")
    List<RescueReport> findAll();

    @Insert("""
            INSERT INTO t_rescue_report (report_id, reporter_name, reporter_phone, found_place, color, gender, health_description, urgent, photo_url, report_status, reported_at)
            VALUES (#{reportId}, #{reporterName}, #{reporterPhone}, #{foundPlace}, #{color}, #{gender}, #{healthDescription}, #{urgent}, #{photoUrl}, #{status}, #{reportedAt})
            """)
    void insert(RescueReport report);

    @Select("SELECT report_id, reporter_name, reporter_phone, found_place, color, gender, health_description, urgent, photo_url, report_status AS status, reported_at FROM t_rescue_report WHERE report_id = #{reportId}")
    RescueReport findById(String reportId);

    @Update("UPDATE t_rescue_report SET report_status = #{status} WHERE report_id = #{reportId}")
    void updateStatus(String reportId, String status);

    @Select("""
            SELECT COALESCE(MAX(CAST(SUBSTRING(report_id, LENGTH(#{prefix}) + 1) AS UNSIGNED)), 0)
            FROM t_rescue_report
            WHERE report_id LIKE CONCAT(#{prefix}, '%')
            """)
    int maxSuffixByPrefix(String prefix);

    @ConstructorArgs({
            @Arg(column = "report_id", javaType = String.class),
            @Arg(column = "report_id", javaType = String.class),
            @Arg(column = "reporter_id", javaType = String.class),
            @Arg(column = "reporter_name", javaType = String.class),
            @Arg(column = "reporter_phone", javaType = String.class),
            @Arg(column = "found_place", javaType = String.class),
            @Arg(column = "found_area", javaType = String.class),
            @Arg(column = "found_time", javaType = LocalDateTime.class),
            @Arg(column = "photo_url", javaType = String.class),
            @Arg(column = "health_description", javaType = String.class),
            @Arg(column = "urgency_level", javaType = String.class),
            @Arg(column = "report_status", javaType = String.class),
            @Arg(column = "verify_user_id", javaType = String.class),
            @Arg(column = "verify_user_name", javaType = String.class),
            @Arg(column = "verify_result", javaType = String.class),
            @Arg(column = "verify_comment", javaType = String.class),
            @Arg(column = "verify_time", javaType = LocalDateTime.class),
            @Arg(column = "created_cat_id", javaType = String.class),
            @Arg(column = "reported_at", javaType = LocalDateTime.class),
            @Arg(column = "update_time", javaType = LocalDateTime.class),
            @Arg(column = "deleted", javaType = boolean.class)
    })
    @Select("""
            <script>
            SELECT r.report_id, r.reporter_id, r.reporter_name, r.reporter_phone, r.found_place,
                   r.found_area, r.found_time, r.photo_url, r.health_description, r.urgency_level,
                   r.report_status, r.verify_user_id, u.user_name AS verify_user_name, r.verify_result,
                   r.verify_comment, r.verify_time, r.created_cat_id, r.reported_at, r.update_time, r.deleted
            FROM t_rescue_report r
            LEFT JOIN t_user u ON r.verify_user_id = u.user_id
            WHERE COALESCE(r.deleted, 0) = 0
            <if test="userId != null and userId != ''">AND r.reporter_id = #{userId}</if>
            <if test="status != null and status != ''">AND r.report_status = #{status}</if>
            <if test="urgencyLevel != null and urgencyLevel != ''">AND r.urgency_level = #{urgencyLevel}</if>
            <if test="keyword != null and keyword != ''">
                AND (r.report_id LIKE CONCAT('%', #{keyword}, '%')
                     OR r.reporter_name LIKE CONCAT('%', #{keyword}, '%')
                     OR r.found_place LIKE CONCAT('%', #{keyword}, '%')
                     OR r.found_area LIKE CONCAT('%', #{keyword}, '%')
                     OR r.health_description LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY r.reported_at DESC
            <if test="limit != null">LIMIT #{limit}</if>
            <if test="offset != null">OFFSET #{offset}</if>
            </script>
            """)
    List<Clue> findClues(@Param("userId") String userId,
                         @Param("status") String status,
                         @Param("urgencyLevel") String urgencyLevel,
                         @Param("keyword") String keyword,
                         @Param("limit") Integer limit,
                         @Param("offset") Integer offset);

    @Select("""
            SELECT r.report_id, r.reporter_id, r.reporter_name, r.reporter_phone, r.found_place,
                   r.found_area, r.found_time, r.photo_url, r.health_description, r.urgency_level,
                   r.report_status, r.verify_user_id, u.user_name AS verify_user_name, r.verify_result,
                   r.verify_comment, r.verify_time, r.created_cat_id, r.reported_at, r.update_time, r.deleted
            FROM t_rescue_report r
            LEFT JOIN t_user u ON r.verify_user_id = u.user_id
            WHERE r.report_id = #{reportId} AND COALESCE(r.deleted, 0) = 0
            """)
    @ConstructorArgs({
            @Arg(column = "report_id", javaType = String.class),
            @Arg(column = "report_id", javaType = String.class),
            @Arg(column = "reporter_id", javaType = String.class),
            @Arg(column = "reporter_name", javaType = String.class),
            @Arg(column = "reporter_phone", javaType = String.class),
            @Arg(column = "found_place", javaType = String.class),
            @Arg(column = "found_area", javaType = String.class),
            @Arg(column = "found_time", javaType = LocalDateTime.class),
            @Arg(column = "photo_url", javaType = String.class),
            @Arg(column = "health_description", javaType = String.class),
            @Arg(column = "urgency_level", javaType = String.class),
            @Arg(column = "report_status", javaType = String.class),
            @Arg(column = "verify_user_id", javaType = String.class),
            @Arg(column = "verify_user_name", javaType = String.class),
            @Arg(column = "verify_result", javaType = String.class),
            @Arg(column = "verify_comment", javaType = String.class),
            @Arg(column = "verify_time", javaType = LocalDateTime.class),
            @Arg(column = "created_cat_id", javaType = String.class),
            @Arg(column = "reported_at", javaType = LocalDateTime.class),
            @Arg(column = "update_time", javaType = LocalDateTime.class),
            @Arg(column = "deleted", javaType = boolean.class)
    })
    Clue findClueById(String reportId);

    @Insert("""
            INSERT INTO t_rescue_report (report_id, reporter_id, reporter_name, reporter_phone, found_place,
                                         found_area, found_time, color, gender, health_description, urgent,
                                         urgency_level, photo_url, report_status, reported_at, update_time, deleted)
            VALUES (#{id}, #{reporterId}, #{reporterName}, #{reporterPhone}, #{foundLocation},
                    #{foundArea}, #{foundTime}, NULL, 'U', #{description}, #{urgent},
                    #{urgencyLevel}, #{photoUrl}, #{status}, #{createdAt}, #{createdAt}, 0)
            """)
    void insertClue(@Param("id") String id,
                    @Param("reporterId") String reporterId,
                    @Param("reporterName") String reporterName,
                    @Param("reporterPhone") String reporterPhone,
                    @Param("foundLocation") String foundLocation,
                    @Param("foundArea") String foundArea,
                    @Param("foundTime") LocalDateTime foundTime,
                    @Param("description") String description,
                    @Param("urgent") boolean urgent,
                    @Param("urgencyLevel") String urgencyLevel,
                    @Param("photoUrl") String photoUrl,
                    @Param("status") ClueStatus status,
                    @Param("createdAt") LocalDateTime createdAt);

    @Update("""
            UPDATE t_rescue_report
            SET report_status = #{status}, verify_user_id = #{verifyUserId}, verify_result = #{verifyResult},
                verify_comment = #{verifyComment}, verify_time = #{verifyTime}, update_time = #{verifyTime}
            WHERE report_id = #{reportId}
            """)
    int updateClueVerification(@Param("reportId") String reportId,
                               @Param("status") ClueStatus status,
                               @Param("verifyUserId") String verifyUserId,
                               @Param("verifyResult") String verifyResult,
                               @Param("verifyComment") String verifyComment,
                               @Param("verifyTime") LocalDateTime verifyTime);

    @Update("""
            UPDATE t_rescue_report
            SET report_status = 'INVALID', verify_user_id = #{operatorId}, verify_result = 'INVALID',
                verify_comment = #{reason}, verify_time = #{updatedAt}, update_time = #{updatedAt}
            WHERE report_id = #{reportId}
              AND COALESCE(deleted, 0) = 0
              AND report_status <> 'CREATED_CAT'
              AND created_cat_id IS NULL
            """)
    int markInvalid(@Param("reportId") String reportId,
                    @Param("reason") String reason,
                    @Param("operatorId") String operatorId,
                    @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE t_rescue_report
            SET report_status = 'INVALID', deleted = 1, verify_user_id = #{operatorId},
                verify_result = 'INVALID', verify_comment = #{reason},
                verify_time = #{updatedAt}, update_time = #{updatedAt}
            WHERE report_id = #{reportId}
              AND COALESCE(deleted, 0) = 0
              AND report_status <> 'CREATED_CAT'
              AND created_cat_id IS NULL
            """)
    int logicalDeleteClue(@Param("reportId") String reportId,
                          @Param("reason") String reason,
                          @Param("operatorId") String operatorId,
                          @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE t_rescue_report
            SET report_status = 'CREATED_CAT', created_cat_id = #{catId}, verify_user_id = #{verifyUserId},
                verify_result = 'VALID', verify_comment = COALESCE(verify_comment, '已建档'),
                verify_time = #{verifyTime}, update_time = #{verifyTime}
            WHERE report_id = #{reportId}
            """)
    int updateCreatedCat(@Param("reportId") String reportId,
                         @Param("catId") String catId,
                         @Param("verifyUserId") String verifyUserId,
                         @Param("verifyTime") LocalDateTime verifyTime);

    @Select("SELECT COUNT(*) FROM t_rescue_report WHERE COALESCE(deleted, 0) = 0 AND report_status = #{status}")
    long countCluesByStatus(ClueStatus status);

    @Select("""
            SELECT r.report_id, r.reporter_id, r.reporter_name, r.reporter_phone, r.found_place,
                   r.found_area, r.found_time, r.photo_url, r.health_description, r.urgency_level,
                   r.report_status, r.verify_user_id, u.user_name AS verify_user_name, r.verify_result,
                   r.verify_comment, r.verify_time, r.created_cat_id, r.reported_at, r.update_time, r.deleted
            FROM t_rescue_report r
            LEFT JOIN t_user u ON r.verify_user_id = u.user_id
            WHERE r.created_cat_id = #{catId} AND COALESCE(r.deleted, 0) = 0
            ORDER BY r.reported_at ASC
            LIMIT 1
            """)
    @ConstructorArgs({
            @Arg(column = "report_id", javaType = String.class),
            @Arg(column = "report_id", javaType = String.class),
            @Arg(column = "reporter_id", javaType = String.class),
            @Arg(column = "reporter_name", javaType = String.class),
            @Arg(column = "reporter_phone", javaType = String.class),
            @Arg(column = "found_place", javaType = String.class),
            @Arg(column = "found_area", javaType = String.class),
            @Arg(column = "found_time", javaType = LocalDateTime.class),
            @Arg(column = "photo_url", javaType = String.class),
            @Arg(column = "health_description", javaType = String.class),
            @Arg(column = "urgency_level", javaType = String.class),
            @Arg(column = "report_status", javaType = String.class),
            @Arg(column = "verify_user_id", javaType = String.class),
            @Arg(column = "verify_user_name", javaType = String.class),
            @Arg(column = "verify_result", javaType = String.class),
            @Arg(column = "verify_comment", javaType = String.class),
            @Arg(column = "verify_time", javaType = LocalDateTime.class),
            @Arg(column = "created_cat_id", javaType = String.class),
            @Arg(column = "reported_at", javaType = LocalDateTime.class),
            @Arg(column = "update_time", javaType = LocalDateTime.class),
            @Arg(column = "deleted", javaType = boolean.class)
    })
    Clue findClueByCreatedCatId(String catId);
}
