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

@Mapper
public interface AdoptionAuditMapper {
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
