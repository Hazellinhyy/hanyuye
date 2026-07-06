package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.AdoptionApplication;
import com.hfut.cat_adoption_system.model.ApplicationStatus;
import com.hfut.cat_adoption_system.dto.ApplicationDetail;
import com.hfut.cat_adoption_system.dto.ApplicationReviewRow;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 领养申请表Mapper
 * 
 * 提供领养申请的数据库操作，包括申请的查询、创建、更新、取消等功能。
 */
@Mapper
public interface ApplicationMapper {

        /**
         * 查询所有领养申请
         * 
         * @param status 申请状态（可选）
         * @return 领养申请列表
         */
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

        /**
         * 根据ID查询领养申请
         * 
         * @param applicationId 申请ID
         * @return 领养申请对象
         */
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

        /**
         * 统计用户对指定猫咪的活跃申请数量
         * 
         * @param userId 用户ID
         * @param catId  猫咪ID
         * @return 活跃申请数量
         */
        @Select("""
                        SELECT COUNT(*) FROM t_application
                        WHERE user_id = #{userId}
                          AND cat_id = #{catId}
                          AND apply_status IN ('PENDING', 'APPROVED')
                        """)
        int countActive(@Param("userId") String userId, @Param("catId") String catId);

        /**
         * 统计用户对指定猫咪的有效申请数量
         * 
         * @param userId 用户ID
         * @param catId  猫咪ID
         * @return 有效申请数量
         */
        @Select("""
                        SELECT COUNT(*) FROM t_application
                        WHERE user_id = #{userId}
                          AND cat_id = #{catId}
                          AND COALESCE(deleted, 0) = 0
                          AND apply_status IN ('PENDING_INITIAL', 'PENDING_FINAL', 'PENDING_HANDOVER', 'HANDED_OVER', 'PENDING', 'APPROVED')
                        """)
        int countEffective(@Param("userId") String userId, @Param("catId") String catId);

        /**
         * 插入领养申请
         * 
         * @param application 领养申请对象
         */
        @Insert("""
                        INSERT INTO t_application (application_id, user_id, cat_id, housing_info, family_attitude, pet_experience,
                                                   economic_ability, promise_accepted, apply_status, applied_at)
                        VALUES (#{applicationId}, #{userId}, #{catId}, #{housingInfo}, #{familyAttitude}, #{petExperience},
                                #{economicAbility}, #{promiseAccepted}, #{status}, #{appliedAt})
                        """)
        void insert(AdoptionApplication application);

        /**
         * 插入领养申请（完整字段版本，含评分和风险等级）
         * 
         * @param applicationId     申请ID
         * @param userId            用户ID
         * @param catId             猫咪ID
         * @param livingCondition   居住条件
         * @param petExperience     养宠经验
         * @param familySupport     家庭支持度
         * @param costAffordability 经济能力
         * @param acceptFollowup    是否接受回访
         * @param commitmentText    承诺内容
         * @param extraReason       额外理由
         * @param score             评分
         * @param riskLevel         风险等级
         * @param scoreReasons      评分理由
         * @param status            申请状态
         * @param appliedAt         申请时间
         */
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

        /**
         * 插入申请评分理由记录
         * 
         * @param applicationId 申请ID
         * @param reasonOrder   理由序号
         * @param reasonText    理由内容
         * @param scoreDelta    分数变化
         */
        @Insert("""
                        INSERT INTO application_score_reason (application_id, reason_order, reason_text, score_delta)
                        VALUES (#{applicationId}, #{reasonOrder}, #{reasonText}, #{scoreDelta})
                        """)
        void insertScoreReason(@Param("applicationId") String applicationId,
                        @Param("reasonOrder") int reasonOrder,
                        @Param("reasonText") String reasonText,
                        @Param("scoreDelta") Integer scoreDelta);

        /**
         * 更新申请状态（Mis版本）
         * 
         * @param applicationId 申请ID
         * @param status        新状态
         * @param reviewNote    审核备注
         * @param operatorId    操作人ID
         * @param reviewedAt    审核时间
         * @return 影响行数
         */
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

        /**
         * 取消申请（Mis版本）
         * 
         * @param applicationId 申请ID
         * @param cancelReason  取消原因
         * @param operatorId    操作人ID
         * @param cancelledAt   取消时间
         * @return 影响行数
         */
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

        /**
         * 作废申请
         * 
         * @param applicationId 申请ID
         * @param reason        作废原因
         * @param operatorId    操作人ID
         * @param updatedAt     更新时间
         * @return 影响行数
         */
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

        /**
         * 更新审核结果
         * 
         * @param applicationId 申请ID
         * @param status        新状态
         * @param reviewNote    审核备注
         * @param interviewNote 面试备注
         * @param agreementNo   协议编号
         * @param reviewedAt    审核时间
         */
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

        /**
         * 更新交接状态
         * 
         * @param applicationId 申请ID
         * @param handedOverAt  交接时间
         */
        @Update("UPDATE t_application SET apply_status = 'HANDED_OVER', updated_at = #{handedOverAt} WHERE application_id = #{applicationId}")
        void updateHandover(@Param("applicationId") String applicationId,
                        @Param("handedOverAt") LocalDateTime handedOverAt);

        /**
         * 标记申请已交接（Mis版本）
         * 
         * @param applicationId 申请ID
         * @param reviewNote    审核备注
         * @param operatorId    操作人ID
         * @param handedOverAt  交接时间
         * @return 影响行数
         */
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

        /**
         * 更新申请为撤回状态
         * 
         * @param applicationId 申请ID
         * @param reviewedAt    撤回时间
         */
        @Update("UPDATE t_application SET apply_status = 'WITHDRAWN', updated_at = #{reviewedAt} WHERE application_id = #{applicationId}")
        void updateWithdraw(@Param("applicationId") String applicationId,
                        @Param("reviewedAt") LocalDateTime reviewedAt);

        /**
         * 拒绝该猫咪的其他待处理申请
         * 
         * @param catId                 猫咪ID
         * @param approvedApplicationId 已通过的申请ID
         * @param reviewedAt            处理时间
         * @return 影响行数
         */
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

        /**
         * 取消该猫咪的其他活跃申请（Mis版本）
         * 
         * @param catId               猫咪ID
         * @param handedApplicationId 已交接的申请ID
         * @param reason              取消原因
         * @param operatorId          操作人ID
         * @param reviewedAt          处理时间
         * @return 影响行数
         */
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

        /**
         * 统计所有申请数量
         * 
         * @return 申请总数
         */
        @Select("SELECT COUNT(*) FROM t_application WHERE COALESCE(deleted, 0) = 0")
        long countAll();

        /**
         * 按状态统计申请数量
         * 
         * @param status 申请状态
         * @return 该状态的申请数量
         */
        @Select("SELECT COUNT(*) FROM t_application WHERE COALESCE(deleted, 0) = 0 AND apply_status = #{status}")
        long countByStatus(ApplicationStatus status);

        /**
         * 按风险等级统计申请数量
         * 
         * @param riskLevel 风险等级
         * @return 该风险等级的申请数量
         */
        @Select("SELECT COUNT(*) FROM t_application WHERE COALESCE(deleted, 0) = 0 AND risk_level = #{riskLevel}")
        long countByRiskLevel(String riskLevel);

        /**
         * 统计指定猫咪的申请数量
         * 
         * @param catId 猫咪ID
         * @return 申请数量
         */
        @Select("SELECT COUNT(*) FROM t_application WHERE cat_id = #{catId}")
        long countByCatId(String catId);

        /**
         * 获取指定前缀的最大编号后缀
         * 
         * @param prefix 编号前缀
         * @return 最大后缀数值
         */
        @Select("""
                        SELECT COALESCE(MAX(CAST(SUBSTRING(application_id, LENGTH(#{prefix}) + 1) AS UNSIGNED)), 0)
                        FROM t_application
                        WHERE application_id LIKE CONCAT(#{prefix}, '%')
                        """)
        int maxSuffixByPrefix(String prefix);

        /**
         * 统计成功交接的申请数量
         * 
         * @return 成功交接的申请数量
         */
        @Select("SELECT COUNT(*) FROM t_application WHERE COALESCE(deleted, 0) = 0 AND apply_status = 'HANDED_OVER'")
        long countSuccessful();

        /**
         * 查询申请详情列表
         * 
         * @param status 申请状态（可选）
         * @return 申请详情列表
         */
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

        /**
         * 根据用户ID查询申请详情列表
         * 
         * @param userId 用户ID
         * @param status 申请状态（可选）
         * @return 申请详情列表
         */
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
        List<ApplicationDetail> findDetailsByUser(@Param("userId") String userId,
                        @Param("status") ApplicationStatus status);

        /**
         * 查询审核列表（分页）
         * 
         * @param userId    用户ID（可选）
         * @param status    申请状态（可选）
         * @param riskLevel 风险等级（可选）
         * @param keyword   关键词（可选，支持申请ID、用户名、猫咪名称、猫咪ID）
         * @param limit     每页数量（可选）
         * @param offset    偏移量（可选）
         * @return 审核列表
         */
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

        /**
         * 根据ID查询审核详情
         * 
         * @param applicationId 申请ID
         * @return 审核详情
         */
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