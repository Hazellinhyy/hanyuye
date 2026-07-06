package com.hfut.cat_adoption_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FollowupSchemaMigration implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(FollowupSchemaMigration.class);

    private final JdbcTemplate jdbcTemplate;

    public FollowupSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute("DROP INDEX uk_followup_record_task_active ON followup_record");
            log.info("Dropped legacy unique index uk_followup_record_task_active from followup_record.");
        } catch (Exception ignored) {
            log.debug("Legacy follow-up unique index is absent or already migrated.");
        }
    }
}
