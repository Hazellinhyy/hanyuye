package com.hfut.cat_adoption_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 随访记录表结构迁移组件
 * 
 * 实现 ApplicationRunner 接口，在应用启动时自动执行数据库表结构迁移任务。
 * 本类用于清理遗留的旧索引，确保数据库表结构符合最新设计。
 */
@Component
public class FollowupSchemaMigration implements ApplicationRunner {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(FollowupSchemaMigration.class);

    /** JDBC模板：用于执行SQL语句 */
    private final JdbcTemplate jdbcTemplate;

    /**
     * 构造函数：注入 JdbcTemplate
     */
    public FollowupSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 应用启动时执行迁移任务
     * 删除 followup_record 表上遗留的唯一索引 uk_followup_record_task_active
     */
    @Override
    public void run(ApplicationArguments args) {
        try {
            // 删除旧的唯一索引（该索引在新版本中已不再需要）
            jdbcTemplate.execute("DROP INDEX uk_followup_record_task_active ON followup_record");
            log.info("Dropped legacy unique index uk_followup_record_task_active from followup_record.");
        } catch (Exception ignored) {
            // 索引不存在或已删除时忽略异常，确保应用正常启动
            log.debug("Legacy follow-up unique index is absent or already migrated.");
        }
    }
}
