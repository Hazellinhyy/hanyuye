package com.hfut.cat_adoption_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class NormalizationSchemaMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(NormalizationSchemaMigration.class);

    private final JdbcTemplate jdbcTemplate;

    public NormalizationSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 启动时检查基础表是否存在，避免测试库或空库启动时执行三范式补表失败。
        if (!tableExists("t_notice")) {
            log.debug("Skip normalization migration because base tables are not initialized yet.");
            return;
        }
        // 先创建拆分后的关联表，再把旧字段中的逗号分隔值迁移为多行关系数据。
        createTables();
        backfillCatTags();
        backfillArticleTags();
        backfillNoticeTargetRoles();
        backfillApplicationRiskTags();
        backfillAuditRiskTags();
    }

    private void createTables() {
        // 三范式拆分表：公告可见角色、申请风险标签、审核风险标签均从主表中独立出来。
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS notice_target_role (
                    notice_id varchar(12) NOT NULL,
                    role_code varchar(20) NOT NULL,
                    PRIMARY KEY (notice_id, role_code),
                    CONSTRAINT fk_notice_target_role_notice
                        FOREIGN KEY (notice_id) REFERENCES t_notice (notice_id)
                        ON DELETE CASCADE
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS application_risk_tag (
                    application_id varchar(12) NOT NULL,
                    tag_name varchar(40) NOT NULL,
                    PRIMARY KEY (application_id, tag_name),
                    CONSTRAINT fk_application_risk_tag_application
                        FOREIGN KEY (application_id) REFERENCES t_application (application_id)
                        ON DELETE CASCADE
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS adoption_audit_risk_tag (
                    audit_id bigint NOT NULL,
                    tag_name varchar(40) NOT NULL,
                    PRIMARY KEY (audit_id, tag_name),
                    CONSTRAINT fk_audit_risk_tag_audit
                        FOREIGN KEY (audit_id) REFERENCES adoption_audit (id)
                        ON DELETE CASCADE
                )
                """);
    }

    private void backfillCatTags() {
        if (!columnExists("t_cat", "tags")) {
            return;
        }
        backfill("cat tags", """
                SELECT cat_id AS owner_id, tags AS values_text
                FROM t_cat
                WHERE tags IS NOT NULL AND TRIM(tags) <> ''
                """, "INSERT IGNORE INTO cat_tag (cat_id, tag_name) VALUES (?, ?)");
    }

    private void backfillArticleTags() {
        if (!columnExists("t_article", "tags")) {
            return;
        }
        backfill("article tags", """
                SELECT article_id AS owner_id, tags AS values_text
                FROM t_article
                WHERE tags IS NOT NULL AND TRIM(tags) <> ''
                """, "INSERT IGNORE INTO article_tag (article_id, tag_name) VALUES (?, ?)");
    }

    private void backfillNoticeTargetRoles() {
        if (!columnExists("t_notice", "target_roles")) {
            return;
        }
        backfill("notice target roles", """
                SELECT notice_id AS owner_id,
                       COALESCE(NULLIF(TRIM(target_roles), ''), 'STUDENT,VOLUNTEER,HOSPITAL,ADMIN') AS values_text
                FROM t_notice
                WHERE COALESCE(deleted, 0) = 0
                """, "INSERT IGNORE INTO notice_target_role (notice_id, role_code) VALUES (?, ?)");
    }

    private void backfillApplicationRiskTags() {
        if (!columnExists("t_application", "risk_tags")) {
            return;
        }
        backfill("application risk tags", """
                SELECT application_id AS owner_id, risk_tags AS values_text
                FROM t_application
                WHERE risk_tags IS NOT NULL AND TRIM(risk_tags) <> ''
                """, "INSERT IGNORE INTO application_risk_tag (application_id, tag_name) VALUES (?, ?)");
    }

    private void backfillAuditRiskTags() {
        if (!columnExists("adoption_audit", "risk_tags")) {
            return;
        }
        backfill("audit risk tags", """
                SELECT id AS owner_id, risk_tags AS values_text
                FROM adoption_audit
                WHERE risk_tags IS NOT NULL AND TRIM(risk_tags) <> ''
                """, "INSERT IGNORE INTO adoption_audit_risk_tag (audit_id, tag_name) VALUES (?, ?)");
    }

    private void backfill(String name, String selectSql, String insertSql) {
        try {
            // 兼容旧库升级：读取旧冗余字段并拆成多条关联记录，INSERT IGNORE 保证可重复执行。
            int inserted = 0;
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(selectSql);
            for (Map<String, Object> row : rows) {
                Object ownerId = row.get("owner_id");
                Object valuesText = row.get("values_text");
                for (String value : splitValues(valuesText)) {
                    inserted += jdbcTemplate.update(insertSql, ownerId, value);
                }
            }
            log.info("Normalization backfill [{}] inserted/ignored {} relation rows.", name, inserted);
        } catch (Exception error) {
            log.debug("Skip normalization backfill [{}]: {}", name, error.getMessage());
        }
    }

    private List<String> splitValues(Object value) {
        if (value == null) {
            return List.of();
        }
        // 支持中英文逗号、分号、顿号和竖线，兼容历史演示数据里的多种标签写法。
        return Arrays.stream(value.toString().split("[,;，；、|]"))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .distinct()
                .toList();
    }

    private boolean tableExists(String tableName) {
        return Boolean.TRUE.equals(jdbcTemplate.execute((Connection connection) -> {
            String schema = connection.getSchema();
            try (ResultSet tables = connection.getMetaData().getTables(null, schema, tableName.toUpperCase(), null)) {
                if (tables.next()) {
                    return true;
                }
            }
            try (ResultSet tables = connection.getMetaData().getTables(null, schema, tableName, null)) {
                return tables.next();
            }
        }));
    }

    private boolean columnExists(String tableName, String columnName) {
        return Boolean.TRUE.equals(jdbcTemplate.execute((Connection connection) -> {
            String schema = connection.getSchema();
            try (ResultSet columns = connection.getMetaData().getColumns(null, schema, tableName.toUpperCase(), columnName.toUpperCase())) {
                if (columns.next()) {
                    return true;
                }
            }
            try (ResultSet columns = connection.getMetaData().getColumns(null, schema, tableName, columnName)) {
                return columns.next();
            }
        }));
    }
}
