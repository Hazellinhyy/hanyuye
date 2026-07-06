-- Strict 3NF schema for the campus cat adoption system.
-- Use this for a clean database when the course requires a normalized design.
-- It removes multi-value columns, duplicated compatibility columns and
-- transitive dependencies kept in the legacy demo schema.

CREATE TABLE role_type (
    role_code VARCHAR(20) PRIMARY KEY,
    role_name VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE dict_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dict_type VARCHAR(60) NOT NULL,
    item_code VARCHAR(60) NOT NULL,
    item_label VARCHAR(120) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    remark VARCHAR(300),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dict_type_code (dict_type, item_code)
);

CREATE TABLE t_user (
    user_id VARCHAR(12) PRIMARY KEY,
    user_name VARCHAR(30) NOT NULL,
    school_no VARCHAR(20) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    id_card VARCHAR(18) NOT NULL UNIQUE,
    college VARCHAR(80) NOT NULL,
    pet_experience VARCHAR(300),
    password_hash VARCHAR(120) NOT NULL DEFAULT '',
    token_version INT NOT NULL DEFAULT 0,
    role_code VARCHAR(20) NOT NULL,
    status TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_user_role FOREIGN KEY (role_code) REFERENCES role_type(role_code)
);

CREATE TABLE t_cat (
    cat_id VARCHAR(12) PRIMARY KEY,
    cat_name VARCHAR(30),
    found_place VARCHAR(100) NOT NULL,
    found_area VARCHAR(100),
    found_date DATE,
    gender CHAR(1),
    color VARCHAR(30),
    age_estimate VARCHAR(30),
    personality VARCHAR(200),
    cat_status VARCHAR(20) NOT NULL,
    cover_url VARCHAR(255),
    description VARCHAR(500),
    adoption_conditions VARCHAR(500),
    source_clue_id VARCHAR(12),
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_cat_create_by FOREIGN KEY (create_by) REFERENCES t_user(user_id),
    CONSTRAINT fk_cat_update_by FOREIGN KEY (update_by) REFERENCES t_user(user_id)
);

CREATE TABLE cat_tag (
    cat_id VARCHAR(12) NOT NULL,
    tag_name VARCHAR(40) NOT NULL,
    PRIMARY KEY (cat_id, tag_name),
    CONSTRAINT fk_cat_tag_cat FOREIGN KEY (cat_id) REFERENCES t_cat(cat_id) ON DELETE CASCADE
);

CREATE TABLE t_cat_photo (
    photo_id VARCHAR(12) PRIMARY KEY,
    cat_id VARCHAR(12) NOT NULL,
    photo_url VARCHAR(255) NOT NULL,
    angle_code VARCHAR(30) NOT NULL,
    photo_scene VARCHAR(80),
    is_cover TINYINT(1) NOT NULL DEFAULT 0,
    recognition_weight DECIMAL(4,2) NOT NULL DEFAULT 1.00,
    feature_vector TEXT,
    feature_note VARCHAR(300),
    uploaded_at DATETIME NOT NULL,
    CONSTRAINT fk_cat_photo_cat FOREIGN KEY (cat_id) REFERENCES t_cat(cat_id) ON DELETE CASCADE
);

CREATE TABLE t_rescue_report (
    report_id VARCHAR(12) PRIMARY KEY,
    reporter_id VARCHAR(12),
    reporter_name VARCHAR(30) NOT NULL,
    reporter_phone VARCHAR(20) NOT NULL,
    found_place VARCHAR(100) NOT NULL,
    found_area VARCHAR(100),
    found_time DATETIME,
    color VARCHAR(30),
    gender CHAR(1),
    health_description VARCHAR(300) NOT NULL,
    urgency_level VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    photo_url VARCHAR(255) NOT NULL,
    report_status VARCHAR(20) NOT NULL,
    verify_user_id VARCHAR(12),
    verify_result VARCHAR(30),
    verify_comment VARCHAR(500),
    verify_time DATETIME,
    created_cat_id VARCHAR(12),
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    reported_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_report_verify_user FOREIGN KEY (verify_user_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_report_created_cat FOREIGN KEY (created_cat_id) REFERENCES t_cat(cat_id),
    CONSTRAINT fk_report_create_by FOREIGN KEY (create_by) REFERENCES t_user(user_id),
    CONSTRAINT fk_report_update_by FOREIGN KEY (update_by) REFERENCES t_user(user_id)
);

ALTER TABLE t_cat
    ADD CONSTRAINT fk_cat_source_clue FOREIGN KEY (source_clue_id) REFERENCES t_rescue_report(report_id);

CREATE TABLE t_medical_record (
    medical_id VARCHAR(12) PRIMARY KEY,
    cat_id VARCHAR(12) NOT NULL,
    hospital_user_id VARCHAR(12),
    hospital VARCHAR(80) NOT NULL,
    record_type VARCHAR(30) NOT NULL DEFAULT 'CHECKUP',
    check_date DATE NOT NULL,
    health_level VARCHAR(10) NOT NULL,
    vaccinated TINYINT(1) NOT NULL DEFAULT 0,
    sterilized TINYINT(1) NOT NULL DEFAULT 0,
    treatment VARCHAR(300),
    doctor_note VARCHAR(300),
    cost DECIMAL(10,2),
    attachment_url VARCHAR(500),
    abnormal_flag TINYINT(1) NOT NULL DEFAULT 0,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_medical_cat FOREIGN KEY (cat_id) REFERENCES t_cat(cat_id),
    CONSTRAINT fk_medical_hospital_user FOREIGN KEY (hospital_user_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_medical_create_by FOREIGN KEY (create_by) REFERENCES t_user(user_id),
    CONSTRAINT fk_medical_update_by FOREIGN KEY (update_by) REFERENCES t_user(user_id)
);

CREATE TABLE t_application (
    application_id VARCHAR(12) PRIMARY KEY,
    user_id VARCHAR(12) NOT NULL,
    cat_id VARCHAR(12) NOT NULL,
    housing_info VARCHAR(200) NOT NULL,
    family_attitude VARCHAR(200) NOT NULL,
    pet_experience VARCHAR(300) NOT NULL,
    economic_ability VARCHAR(200) NOT NULL,
    promise_accepted TINYINT(1) NOT NULL DEFAULT 1,
    commitment_text VARCHAR(1000),
    extra_reason VARCHAR(1000),
    score INT,
    risk_level VARCHAR(20),
    apply_status VARCHAR(20) NOT NULL,
    cancel_reason VARCHAR(500),
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    applied_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_application_user FOREIGN KEY (user_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_application_cat FOREIGN KEY (cat_id) REFERENCES t_cat(cat_id),
    CONSTRAINT fk_application_create_by FOREIGN KEY (create_by) REFERENCES t_user(user_id),
    CONSTRAINT fk_application_update_by FOREIGN KEY (update_by) REFERENCES t_user(user_id)
);

CREATE TABLE application_score_reason (
    application_id VARCHAR(12) NOT NULL,
    reason_order INT NOT NULL,
    reason_text VARCHAR(300) NOT NULL,
    score_delta INT,
    PRIMARY KEY (application_id, reason_order),
    CONSTRAINT fk_score_reason_application FOREIGN KEY (application_id) REFERENCES t_application(application_id) ON DELETE CASCADE
);

CREATE TABLE adoption_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id VARCHAR(12) NOT NULL,
    auditor_id VARCHAR(12) NOT NULL,
    audit_stage VARCHAR(30) NOT NULL,
    audit_result VARCHAR(30) NOT NULL,
    audit_opinion VARCHAR(500) NOT NULL,
    risk_score DECIMAL(5,2),
    before_status VARCHAR(30),
    after_status VARCHAR(30),
    status VARCHAR(30) NOT NULL DEFAULT 'VALID',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    audit_time DATETIME,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_application FOREIGN KEY (application_id) REFERENCES t_application(application_id),
    CONSTRAINT fk_audit_auditor FOREIGN KEY (auditor_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_audit_create_by FOREIGN KEY (create_by) REFERENCES t_user(user_id),
    CONSTRAINT fk_audit_update_by FOREIGN KEY (update_by) REFERENCES t_user(user_id)
);

CREATE TABLE adoption_agreement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agreement_no VARCHAR(40) NOT NULL UNIQUE,
    application_id VARCHAR(12) NOT NULL,
    agreement_content TEXT,
    generated_time DATETIME,
    handover_time DATETIME,
    handover_location VARCHAR(200),
    handover_user_id VARCHAR(12),
    adopter_confirmed TINYINT(1) NOT NULL DEFAULT 0,
    volunteer_confirmed TINYINT(1) NOT NULL DEFAULT 0,
    remark VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_agreement_application FOREIGN KEY (application_id) REFERENCES t_application(application_id),
    CONSTRAINT fk_agreement_handover_user FOREIGN KEY (handover_user_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_agreement_create_by FOREIGN KEY (create_by) REFERENCES t_user(user_id),
    CONSTRAINT fk_agreement_update_by FOREIGN KEY (update_by) REFERENCES t_user(user_id)
);

CREATE TABLE followup_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id VARCHAR(12) NOT NULL,
    agreement_id BIGINT,
    plan_date DATE NOT NULL,
    actual_date DATE,
    task_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    handler_id VARCHAR(12),
    completed_time DATETIME,
    abnormal_flag TINYINT(1) NOT NULL DEFAULT 0,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_followup_task_application FOREIGN KEY (application_id) REFERENCES t_application(application_id),
    CONSTRAINT fk_followup_task_agreement FOREIGN KEY (agreement_id) REFERENCES adoption_agreement(id),
    CONSTRAINT fk_followup_task_handler FOREIGN KEY (handler_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_followup_task_create_by FOREIGN KEY (create_by) REFERENCES t_user(user_id),
    CONSTRAINT fk_followup_task_update_by FOREIGN KEY (update_by) REFERENCES t_user(user_id),
    UNIQUE KEY uk_followup_task_active (application_id, task_type, deleted)
);

CREATE TABLE followup_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    content VARCHAR(1000),
    cat_condition VARCHAR(500) NOT NULL,
    environment_description VARCHAR(500) NOT NULL,
    photo_url VARCHAR(500),
    abnormal_flag TINYINT(1) NOT NULL DEFAULT 0,
    abnormal_description VARCHAR(500),
    volunteer_comment VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    submit_time DATETIME,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_followup_record_task FOREIGN KEY (task_id) REFERENCES followup_task(id),
    CONSTRAINT fk_followup_record_create_by FOREIGN KEY (create_by) REFERENCES t_user(user_id),
    CONSTRAINT fk_followup_record_update_by FOREIGN KEY (update_by) REFERENCES t_user(user_id),
    INDEX idx_followup_record_task_active (task_id, deleted)
);

CREATE TABLE warning_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warning_type VARCHAR(40) NOT NULL,
    warning_level VARCHAR(20) NOT NULL,
    cat_id VARCHAR(12),
    user_id VARCHAR(12),
    application_id VARCHAR(12),
    task_id BIGINT,
    title VARCHAR(120) NOT NULL,
    content VARCHAR(1000),
    handle_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    handler_id VARCHAR(12),
    handle_comment VARCHAR(500),
    handle_time DATETIME,
    status VARCHAR(30) NOT NULL DEFAULT 'VALID',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_warning_cat FOREIGN KEY (cat_id) REFERENCES t_cat(cat_id),
    CONSTRAINT fk_warning_user FOREIGN KEY (user_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_warning_application FOREIGN KEY (application_id) REFERENCES t_application(application_id),
    CONSTRAINT fk_warning_task FOREIGN KEY (task_id) REFERENCES followup_task(id),
    CONSTRAINT fk_warning_handler FOREIGN KEY (handler_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_warning_create_by FOREIGN KEY (create_by) REFERENCES t_user(user_id),
    CONSTRAINT fk_warning_update_by FOREIGN KEY (update_by) REFERENCES t_user(user_id)
);

CREATE TABLE t_notice (
    notice_id VARCHAR(12) PRIMARY KEY,
    title VARCHAR(80) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    notice_type VARCHAR(30) NOT NULL DEFAULT 'SYSTEM',
    publish_status VARCHAR(30) NOT NULL DEFAULT 'PUBLISHED',
    publisher_id VARCHAR(12) NOT NULL,
    publish_time DATETIME,
    pinned TINYINT(1) NOT NULL DEFAULT 0,
    sort_order INT NOT NULL DEFAULT 0,
    image_url VARCHAR(255),
    target_roles VARCHAR(120) NOT NULL DEFAULT 'STUDENT,VOLUNTEER,HOSPITAL,ADMIN',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_notice_publisher FOREIGN KEY (publisher_id) REFERENCES t_user(user_id)
);

CREATE TABLE system_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiver_id VARCHAR(12) NOT NULL,
    message_type VARCHAR(40) NOT NULL,
    title VARCHAR(120) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    biz_type VARCHAR(40),
    biz_id VARCHAR(40),
    read_status VARCHAR(20) NOT NULL DEFAULT 'UNREAD',
    read_time DATETIME,
    status VARCHAR(30) NOT NULL DEFAULT 'VALID',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_message_receiver FOREIGN KEY (receiver_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_message_create_by FOREIGN KEY (create_by) REFERENCES t_user(user_id),
    CONSTRAINT fk_message_update_by FOREIGN KEY (update_by) REFERENCES t_user(user_id)
);

CREATE TABLE operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator_id VARCHAR(12),
    operation_type VARCHAR(60) NOT NULL,
    target_type VARCHAR(40) NOT NULL,
    target_id VARCHAR(40) NOT NULL,
    request_ip VARCHAR(64),
    before_data TEXT,
    after_data TEXT,
    remark VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_operation_operator FOREIGN KEY (operator_id) REFERENCES t_user(user_id)
);

CREATE TABLE t_article (
    article_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    type_name VARCHAR(40),
    cover_url VARCHAR(255),
    summary VARCHAR(300),
    content TEXT NOT NULL,
    source VARCHAR(80),
    source_url VARCHAR(255),
    hits INT NOT NULL DEFAULT 0,
    praise_count INT NOT NULL DEFAULT 0,
    published TINYINT(1) NOT NULL DEFAULT 1,
    pinned TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE article_tag (
    article_id INT NOT NULL,
    tag_name VARCHAR(40) NOT NULL,
    PRIMARY KEY (article_id, tag_name),
    CONSTRAINT fk_article_tag_article FOREIGN KEY (article_id) REFERENCES t_article(article_id) ON DELETE CASCADE
);

CREATE TABLE t_forum_post (
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(12) NOT NULL,
    type_name VARCHAR(40),
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    cover_url VARCHAR(255),
    hits INT NOT NULL DEFAULT 0,
    praise_count INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_forum_post_user FOREIGN KEY (user_id) REFERENCES t_user(user_id)
);

CREATE TABLE t_comment (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(12) NOT NULL,
    source_type VARCHAR(20) NOT NULL,
    source_id INT NOT NULL,
    reply_to_id INT,
    content VARCHAR(600) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_comment_reply FOREIGN KEY (reply_to_id) REFERENCES t_comment(comment_id)
);

CREATE TABLE t_generic_collect (
    user_id VARCHAR(12) NOT NULL,
    source_type VARCHAR(20) NOT NULL,
    source_id INT NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (user_id, source_type, source_id),
    CONSTRAINT fk_collect_user FOREIGN KEY (user_id) REFERENCES t_user(user_id)
);

CREATE TABLE t_product (
    product_id VARCHAR(12) PRIMARY KEY,
    product_name VARCHAR(50) NOT NULL,
    category VARCHAR(30) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    pay_url VARCHAR(255),
    stock INT NOT NULL DEFAULT 0,
    status TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL
);

CREATE TABLE t_product_order (
    order_id VARCHAR(12) PRIMARY KEY,
    user_id VARCHAR(12) NOT NULL,
    product_id VARCHAR(12) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    pay_url VARCHAR(255),
    order_status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_order_product FOREIGN KEY (product_id) REFERENCES t_product(product_id)
);

CREATE TABLE t_donation_channel (
    channel_id VARCHAR(12) PRIMARY KEY,
    channel_name VARCHAR(50) NOT NULL,
    qr_url VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    updated_at DATETIME NOT NULL
);

CREATE TABLE t_donation_record (
    donation_id VARCHAR(12) PRIMARY KEY,
    user_id VARCHAR(12) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    channel_id VARCHAR(12),
    donor_message VARCHAR(300),
    donate_status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_donation_user FOREIGN KEY (user_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_donation_channel FOREIGN KEY (channel_id) REFERENCES t_donation_channel(channel_id)
);

CREATE TABLE t_slide (
    slide_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(80) NOT NULL,
    content VARCHAR(300),
    image_url VARCHAR(255) NOT NULL,
    link_url VARCHAR(255),
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL
);

CREATE TABLE t_pet_species (
    species_id INT AUTO_INCREMENT PRIMARY KEY,
    species_name VARCHAR(40) NOT NULL UNIQUE,
    description VARCHAR(300),
    created_at DATETIME NOT NULL
);
