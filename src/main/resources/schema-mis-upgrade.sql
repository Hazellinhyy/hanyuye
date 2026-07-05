-- MIS upgrade script for the campus cat adoption system.
-- Run after schema.sql. This script only adds tables/columns/indexes.

CREATE TABLE IF NOT EXISTS adoption_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id VARCHAR(12) NOT NULL,
    cat_id VARCHAR(12),
    user_id VARCHAR(12),
    auditor_id VARCHAR(12) NOT NULL,
    audit_stage VARCHAR(30) NOT NULL,
    audit_result VARCHAR(30) NOT NULL,
    audit_opinion VARCHAR(500) NOT NULL,
    risk_score DECIMAL(5,2),
    risk_tags VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'VALID',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_adoption_audit_application (application_id),
    INDEX idx_adoption_audit_auditor (auditor_id),
    INDEX idx_adoption_audit_stage (audit_stage),
    INDEX idx_adoption_audit_create_time (create_time)
);

CREATE TABLE IF NOT EXISTS adoption_agreement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agreement_no VARCHAR(40) NOT NULL UNIQUE,
    application_id VARCHAR(12) NOT NULL,
    cat_id VARCHAR(12) NOT NULL,
    user_id VARCHAR(12) NOT NULL,
    content TEXT,
    handover_time DATETIME,
    handover_place VARCHAR(200),
    handover_person VARCHAR(80),
    remark VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_agreement_application (application_id),
    INDEX idx_agreement_cat (cat_id),
    INDEX idx_agreement_user (user_id),
    INDEX idx_agreement_status (status)
);

CREATE TABLE IF NOT EXISTS followup_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id VARCHAR(12) NOT NULL,
    agreement_id BIGINT,
    cat_id VARCHAR(12) NOT NULL,
    user_id VARCHAR(12) NOT NULL,
    plan_date DATE NOT NULL,
    round_name VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    handler_id VARCHAR(12),
    completed_time DATETIME,
    abnormal_flag TINYINT(1) NOT NULL DEFAULT 0,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_followup_task_application (application_id),
    INDEX idx_followup_task_cat (cat_id),
    INDEX idx_followup_task_user (user_id),
    INDEX idx_followup_task_handler (handler_id),
    INDEX idx_followup_task_status_plan (status, plan_date)
);

CREATE TABLE IF NOT EXISTS followup_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    application_id VARCHAR(12),
    cat_id VARCHAR(12),
    user_id VARCHAR(12) NOT NULL,
    cat_condition VARCHAR(500) NOT NULL,
    environment_description VARCHAR(500) NOT NULL,
    photo_url VARCHAR(500),
    abnormal_flag TINYINT(1) NOT NULL DEFAULT 0,
    abnormal_description VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_followup_record_task (task_id),
    INDEX idx_followup_record_application (application_id),
    INDEX idx_followup_record_cat (cat_id),
    INDEX idx_followup_record_user (user_id),
    INDEX idx_followup_record_abnormal (abnormal_flag)
);

CREATE TABLE IF NOT EXISTS warning_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warning_type VARCHAR(40) NOT NULL,
    warning_level VARCHAR(20) NOT NULL,
    related_type VARCHAR(40) NOT NULL,
    related_id VARCHAR(40) NOT NULL,
    cat_id VARCHAR(12),
    user_id VARCHAR(12),
    application_id VARCHAR(12),
    task_id BIGINT,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(800),
    handle_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    handler_id VARCHAR(12),
    handle_opinion VARCHAR(500),
    handled_time DATETIME,
    status VARCHAR(30) NOT NULL DEFAULT 'VALID',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_warning_type_level (warning_type, warning_level),
    INDEX idx_warning_status (handle_status),
    INDEX idx_warning_related (related_type, related_id),
    INDEX idx_warning_cat (cat_id),
    INDEX idx_warning_user (user_id),
    INDEX idx_warning_application (application_id),
    INDEX idx_warning_task (task_id),
    INDEX idx_warning_handler (handler_id)
);

CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator_id VARCHAR(12),
    operator_name VARCHAR(80),
    operation_type VARCHAR(60) NOT NULL,
    target_type VARCHAR(40) NOT NULL,
    target_id VARCHAR(40) NOT NULL,
    request_ip VARCHAR(64),
    before_data TEXT,
    after_data TEXT,
    remark VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_operation_operator (operator_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_operation_target (target_type, target_id),
    INDEX idx_operation_create_time (create_time)
);

CREATE TABLE IF NOT EXISTS system_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(12) NOT NULL,
    message_type VARCHAR(40) NOT NULL,
    title VARCHAR(120) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    related_type VARCHAR(40),
    related_id VARCHAR(40),
    read_flag TINYINT(1) NOT NULL DEFAULT 0,
    read_time DATETIME,
    status VARCHAR(30) NOT NULL DEFAULT 'VALID',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_message_user_read (user_id, read_flag),
    INDEX idx_message_type (message_type),
    INDEX idx_message_related (related_type, related_id),
    INDEX idx_message_create_time (create_time)
);

CREATE TABLE IF NOT EXISTS dict_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dict_type VARCHAR(60) NOT NULL,
    item_code VARCHAR(60) NOT NULL,
    item_label VARCHAR(120) NOT NULL,
    item_value VARCHAR(120),
    sort_order INT NOT NULL DEFAULT 0,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    remark VARCHAR(300),
    status VARCHAR(30) NOT NULL DEFAULT 'VALID',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dict_type_code (dict_type, item_code),
    INDEX idx_dict_type_enabled (dict_type, enabled)
);

ALTER TABLE t_application ADD COLUMN initial_auditor_id VARCHAR(12);
ALTER TABLE t_application ADD COLUMN initial_audit_note VARCHAR(500);
ALTER TABLE t_application ADD COLUMN initial_audited_at DATETIME;
ALTER TABLE t_application ADD COLUMN final_auditor_id VARCHAR(12);
ALTER TABLE t_application ADD COLUMN final_audit_note VARCHAR(500);
ALTER TABLE t_application ADD COLUMN final_audited_at DATETIME;
ALTER TABLE t_application ADD COLUMN risk_score DECIMAL(5,2);
ALTER TABLE t_application ADD COLUMN risk_tags VARCHAR(500);
ALTER TABLE t_application ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_application ADD COLUMN create_by VARCHAR(12);
ALTER TABLE t_application ADD COLUMN update_by VARCHAR(12);
ALTER TABLE t_application ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE t_cat ADD COLUMN adoption_conditions VARCHAR(500);
ALTER TABLE t_cat ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_cat ADD COLUMN create_by VARCHAR(12);
ALTER TABLE t_cat ADD COLUMN update_by VARCHAR(12);

ALTER TABLE t_medical_record ADD COLUMN record_type VARCHAR(30) NOT NULL DEFAULT 'CHECKUP';
ALTER TABLE t_medical_record ADD COLUMN abnormal_flag TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_medical_record ADD COLUMN attachment_url VARCHAR(500);
ALTER TABLE t_medical_record ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_medical_record ADD COLUMN create_by VARCHAR(12);
ALTER TABLE t_medical_record ADD COLUMN update_by VARCHAR(12);
ALTER TABLE t_medical_record ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE t_rescue_report ADD COLUMN verifier_id VARCHAR(12);
ALTER TABLE t_rescue_report ADD COLUMN verify_note VARCHAR(500);
ALTER TABLE t_rescue_report ADD COLUMN verified_at DATETIME;
ALTER TABLE t_rescue_report ADD COLUMN linked_cat_id VARCHAR(12);
ALTER TABLE t_rescue_report ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_rescue_report ADD COLUMN create_by VARCHAR(12);
ALTER TABLE t_rescue_report ADD COLUMN update_by VARCHAR(12);
ALTER TABLE t_rescue_report ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

CREATE INDEX idx_t_application_user_cat_status ON t_application (user_id, cat_id, apply_status);
CREATE INDEX idx_t_cat_status_deleted ON t_cat (cat_status, deleted);
CREATE INDEX idx_t_medical_cat_type ON t_medical_record (cat_id, record_type);
CREATE INDEX idx_t_report_status ON t_rescue_report (report_status);

-- Stage 2: clue verification and one-click cat profile creation.
ALTER TABLE t_rescue_report ADD COLUMN reporter_id VARCHAR(12);
ALTER TABLE t_rescue_report ADD COLUMN found_area VARCHAR(100);
ALTER TABLE t_rescue_report ADD COLUMN found_time DATETIME;
ALTER TABLE t_rescue_report ADD COLUMN urgency_level VARCHAR(20) NOT NULL DEFAULT 'NORMAL';
ALTER TABLE t_rescue_report ADD COLUMN verify_user_id VARCHAR(12);
ALTER TABLE t_rescue_report ADD COLUMN verify_result VARCHAR(30);
ALTER TABLE t_rescue_report ADD COLUMN verify_comment VARCHAR(500);
ALTER TABLE t_rescue_report ADD COLUMN verify_time DATETIME;
ALTER TABLE t_rescue_report ADD COLUMN created_cat_id VARCHAR(12);
ALTER TABLE t_rescue_report ADD COLUMN update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

CREATE INDEX idx_report_reporter_status ON t_rescue_report (reporter_id, report_status);
CREATE INDEX idx_report_urgency_status ON t_rescue_report (urgency_level, report_status);
CREATE INDEX idx_report_verify_user ON t_rescue_report (verify_user_id);
CREATE INDEX idx_report_created_cat ON t_rescue_report (created_cat_id);

-- Stage 3: cat profile completion, medical maintenance and adoption publishing.
ALTER TABLE t_cat ADD COLUMN found_area VARCHAR(100);
ALTER TABLE t_cat ADD COLUMN source_clue_id VARCHAR(12);

ALTER TABLE t_medical_record ADD COLUMN hospital_user_id VARCHAR(12);
ALTER TABLE t_medical_record ADD COLUMN cost DECIMAL(10,2);

CREATE INDEX idx_t_cat_health_status ON t_cat (health_level, cat_status);
CREATE INDEX idx_t_cat_source_clue ON t_cat (source_clue_id);
CREATE INDEX idx_medical_record_type_date ON t_medical_record (record_type, check_date);
CREATE INDEX idx_medical_hospital_user ON t_medical_record (hospital_user_id);

-- Stage 4: adoption application scoring and two-step audit.
ALTER TABLE t_application ADD COLUMN commitment_text VARCHAR(1000);
ALTER TABLE t_application ADD COLUMN extra_reason VARCHAR(1000);
ALTER TABLE t_application ADD COLUMN score INT;
ALTER TABLE t_application ADD COLUMN risk_level VARCHAR(20);
ALTER TABLE t_application ADD COLUMN score_reasons VARCHAR(1000);
ALTER TABLE t_application ADD COLUMN cancel_reason VARCHAR(500);

ALTER TABLE adoption_audit ADD COLUMN before_status VARCHAR(30);
ALTER TABLE adoption_audit ADD COLUMN after_status VARCHAR(30);
ALTER TABLE adoption_audit ADD COLUMN audit_time DATETIME;

CREATE INDEX idx_application_status_risk ON t_application (apply_status, risk_level);
CREATE INDEX idx_application_cat_user_status ON t_application (cat_id, user_id, apply_status);
CREATE INDEX idx_adoption_audit_app_stage ON adoption_audit (application_id, audit_stage);
CREATE INDEX idx_adoption_audit_auditor_time ON adoption_audit (auditor_id, audit_time);

-- Stage 5: agreement handover and automatic follow-up tasks.
ALTER TABLE adoption_agreement ADD COLUMN adopter_id VARCHAR(12);
ALTER TABLE adoption_agreement ADD COLUMN agreement_content TEXT;
ALTER TABLE adoption_agreement ADD COLUMN generated_time DATETIME;
ALTER TABLE adoption_agreement ADD COLUMN handover_location VARCHAR(200);
ALTER TABLE adoption_agreement ADD COLUMN handover_user_id VARCHAR(12);
ALTER TABLE adoption_agreement ADD COLUMN adopter_confirmed TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE adoption_agreement ADD COLUMN volunteer_confirmed TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE followup_task ADD COLUMN adopter_id VARCHAR(12);
ALTER TABLE followup_task ADD COLUMN actual_date DATE;
ALTER TABLE followup_task ADD COLUMN task_type VARCHAR(30);

CREATE INDEX idx_agreement_adopter ON adoption_agreement (adopter_id);
CREATE INDEX idx_agreement_generated_time ON adoption_agreement (generated_time);
CREATE INDEX idx_followup_task_agreement ON followup_task (agreement_id);
CREATE INDEX idx_followup_task_adopter ON followup_task (adopter_id);
CREATE INDEX idx_followup_task_type ON followup_task (task_type);
CREATE INDEX idx_followup_task_plan_date ON followup_task (plan_date);

CREATE UNIQUE INDEX uk_agreement_application_active ON adoption_agreement (application_id, deleted);
CREATE UNIQUE INDEX uk_followup_application_task_active ON followup_task (application_id, task_type, deleted);

-- Stage 6: follow-up feedback, overdue refresh and warning handling.
ALTER TABLE followup_record ADD COLUMN agreement_id BIGINT;
ALTER TABLE followup_record ADD COLUMN adopter_id VARCHAR(12);
ALTER TABLE followup_record ADD COLUMN content VARCHAR(1000);
ALTER TABLE followup_record ADD COLUMN environment_desc VARCHAR(500);
ALTER TABLE followup_record ADD COLUMN abnormal_desc VARCHAR(500);
ALTER TABLE followup_record ADD COLUMN volunteer_comment VARCHAR(500);
ALTER TABLE followup_record ADD COLUMN submit_time DATETIME;

ALTER TABLE warning_record ADD COLUMN biz_type VARCHAR(40);
ALTER TABLE warning_record ADD COLUMN biz_id VARCHAR(40);
ALTER TABLE warning_record ADD COLUMN content VARCHAR(1000);
ALTER TABLE warning_record ADD COLUMN handle_comment VARCHAR(500);
ALTER TABLE warning_record ADD COLUMN handle_time DATETIME;

CREATE INDEX idx_followup_record_adopter ON followup_record (adopter_id);
CREATE INDEX idx_followup_record_submit_time ON followup_record (submit_time);
CREATE INDEX idx_warning_biz ON warning_record (biz_type, biz_id);
CREATE INDEX idx_warning_user_status ON warning_record (user_id, handle_status);
CREATE INDEX idx_warning_task_type_status ON warning_record (task_id, warning_type, handle_status);

CREATE UNIQUE INDEX uk_followup_record_task_active ON followup_record (task_id, deleted);

-- Stage 7: system management, messages, notices, dicts and dashboard support.
ALTER TABLE system_message ADD COLUMN receiver_id VARCHAR(12);
ALTER TABLE system_message ADD COLUMN biz_type VARCHAR(40);
ALTER TABLE system_message ADD COLUMN biz_id VARCHAR(40);
ALTER TABLE system_message ADD COLUMN read_status VARCHAR(20) NOT NULL DEFAULT 'UNREAD';

ALTER TABLE t_notice ADD COLUMN notice_type VARCHAR(30) NOT NULL DEFAULT 'SYSTEM';
ALTER TABLE t_notice ADD COLUMN publish_status VARCHAR(30) NOT NULL DEFAULT 'PUBLISHED';
ALTER TABLE t_notice ADD COLUMN publisher_id VARCHAR(12);
ALTER TABLE t_notice ADD COLUMN publish_time DATETIME;
ALTER TABLE t_notice ADD COLUMN sort_order INT NOT NULL DEFAULT 0;
ALTER TABLE t_notice ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_notice ADD COLUMN create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE t_notice ADD COLUMN update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE dict_item ADD COLUMN dict_label VARCHAR(120);
ALTER TABLE dict_item ADD COLUMN dict_value VARCHAR(120);

UPDATE system_message
SET receiver_id = user_id,
    biz_type = related_type,
    biz_id = related_id,
    read_status = CASE WHEN read_flag = 1 THEN 'READ' ELSE 'UNREAD' END
WHERE receiver_id IS NULL;

UPDATE t_notice
SET publish_status = CASE WHEN enabled = 1 THEN 'PUBLISHED' ELSE 'OFFLINE' END,
    publish_time = published_at,
    create_time = COALESCE(published_at, create_time),
    update_time = COALESCE(published_at, update_time)
WHERE notice_id IS NOT NULL;

UPDATE dict_item
SET dict_label = item_label,
    dict_value = COALESCE(item_value, item_code)
WHERE dict_label IS NULL OR dict_value IS NULL;

CREATE INDEX idx_message_receiver_read ON system_message (receiver_id, read_status);
CREATE INDEX idx_message_biz ON system_message (biz_type, biz_id);
CREATE INDEX idx_notice_publish_type ON t_notice (publish_status, notice_type);
CREATE INDEX idx_notice_sort_publish ON t_notice (sort_order, publish_time);
CREATE INDEX idx_dict_type_value ON dict_item (dict_type, dict_value);
