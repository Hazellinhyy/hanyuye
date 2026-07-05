package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.AdoptionApplication;
import com.hfut.cat_adoption_system.model.ApplicationStatus;
import com.hfut.cat_adoption_system.dto.ApplicationDetail;
import com.hfut.cat_adoption_system.dto.ApplicationReviewRow;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ApplicationMapper {
    @Select("""
            <script>
            SELECT application_id, user_id, cat_id, housing_info, family_attitude, pet_experience, economic_ability,
                   promise_accepted, apply_status AS status,
                   (SELECT aa.audit_opinion FROM adoption_audit aa WHERE aa.application_id = t_application.application_id AND COALESCE(aa.deleted,0)=0 ORDER BY aa.audit_time DESC, aa.id DESC LIMIT 1) AS review_note,
                   NULL AS interview_note,
                   (SELECT ag.agreement_no FROM adoption_agreement ag WHERE ag.application_id = t_application.application_id AND COALESCE(ag.deleted,0)=0 ORDER BY ag.id DESC LIMIT 1) AS agreement_no,
                   applied_at,
                   (SELECT aa.audit_time FROM adoption_audit aa WHERE aa.application_id = t_application.application_id AND COALESCE(aa.deleted,0)=0 ORDER BY aa.audit_time DESC, aa.id DESC LIMIT 1) AS reviewed_at,
                   (SELECT ag.handover_time FROM adoption_agreement ag WHERE ag.application_id = t_application.application_id AND COALESCE(ag.deleted,0)=0 ORDER BY ag.id DESC LIMIT 1) AS handed_over_at
            FROM t_application
            WHERE 1 = 1
            <if test="status != null">AND apply_status = #{status}</if>
            ORDER BY applied_at DESC
            </script>
            """)
    List<AdoptionApplication> findAll(@Param("status") ApplicationStatus status);

    @Select("""
            SELECT application_id, user_id, cat_id, housing_info, family_attitude, pet_experience, economic_ability,
                   promise_accepted, apply_status AS status,
                   (SELECT aa.audit_opinion FROM adoption_audit aa WHERE aa.application_id = t_application.application_id AND COALESCE(aa.deleted,0)=0 ORDER BY aa.audit_time DESC, aa.id DESC LIMIT 1) AS review_note,
                   NULL AS interview_note,
                   (SELECT ag.agreement_no FROM adoption_agreement ag WHERE ag.application_id = t_application.application_id AND COALESCE(ag.deleted,0)=0 ORDER BY ag.id DESC LIMIT 1) AS agreement_no,
                   applied_at,
                   (SELECT aa.audit_time FROM adoption_audit aa WHERE aa.application_id = t_application.application_id AND COALESCE(aa.deleted,0)=0 ORDER BY aa.audit_time DESC, aa.id DESC LIMIT 1) AS reviewed_at,
                   (SELECT ag.handover_time FROM adoption_agreement ag WHERE ag.application_id = t_application.application_id AND COALESCE(ag.deleted,0)=0 ORDER BY ag.id DESC LIMIT 1) AS handed_over_at
            FROM t_application WHERE application_id = #{applicationId}
            """)
    AdoptionApplication findById(String applicationId);

    @Select("""
            SELECT COUNT(*) FROM t_application
            WHERE user_id = #{userId}
              AND cat_id = #{catId}
              AND apply_status IN ('PENDING', 'APPROVED')
            """)
    int countActive(@Param("userId") String userId, @Param("catId") String catId);

    @Select("""
            SELECT COUNT(*) FROM t_application
            WHERE user_id = #{userId}
              AND cat_id = #{catId}
              AND COALESCE(deleted, 0) = 0
              AND apply_status IN ('PENDING_INITIAL', 'PENDING_FINAL', 'PENDING_HANDOVER', 'HANDED_OVER', 'PENDING', 'APPROVED')
            """)
    int countEffective(@Param("userId") String userId, @Param("catId") String catId);

    @Insert("""
            INSERT INTO t_application (application_id, user_id, cat_id, housing_info, family_attitude, pet_experience,
                                       economic_ability, promise_accepted, apply_status, applied_at)
            VALUES (#{applicationId}, #{userId}, #{catId}, #{housingInfo}, #{familyAttitude}, #{petExperience},
                    #{economicAbility}, #{promiseAccepted}, #{status}, #{appliedAt})
            """)
    void insert(AdoptionApplication application);

    @Insert("""
            INSERT INTO t_application (application_id, user_id, cat_id, housing_info, family_attitude, pet_experience,
                                       economic_ability, promise_accepted, commitment_text, extra_reason,
                                       score, risk_level, apply_status, applied_at, create_by, update_by,
                                       updated_at, deleted)
            VALUES (#{applicationId}, #{userId}, #{catId}, #{livingCondition}, #{familySupport}, #{petExperience},
                    #{costAffordability}, #{acceptFollowup}, #{commitmentText}, #{extraReason},
                    #{score}, #{riskLevel}, #{status}, #{appliedAt}, #{userId}, #{userId},
                    #{appliedAt}, 0)
            """)
    void insertMis(@Param("applicationId") String applicationId,
                   @Param("userId") String userId,
                   @Param("catId") String catId,
                   @Param("livingCondition") String livingCondition,
                   @Param("petExperience") String petExperience,
                   @Param("familySupport") String familySupport,
                   @Param("costAffordability") String costAffordability,
                   @Param("acceptFollowup") boolean acceptFollowup,
                   @Param("commitmentText") String commitmentText,
                   @Param("extraReason") String extraReason,
                   @Param("score") int score,
                   @Param("riskLevel") String riskLevel,
                   @Param("scoreReasons") String scoreReasons,
                   @Param("status") ApplicationStatus status,
                   @Param("appliedAt") LocalDateTime appliedAt);

    @Insert("""
            INSERT INTO application_score_reason (application_id, reason_order, reason_text, score_delta)
            VALUES (#{applicationId}, #{reasonOrder}, #{reasonText}, #{scoreDelta})
            """)
    void insertScoreReason(@Param("applicationId") String applicationId,
                           @Param("reasonOrder") int reasonOrder,
                           @Param("reasonText") String reasonText,
                           @Param("scoreDelta") Integer scoreDelta);

    @Update("""
            UPDATE t_application
            SET apply_status = #{status}, update_by = #{operatorId}, updated_at = #{reviewedAt}
            WHERE application_id = #{applicationId}
            """)
    int updateMisStatus(@Param("applicationId") String applicationId,
                        @Param("status") ApplicationStatus status,
                        @Param("reviewNote") String reviewNote,
                        @Param("operatorId") String operatorId,
                        @Param("reviewedAt") LocalDateTime reviewedAt);

    @Update("""
            UPDATE t_application
            SET apply_status = 'CANCELLED', cancel_reason = #{cancelReason},
                update_by = #{operatorId}, updated_at = #{cancelledAt}
            WHERE application_id = #{applicationId}
            """)
    int cancelMis(@Param("applicationId") String applicationId,
                  @Param("cancelReason") String cancelReason,
                  @Param("operatorId") String operatorId,
                  @Param("cancelledAt") LocalDateTime cancelledAt);

    @Update("""
            UPDATE t_application
            SET apply_status = 'CANCELLED', cancel_reason = #{reason},
                update_by = #{operatorId}, updated_at = #{updatedAt}
            WHERE application_id = #{applicationId}
              AND COALESCE(deleted, 0) = 0
              AND apply_status <> 'HANDED_OVER'
            """)
    int voidApplication(@Param("applicationId") String applicationId,
                        @Param("reason") String reason,
                        @Param("operatorId") String operatorId,
                        @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE t_application
            SET apply_status = #{status}, updated_at = #{reviewedAt}
            WHERE application_id = #{applicationId}
            """)
    void updateReview(@Param("applicationId") String applicationId,
                      @Param("status") ApplicationStatus status,
                      @Param("reviewNote") String reviewNote,
                      @Param("interviewNote") String interviewNote,
                      @Param("agreementNo") String agreementNo,
                      @Param("reviewedAt") LocalDateTime reviewedAt);

    @Update("UPDATE t_application SET apply_status = 'HANDED_OVER', updated_at = #{handedOverAt} WHERE application_id = #{applicationId}")
    void updateHandover(@Param("applicationId") String applicationId, @Param("handedOverAt") LocalDateTime handedOverAt);

    @Update("""
            UPDATE t_application
            SET apply_status = 'HANDED_OVER', update_by = #{operatorId}, updated_at = #{handedOverAt}
            WHERE application_id = #{applicationId}
              AND apply_status = 'PENDING_HANDOVER'
              AND COALESCE(deleted, 0) = 0
            """)
    int markMisHandedOver(@Param("applicationId") String applicationId,
                          @Param("reviewNote") String reviewNote,
                          @Param("operatorId") String operatorId,
                          @Param("handedOverAt") LocalDateTime handedOverAt);

    @Update("UPDATE t_application SET apply_status = 'WITHDRAWN', updated_at = #{reviewedAt} WHERE application_id = #{applicationId}")
    void updateWithdraw(@Param("applicationId") String applicationId, @Param("reviewedAt") LocalDateTime reviewedAt);

    @Update("""
            UPDATE t_application
            SET apply_status = 'REJECTED',
                updated_at = #{reviewedAt}
            WHERE cat_id = #{catId}
              AND application_id <> #{approvedApplicationId}
              AND apply_status = 'PENDING'
            """)
    int rejectOtherPending(@Param("catId") String catId,
                           @Param("approvedApplicationId") String approvedApplicationId,
                           @Param("reviewedAt") LocalDateTime reviewedAt);

    @Update("""
            UPDATE t_application
            SET apply_status = 'CANCELLED',
                cancel_reason = #{reason},
                update_by = #{operatorId},
                updated_at = #{reviewedAt}
            WHERE cat_id = #{catId}
              AND application_id <> #{handedApplicationId}
              AND COALESCE(deleted, 0) = 0
              AND apply_status IN ('PENDING_INITIAL', 'PENDING_FINAL', 'PENDING_HANDOVER')
            """)
    int cancelOtherMisActive(@Param("catId") String catId,
                             @Param("handedApplicationId") String handedApplicationId,
                             @Param("reason") String reason,
                             @Param("operatorId") String operatorId,
                             @Param("reviewedAt") LocalDateTime reviewedAt);

    @Select("SELECT COUNT(*) FROM t_application WHERE COALESCE(deleted, 0) = 0")
    long countAll();

    @Select("SELECT COUNT(*) FROM t_application WHERE COALESCE(deleted, 0) = 0 AND apply_status = #{status}")
    long countByStatus(ApplicationStatus status);

    @Select("SELECT COUNT(*) FROM t_application WHERE COALESCE(deleted, 0) = 0 AND risk_level = #{riskLevel}")
    long countByRiskLevel(String riskLevel);

    @Select("SELECT COUNT(*) FROM t_application WHERE cat_id = #{catId}")
    long countByCatId(String catId);

    @Select("""
            SELECT COALESCE(MAX(CAST(SUBSTRING(application_id, LENGTH(#{prefix}) + 1) AS UNSIGNED)), 0)
            FROM t_application
            WHERE application_id LIKE CONCAT(#{prefix}, '%')
            """)
    int maxSuffixByPrefix(String prefix);

    @Select("SELECT COUNT(*) FROM t_application WHERE COALESCE(deleted, 0) = 0 AND apply_status = 'HANDED_OVER'")
    long countSuccessful();

    @Select("""
            <script>
            SELECT a.application_id, a.user_id, u.user_name, u.phone, a.cat_id, c.cat_name, c.cover_url,
                   a.housing_info, a.family_attitude, a.pet_experience, a.economic_ability, a.promise_accepted,
                   a.apply_status AS status,
                   (SELECT aa.audit_opinion FROM adoption_audit aa WHERE aa.application_id = a.application_id AND COALESCE(aa.deleted,0)=0 ORDER BY aa.audit_time DESC, aa.id DESC LIMIT 1) AS review_note,
                   NULL AS interview_note,
                   (SELECT ag.agreement_no FROM adoption_agreement ag WHERE ag.application_id = a.application_id AND COALESCE(ag.deleted,0)=0 ORDER BY ag.id DESC LIMIT 1) AS agreement_no,
                   a.applied_at,
                   (SELECT aa.audit_time FROM adoption_audit aa WHERE aa.application_id = a.application_id AND COALESCE(aa.deleted,0)=0 ORDER BY aa.audit_time DESC, aa.id DESC LIMIT 1) AS reviewed_at,
                   (SELECT ag.handover_time FROM adoption_agreement ag WHERE ag.application_id = a.application_id AND COALESCE(ag.deleted,0)=0 ORDER BY ag.id DESC LIMIT 1) AS handed_over_at
            FROM t_application a
            JOIN t_user u ON u.user_id = a.user_id
            JOIN t_cat c ON c.cat_id = a.cat_id
            WHERE 1 = 1
            <if test="status != null">AND a.apply_status = #{status}</if>
            ORDER BY a.applied_at DESC
            </script>
            """)
    List<ApplicationDetail> findDetails(@Param("status") ApplicationStatus status);

    @Select("""
            <script>
            SELECT a.application_id, a.user_id, u.user_name, u.phone, a.cat_id, c.cat_name, c.cover_url,
                   a.housing_info, a.family_attitude, a.pet_experience, a.economic_ability, a.promise_accepted,
                   a.apply_status AS status,
                   (SELECT aa.audit_opinion FROM adoption_audit aa WHERE aa.application_id = a.application_id AND COALESCE(aa.deleted,0)=0 ORDER BY aa.audit_time DESC, aa.id DESC LIMIT 1) AS review_note,
                   NULL AS interview_note,
                   (SELECT ag.agreement_no FROM adoption_agreement ag WHERE ag.application_id = a.application_id AND COALESCE(ag.deleted,0)=0 ORDER BY ag.id DESC LIMIT 1) AS agreement_no,
                   a.applied_at,
                   (SELECT aa.audit_time FROM adoption_audit aa WHERE aa.application_id = a.application_id AND COALESCE(aa.deleted,0)=0 ORDER BY aa.audit_time DESC, aa.id DESC LIMIT 1) AS reviewed_at,
                   (SELECT ag.handover_time FROM adoption_agreement ag WHERE ag.application_id = a.application_id AND COALESCE(ag.deleted,0)=0 ORDER BY ag.id DESC LIMIT 1) AS handed_over_at
            FROM t_application a
            JOIN t_user u ON u.user_id = a.user_id
            JOIN t_cat c ON c.cat_id = a.cat_id
            WHERE a.user_id = #{userId}
            <if test="status != null">AND a.apply_status = #{status}</if>
            ORDER BY a.applied_at DESC
            </script>
            """)
    List<ApplicationDetail> findDetailsByUser(@Param("userId") String userId, @Param("status") ApplicationStatus status);

    @ConstructorArgs({
            @Arg(column = "application_id", javaType = String.class),
            @Arg(column = "user_id", javaType = String.class),
            @Arg(column = "user_name", javaType = String.class),
            @Arg(column = "phone", javaType = String.class),
            @Arg(column = "cat_id", javaType = String.class),
            @Arg(column = "cat_name", javaType = String.class),
            @Arg(column = "cover_url", javaType = String.class),
            @Arg(column = "housing_info", javaType = String.class),
            @Arg(column = "pet_experience", javaType = String.class),
            @Arg(column = "family_attitude", javaType = String.class),
            @Arg(column = "economic_ability", javaType = String.class),
            @Arg(column = "promise_accepted", javaType = boolean.class),
            @Arg(column = "commitment_text", javaType = String.class),
            @Arg(column = "extra_reason", javaType = String.class),
            @Arg(column = "score", javaType = Integer.class),
            @Arg(column = "risk_level", javaType = String.class),
            @Arg(column = "score_reasons", javaType = String.class),
            @Arg(column = "apply_status", javaType = ApplicationStatus.class),
            @Arg(column = "review_note", javaType = String.class),
            @Arg(column = "applied_at", javaType = LocalDateTime.class),
            @Arg(column = "reviewed_at", javaType = LocalDateTime.class)
    })
    @Select("""
            <script>
            SELECT a.application_id, a.user_id, u.user_name, u.phone, a.cat_id, c.cat_name, c.cover_url,
                   a.housing_info, a.pet_experience, a.family_attitude, a.economic_ability, a.promise_accepted,
                   a.commitment_text, a.extra_reason, a.score, a.risk_level,
                   (SELECT GROUP_CONCAT(asr.reason_text ORDER BY asr.reason_order SEPARATOR '；') FROM application_score_reason asr WHERE asr.application_id = a.application_id) AS score_reasons,
                   a.apply_status,
                   (SELECT aa.audit_opinion FROM adoption_audit aa WHERE aa.application_id = a.application_id AND COALESCE(aa.deleted,0)=0 ORDER BY aa.audit_time DESC, aa.id DESC LIMIT 1) AS review_note,
                   a.applied_at,
                   (SELECT aa.audit_time FROM adoption_audit aa WHERE aa.application_id = a.application_id AND COALESCE(aa.deleted,0)=0 ORDER BY aa.audit_time DESC, aa.id DESC LIMIT 1) AS reviewed_at
            FROM t_application a
            JOIN t_user u ON u.user_id = a.user_id
            JOIN t_cat c ON c.cat_id = a.cat_id
            WHERE COALESCE(a.deleted, 0) = 0
            <if test="userId != null and userId != ''">AND a.user_id = #{userId}</if>
            <if test="status != null">AND a.apply_status = #{status}</if>
            <if test="riskLevel != null and riskLevel != ''">AND a.risk_level = #{riskLevel}</if>
            <if test="keyword != null and keyword != ''">
                AND (a.application_id LIKE CONCAT('%', #{keyword}, '%')
                     OR u.user_name LIKE CONCAT('%', #{keyword}, '%')
                     OR c.cat_name LIKE CONCAT('%', #{keyword}, '%')
                     OR c.cat_id LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY a.applied_at DESC
            <if test="limit != null">LIMIT #{limit}</if>
            <if test="offset != null">OFFSET #{offset}</if>
            </script>
            """)
    List<ApplicationReviewRow> findReviewRows(@Param("userId") String userId,
                                              @Param("status") ApplicationStatus status,
                                              @Param("riskLevel") String riskLevel,
                                              @Param("keyword") String keyword,
                                              @Param("limit") Integer limit,
                                              @Param("offset") Integer offset);

    @ConstructorArgs({
            @Arg(column = "application_id", javaType = String.class),
            @Arg(column = "user_id", javaType = String.class),
            @Arg(column = "user_name", javaType = String.class),
            @Arg(column = "phone", javaType = String.class),
            @Arg(column = "cat_id", javaType = String.class),
            @Arg(column = "cat_name", javaType = String.class),
            @Arg(column = "cover_url", javaType = String.class),
            @Arg(column = "housing_info", javaType = String.class),
            @Arg(column = "pet_experience", javaType = String.class),
            @Arg(column = "family_attitude", javaType = String.class),
            @Arg(column = "economic_ability", javaType = String.class),
            @Arg(column = "promise_accepted", javaType = boolean.class),
            @Arg(column = "commitment_text", javaType = String.class),
            @Arg(column = "extra_reason", javaType = String.class),
            @Arg(column = "score", javaType = Integer.class),
            @Arg(column = "risk_level", javaType = String.class),
            @Arg(column = "score_reasons", javaType = String.class),
            @Arg(column = "apply_status", javaType = ApplicationStatus.class),
            @Arg(column = "review_note", javaType = String.class),
            @Arg(column = "applied_at", javaType = LocalDateTime.class),
            @Arg(column = "reviewed_at", javaType = LocalDateTime.class)
    })
    @Select("""
            SELECT a.application_id, a.user_id, u.user_name, u.phone, a.cat_id, c.cat_name, c.cover_url,
                   a.housing_info, a.pet_experience, a.family_attitude, a.economic_ability, a.promise_accepted,
                   a.commitment_text, a.extra_reason, a.score, a.risk_level,
                   (SELECT GROUP_CONCAT(asr.reason_text ORDER BY asr.reason_order SEPARATOR '；') FROM application_score_reason asr WHERE asr.application_id = a.application_id) AS score_reasons,
                   a.apply_status,
                   (SELECT aa.audit_opinion FROM adoption_audit aa WHERE aa.application_id = a.application_id AND COALESCE(aa.deleted,0)=0 ORDER BY aa.audit_time DESC, aa.id DESC LIMIT 1) AS review_note,
                   a.applied_at,
                   (SELECT aa.audit_time FROM adoption_audit aa WHERE aa.application_id = a.application_id AND COALESCE(aa.deleted,0)=0 ORDER BY aa.audit_time DESC, aa.id DESC LIMIT 1) AS reviewed_at
            FROM t_application a
            JOIN t_user u ON u.user_id = a.user_id
            JOIN t_cat c ON c.cat_id = a.cat_id
            WHERE a.application_id = #{applicationId} AND COALESCE(a.deleted, 0) = 0
            """)
    ApplicationReviewRow findReviewRowById(String applicationId);
}
