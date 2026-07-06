package com.hfut.cat_adoption_system.mapper;

import com.hfut.cat_adoption_system.model.AuditLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 审计日志Mapper
 * 
 * 提供系统审计日志的数据库操作，包括日志查询和插入功能。
 */
@Mapper
public interface AuditLogMapper {

    /**
     * 查询所有审计日志，按创建时间倒序排列
     * 
     * @return 审计日志列表
     */
    @Select("SELECT log_id, operator, action, target_type, target_id, detail, created_at FROM t_audit_log ORDER BY created_at DESC")
    List<AuditLog> findAll();

    /**
     * 插入审计日志记录
     * 
     * @param log 审计日志对象
     */
    @Insert("INSERT INTO t_audit_log (log_id, operator, action, target_type, target_id, detail, created_at) VALUES (#{logId}, #{operator}, #{action}, #{targetType}, #{targetId}, #{detail}, #{createdAt})")
    void insert(AuditLog log);

    /**
     * 获取指定前缀的最大日志编号后缀
     * 
     * @param prefix 编号前缀
     * @return 最大后缀数值
     */
    @Select("""
            SELECT COALESCE(MAX(CAST(SUBSTRING(log_id, LENGTH(#{prefix}) + 1) AS SIGNED)), 0)
            FROM t_audit_log
            WHERE log_id LIKE CONCAT(#{prefix}, '%')
            """)
    int maxSuffixByPrefix(String prefix);
}