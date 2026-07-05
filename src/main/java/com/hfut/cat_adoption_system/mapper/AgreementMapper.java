package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.dto.AgreementInfo;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AgreementMapper {
    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "agreement_no", javaType = String.class),
            @Arg(column = "application_id", javaType = String.class),
            @Arg(column = "cat_id", javaType = String.class),
            @Arg(column = "cat_name", javaType = String.class),
            @Arg(column = "adopter_id", javaType = String.class),
            @Arg(column = "adopter_name", javaType = String.class),
            @Arg(column = "adopter_phone", javaType = String.class),
            @Arg(column = "agreement_content", javaType = String.class),
            @Arg(column = "status", javaType = String.class),
            @Arg(column = "generated_time", javaType = LocalDateTime.class),
            @Arg(column = "handover_time", javaType = LocalDateTime.class),
            @Arg(column = "handover_location", javaType = String.class),
            @Arg(column = "handover_user_id", javaType = String.class),
            @Arg(column = "handover_user_name", javaType = String.class),
            @Arg(column = "adopter_confirmed", javaType = Boolean.class),
            @Arg(column = "volunteer_confirmed", javaType = Boolean.class),
            @Arg(column = "remark", javaType = String.class),
            @Arg(column = "final_approved_at", javaType = LocalDateTime.class),
            @Arg(column = "application_status", javaType = String.class)
    })
    @Select("""
            <script>
            SELECT ag.id, ag.agreement_no, a.application_id, a.cat_id, c.cat_name,
                   a.user_id AS adopter_id,
                   u.user_name AS adopter_name, u.phone AS adopter_phone,
                   ag.agreement_content,
                   COALESCE(ag.status, 'NOT_GENERATED') AS status,
                   ag.generated_time, ag.handover_time,
                   ag.handover_location,
                   ag.handover_user_id,
                   hu.user_name AS handover_user_name,
                   ag.adopter_confirmed, ag.volunteer_confirmed, ag.remark,
                   aa.audit_time AS final_approved_at, a.apply_status AS application_status
            FROM t_application a
            JOIN t_cat c ON c.cat_id = a.cat_id
            JOIN t_user u ON u.user_id = a.user_id
            LEFT JOIN adoption_agreement ag ON ag.application_id = a.application_id AND COALESCE(ag.deleted, 0) = 0
            LEFT JOIN adoption_audit aa ON aa.application_id = a.application_id AND aa.audit_stage = 'FINAL'
                 AND aa.audit_result = 'APPROVED' AND COALESCE(aa.deleted, 0) = 0
            LEFT JOIN t_user hu ON hu.user_id = ag.handover_user_id
            WHERE COALESCE(a.deleted, 0) = 0
              AND (a.apply_status IN ('PENDING_HANDOVER', 'HANDED_OVER') OR ag.id IS NOT NULL)
            <if test="status != null and status != ''">
              AND COALESCE(ag.status, 'NOT_GENERATED') = #{status}
            </if>
            <if test="keyword != null and keyword != ''">
              AND (a.application_id LIKE CONCAT('%', #{keyword}, '%')
                   OR ag.agreement_no LIKE CONCAT('%', #{keyword}, '%')
                   OR a.cat_id LIKE CONCAT('%', #{keyword}, '%')
                   OR c.cat_name LIKE CONCAT('%', #{keyword}, '%')
                   OR u.user_name LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            ORDER BY COALESCE(aa.audit_time, a.applied_at) DESC, a.applied_at DESC
            </script>
            """)
    List<AgreementInfo> findPending(@Param("status") String status, @Param("keyword") String keyword);

    @Select("""
            SELECT COUNT(*) FROM adoption_agreement
            WHERE application_id = #{applicationId} AND COALESCE(deleted, 0) = 0
            """)
    int countActiveByApplicationId(String applicationId);

    @Select("SELECT COUNT(*) FROM adoption_agreement WHERE status = #{status} AND COALESCE(deleted, 0) = 0")
    long countByStatus(String status);

    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "agreement_no", javaType = String.class),
            @Arg(column = "application_id", javaType = String.class),
            @Arg(column = "cat_id", javaType = String.class),
            @Arg(column = "cat_name", javaType = String.class),
            @Arg(column = "adopter_id", javaType = String.class),
            @Arg(column = "adopter_name", javaType = String.class),
            @Arg(column = "adopter_phone", javaType = String.class),
            @Arg(column = "agreement_content", javaType = String.class),
            @Arg(column = "status", javaType = String.class),
            @Arg(column = "generated_time", javaType = LocalDateTime.class),
            @Arg(column = "handover_time", javaType = LocalDateTime.class),
            @Arg(column = "handover_location", javaType = String.class),
            @Arg(column = "handover_user_id", javaType = String.class),
            @Arg(column = "handover_user_name", javaType = String.class),
            @Arg(column = "adopter_confirmed", javaType = Boolean.class),
            @Arg(column = "volunteer_confirmed", javaType = Boolean.class),
            @Arg(column = "remark", javaType = String.class),
            @Arg(column = "final_approved_at", javaType = LocalDateTime.class),
            @Arg(column = "application_status", javaType = String.class)
    })
    @Select("""
            SELECT ag.id, ag.agreement_no, ag.application_id, a.cat_id, c.cat_name,
                   a.user_id AS adopter_id,
                   u.user_name AS adopter_name, u.phone AS adopter_phone,
                   ag.agreement_content,
                   ag.status, ag.generated_time, ag.handover_time,
                   ag.handover_location,
                   ag.handover_user_id, hu.user_name AS handover_user_name,
                   ag.adopter_confirmed, ag.volunteer_confirmed, ag.remark,
                   aa.audit_time AS final_approved_at, a.apply_status AS application_status
            FROM adoption_agreement ag
            JOIN t_application a ON a.application_id = ag.application_id
            JOIN t_cat c ON c.cat_id = a.cat_id
            JOIN t_user u ON u.user_id = a.user_id
            LEFT JOIN adoption_audit aa ON aa.application_id = a.application_id AND aa.audit_stage = 'FINAL'
                 AND aa.audit_result = 'APPROVED' AND COALESCE(aa.deleted, 0) = 0
            LEFT JOIN t_user hu ON hu.user_id = ag.handover_user_id
            WHERE ag.id = #{id} AND COALESCE(ag.deleted, 0) = 0
            """)
    AgreementInfo findById(Long id);

    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "agreement_no", javaType = String.class),
            @Arg(column = "application_id", javaType = String.class),
            @Arg(column = "cat_id", javaType = String.class),
            @Arg(column = "cat_name", javaType = String.class),
            @Arg(column = "adopter_id", javaType = String.class),
            @Arg(column = "adopter_name", javaType = String.class),
            @Arg(column = "adopter_phone", javaType = String.class),
            @Arg(column = "agreement_content", javaType = String.class),
            @Arg(column = "status", javaType = String.class),
            @Arg(column = "generated_time", javaType = LocalDateTime.class),
            @Arg(column = "handover_time", javaType = LocalDateTime.class),
            @Arg(column = "handover_location", javaType = String.class),
            @Arg(column = "handover_user_id", javaType = String.class),
            @Arg(column = "handover_user_name", javaType = String.class),
            @Arg(column = "adopter_confirmed", javaType = Boolean.class),
            @Arg(column = "volunteer_confirmed", javaType = Boolean.class),
            @Arg(column = "remark", javaType = String.class),
            @Arg(column = "final_approved_at", javaType = LocalDateTime.class),
            @Arg(column = "application_status", javaType = String.class)
    })
    @Select("""
            SELECT ag.id, ag.agreement_no, ag.application_id, a.cat_id, c.cat_name,
                   a.user_id AS adopter_id,
                   u.user_name AS adopter_name, u.phone AS adopter_phone,
                   ag.agreement_content,
                   ag.status, ag.generated_time, ag.handover_time,
                   ag.handover_location,
                   ag.handover_user_id, hu.user_name AS handover_user_name,
                   ag.adopter_confirmed, ag.volunteer_confirmed, ag.remark,
                   aa.audit_time AS final_approved_at, a.apply_status AS application_status
            FROM adoption_agreement ag
            JOIN t_application a ON a.application_id = ag.application_id
            JOIN t_cat c ON c.cat_id = a.cat_id
            JOIN t_user u ON u.user_id = a.user_id
            LEFT JOIN adoption_audit aa ON aa.application_id = a.application_id AND aa.audit_stage = 'FINAL'
                 AND aa.audit_result = 'APPROVED' AND COALESCE(aa.deleted, 0) = 0
            LEFT JOIN t_user hu ON hu.user_id = ag.handover_user_id
            WHERE ag.application_id = #{applicationId} AND COALESCE(ag.deleted, 0) = 0
            ORDER BY ag.id DESC LIMIT 1
            """)
    AgreementInfo findByApplicationId(String applicationId);

    @Insert("""
            INSERT INTO adoption_agreement
            (agreement_no, application_id, agreement_content, status, generated_time, remark,
             deleted, create_by, update_by, create_time, update_time)
            VALUES
            (#{agreementNo}, #{applicationId}, #{agreementContent}, 'GENERATED', #{generatedTime}, #{remark},
             0, #{operatorId}, #{operatorId}, #{generatedTime}, #{generatedTime})
            """)
    int insertGenerated(@Param("agreementNo") String agreementNo,
                        @Param("applicationId") String applicationId,
                        @Param("catId") String catId,
                        @Param("adopterId") String adopterId,
                        @Param("agreementContent") String agreementContent,
                        @Param("remark") String remark,
                        @Param("operatorId") String operatorId,
                        @Param("generatedTime") LocalDateTime generatedTime);

    @Update("""
            UPDATE adoption_agreement
            SET agreement_content = #{agreementContent},
                remark = #{remark}, update_by = #{operatorId}, update_time = #{updatedAt}
            WHERE id = #{id} AND status IN ('DRAFT', 'GENERATED') AND COALESCE(deleted, 0) = 0
            """)
    int updateContent(@Param("id") Long id,
                      @Param("agreementContent") String agreementContent,
                      @Param("remark") String remark,
                      @Param("operatorId") String operatorId,
                      @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE adoption_agreement
            SET status = 'HANDED_OVER',
                handover_time = #{handoverTime},
                handover_location = #{handoverLocation},
                handover_user_id = #{handoverUserId},
                adopter_confirmed = #{adopterConfirmed},
                volunteer_confirmed = #{volunteerConfirmed},
                remark = #{remark},
                update_by = #{operatorId},
                update_time = #{updatedAt}
            WHERE id = #{id} AND status = 'GENERATED' AND COALESCE(deleted, 0) = 0
            """)
    int markHandedOver(@Param("id") Long id,
                       @Param("handoverTime") LocalDateTime handoverTime,
                       @Param("handoverLocation") String handoverLocation,
                       @Param("handoverUserId") String handoverUserId,
                       @Param("adopterConfirmed") boolean adopterConfirmed,
                       @Param("volunteerConfirmed") boolean volunteerConfirmed,
                       @Param("remark") String remark,
                       @Param("operatorId") String operatorId,
                       @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            UPDATE adoption_agreement
            SET status = 'CANCELLED',
                remark = #{reason},
                update_by = #{operatorId},
                update_time = #{updatedAt}
            WHERE id = #{id}
              AND status IN ('DRAFT', 'GENERATED')
              AND handover_time IS NULL
              AND COALESCE(deleted, 0) = 0
            """)
    int cancelAgreement(@Param("id") Long id,
                        @Param("reason") String reason,
                        @Param("operatorId") String operatorId,
                        @Param("updatedAt") LocalDateTime updatedAt);
}
