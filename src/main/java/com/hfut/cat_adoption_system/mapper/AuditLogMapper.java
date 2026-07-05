package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.AuditLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuditLogMapper {
    @Select("SELECT log_id, operator, action, target_type, target_id, detail, created_at FROM t_audit_log ORDER BY created_at DESC")
    List<AuditLog> findAll();

    @Insert("INSERT INTO t_audit_log (log_id, operator, action, target_type, target_id, detail, created_at) VALUES (#{logId}, #{operator}, #{action}, #{targetType}, #{targetId}, #{detail}, #{createdAt})")
    void insert(AuditLog log);

    @Select("""
            SELECT COALESCE(MAX(CAST(SUBSTRING(log_id, LENGTH(#{prefix}) + 1) AS SIGNED)), 0)
            FROM t_audit_log
            WHERE log_id LIKE CONCAT(#{prefix}, '%')
            """)
    int maxSuffixByPrefix(String prefix);
}
