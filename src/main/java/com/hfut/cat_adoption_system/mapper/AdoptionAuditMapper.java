package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.dto.ApplicationAuditRecord;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 领养审核记录Mapper
 * 
 * 提供领养申请审核记录的数据库操作，包括插入审核记录和查询审核历史。
 */
@Mapper
public interface AdoptionAuditMapper {

        /**
         * 插入领养审核记录
         * 
         * @param applicationId 申请ID
         * @param auditorId     审核人ID
         * @param auditStage    审核阶段
         * @param auditResult   审核结果
         * @param auditComment  审核意见
         * @param beforeStatus  审核前状态
         * @param afterStatus   审核后状态
         * @param auditTime     审核时间
         */
        @Insert("""
                        INSERT INTO adoption_audit (application_id, auditor_id, audit_stage, audit_result,
                                                    audit_opinion, before_status, after_status, audit_time,
                                                    status, create_by, update_by, create_time, update_time)
                        VALUES (#{applicationId}, #{auditorId}, #{auditStage}, #{auditResult},
                                #{auditComment}, #{beforeStatus}, #{afterStatus}, #{auditTime},
                                'VALID', #{auditorId}, #{auditorId}, #{auditTime}, #{auditTime})
                        """)
        void insert(@Param("applicationId") String applicationId,
                        @Param("auditorId") String auditorId,
                        @Param("auditStage") String auditStage,
                        @Param("auditResult") String auditResult,
                        @Param("auditComment") String auditComment,
                        @Param("beforeStatus") String beforeStatus,
                        @Param("afterStatus") String afterStatus,
                        @Param("auditTime") LocalDateTime auditTime);

        /**
         * 根据申请ID查询审核记录列表
         * 
         * @param applicationId 申请ID
         * @return 审核记录列表，按审核时间升序排列
         */
        @ConstructorArgs({
                        @Arg(column = "id", javaType = Long.class),
                        @Arg(column = "application_id", javaType = String.class),
                        @Arg(column = "auditor_id", javaType = String.class),
                        @Arg(column = "auditor_name", javaType = String.class),
                        @Arg(column = "audit_stage", javaType = String.class),
                        @Arg(column = "audit_result", javaType = String.class),
                        @Arg(column = "audit_opinion", javaType = String.class),
                        @Arg(column = "before_status", javaType = String.class),
                        @Arg(column = "after_status", javaType = String.class),
                        @Arg(column = "audit_time", javaType = LocalDateTime.class)
        })
        @Select("""
                        SELECT a.id, a.application_id, a.auditor_id, u.user_name AS auditor_name,
                               a.audit_stage, a.audit_result, a.audit_opinion,
                               a.before_status, a.after_status, a.audit_time
                        FROM adoption_audit a
                        LEFT JOIN t_user u ON u.user_id = a.auditor_id
                        WHERE a.application_id = #{applicationId}
                        ORDER BY a.audit_time ASC, a.id ASC
                        """)
        List<ApplicationAuditRecord> findByApplicationId(String applicationId);
}