package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.dto.AgreementInfo;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 领养协议Mapper
 * 
 * 提供领养协议的数据库操作，包括协议查询、创建、更新、取消和删除等功能。
 */
@Mapper
public interface AgreementMapper {

        /**
         * 查询待处理的领养协议列表
         * 
         * @param status  协议状态（可选）
         * @param keyword 关键词（支持协议编号、猫咪ID、猫咪名称、领养人姓名模糊搜索）
         * @return 协议信息列表
         */
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
                          AND (a.apply_status = 'PENDING_HANDOVER' OR ag.id IS NOT NULL)
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

        /**
         * 根据申请ID统计有效的协议数量
         * 
         * @param applicationId 申请ID
         * @return 有效协议数量
         */
        @Select("""
                        SELECT COUNT(*) FROM adoption_agreement
                        WHERE application_id = #{applicationId} AND COALESCE(deleted, 0) = 0
                        """)
        int countActiveByApplicationId(String applicationId);

        /**
         * 统计指定猫咪在其他申请中有效的协议数量（排除当前申请）
         * 
         * @param catId         猫咪ID
         * @param applicationId 排除的申请ID
         * @return 有效协议数量
         */
        @Select("""
                        SELECT COUNT(*)
                        FROM adoption_agreement ag
                        JOIN t_application a ON a.application_id = ag.application_id
                        WHERE a.cat_id = #{catId}
                          AND ag.application_id <> #{applicationId}
                          AND ag.status IN ('DRAFT', 'GENERATED', 'HANDED_OVER')
                          AND a.apply_status IN ('PENDING_HANDOVER', 'HANDED_OVER')
                          AND COALESCE(ag.deleted, 0) = 0
                          AND COALESCE(a.deleted, 0) = 0
                        """)
        int countActiveByCatExceptApplication(@Param("catId") String catId,
                        @Param("applicationId") String applicationId);

        /**
         * 根据状态统计协议数量
         * 
         * @param status 协议状态
         * @return 协议数量
         */
        @Select("SELECT COUNT(*) FROM adoption_agreement WHERE status = #{status} AND COALESCE(deleted, 0) = 0")
        long countByStatus(String status);

        /**
         * 根据ID查询协议详情
         * 
         * @param id 协议ID
         * @return 协议信息
         */
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

        /**
         * 根据申请ID查询最新的协议
         * 
         * @param applicationId 申请ID
         * @return 最新协议信息
         */
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

        /**
         * 插入新生成的协议记录
         * 
         * @param agreementNo      协议编号
         * @param applicationId    申请ID
         * @param catId            猫咪ID
         * @param adopterId        领养人ID
         * @param agreementContent 协议内容
         * @param remark           备注
         * @param operatorId       操作人ID
         * @param generatedTime    生成时间
         * @return 影响行数
         */
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

        /**
         * 更新协议内容
         * 
         * @param id               协议ID
         * @param agreementContent 协议内容
         * @param remark           备注
         * @param operatorId       操作人ID
         * @param updatedAt        更新时间
         * @return 影响行数
         */
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

        /**
         * 标记协议已交接
         * 
         * @param id                 协议ID
         * @param handoverTime       交接时间
         * @param handoverLocation   交接地点
         * @param handoverUserId     交接人ID
         * @param adopterConfirmed   领养人是否确认
         * @param volunteerConfirmed 志愿者是否确认
         * @param remark             备注
         * @param operatorId         操作人ID
         * @param updatedAt          更新时间
         * @return 影响行数
         */
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

        /**
         * 取消协议
         * 
         * @param id         协议ID
         * @param reason     取消原因
         * @param operatorId 操作人ID
         * @param updatedAt  更新时间
         * @return 影响行数
         */
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

        /**
         * 删除协议（软删除）
         * 
         * @param id         协议ID
         * @param reason     删除原因
         * @param operatorId 操作人ID
         * @param updatedAt  更新时间
         * @return 影响行数
         */
        @Update("""
                        UPDATE adoption_agreement
                        SET deleted = 1,
                            remark = #{reason},
                            update_by = #{operatorId},
                            update_time = #{updatedAt}
                        WHERE id = #{id}
                          AND status IN ('DRAFT', 'GENERATED', 'CANCELLED')
                          AND handover_time IS NULL
                          AND COALESCE(deleted, 0) = 0
                        """)
        int deleteAgreement(@Param("id") Long id,
                        @Param("reason") String reason,
                        @Param("operatorId") String operatorId,
                        @Param("updatedAt") LocalDateTime updatedAt);
}
