CREATE TABLE IF NOT EXISTS t_user (
    user_id VARCHAR(12) PRIMARY KEY,
    user_name VARCHAR(30) NOT NULL,
    school_no VARCHAR(20) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    id_card VARCHAR(18) NOT NULL,
    college VARCHAR(80) NOT NULL,
    pet_experience VARCHAR(300),
    password_hash VARCHAR(120) NOT NULL DEFAULT '',
    token_version INT NOT NULL DEFAULT 0,
    role_code VARCHAR(20) NOT NULL,
    status TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL
);

ALTER TABLE t_user ADD COLUMN password_hash VARCHAR(120) NOT NULL DEFAULT '';
ALTER TABLE t_user ADD COLUMN token_version INT NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS t_cat (
    cat_id VARCHAR(12) PRIMARY KEY,
    cat_name VARCHAR(30),
    found_place VARCHAR(100) NOT NULL,
    found_date DATE,
    gender CHAR(1),
    color VARCHAR(30),
    age_estimate VARCHAR(30),
    personality VARCHAR(200),
    health_level VARCHAR(10) NOT NULL,
    sterilized TINYINT(1) NOT NULL DEFAULT 0,
    vaccinated TINYINT(1) NOT NULL DEFAULT 0,
    cat_status VARCHAR(20) NOT NULL,
    cover_url VARCHAR(255),
    tags VARCHAR(255),
    description VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS t_cat_photo (
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

ALTER TABLE t_cat_photo ADD COLUMN feature_vector TEXT;


CREATE TABLE IF NOT EXISTS t_favorite (
    user_id VARCHAR(12) NOT NULL,
    cat_id VARCHAR(12) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (user_id, cat_id),
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_favorite_cat FOREIGN KEY (cat_id) REFERENCES t_cat(cat_id)
);

CREATE TABLE IF NOT EXISTS t_rescue_report (
    report_id VARCHAR(12) PRIMARY KEY,
    reporter_name VARCHAR(30) NOT NULL,
    reporter_phone VARCHAR(20) NOT NULL,
    found_place VARCHAR(100) NOT NULL,
    color VARCHAR(30),
    gender CHAR(1),
    health_description VARCHAR(300) NOT NULL,
    urgent TINYINT(1) NOT NULL DEFAULT 0,
    photo_url VARCHAR(255) NOT NULL,
    report_status VARCHAR(20) NOT NULL,
    reported_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS t_medical_record (
    medical_id VARCHAR(12) PRIMARY KEY,
    cat_id VARCHAR(12) NOT NULL,
    check_date DATE NOT NULL,
    hospital VARCHAR(80) NOT NULL,
    health_level VARCHAR(10) NOT NULL,
    vaccinated TINYINT(1) NOT NULL DEFAULT 0,
    sterilized TINYINT(1) NOT NULL DEFAULT 0,
    treatment VARCHAR(300),
    doctor_note VARCHAR(300),
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_medical_cat FOREIGN KEY (cat_id) REFERENCES t_cat(cat_id)
);

CREATE TABLE IF NOT EXISTS t_application (
    application_id VARCHAR(12) PRIMARY KEY,
    user_id VARCHAR(12) NOT NULL,
    cat_id VARCHAR(12) NOT NULL,
    housing_info VARCHAR(200) NOT NULL,
    family_attitude VARCHAR(200) NOT NULL,
    pet_experience VARCHAR(300) NOT NULL,
    economic_ability VARCHAR(200) NOT NULL,
    promise_accepted TINYINT(1) NOT NULL DEFAULT 1,
    apply_status VARCHAR(20) NOT NULL,
    review_note VARCHAR(200),
    interview_note VARCHAR(300),
    agreement_no VARCHAR(30),
    applied_at DATETIME NOT NULL,
    reviewed_at DATETIME,
    handed_over_at DATETIME,
    CONSTRAINT fk_application_user FOREIGN KEY (user_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_application_cat FOREIGN KEY (cat_id) REFERENCES t_cat(cat_id)
);

CREATE TABLE IF NOT EXISTS t_followup (
    followup_id VARCHAR(12) PRIMARY KEY,
    application_id VARCHAR(12) NOT NULL,
    followup_time DATETIME NOT NULL,
    method VARCHAR(20) NOT NULL,
    cat_condition VARCHAR(200) NOT NULL,
    environment_description VARCHAR(300) NOT NULL,
    result_level VARCHAR(20) NOT NULL,
    photo_url VARCHAR(255),
    suggestion VARCHAR(200),
    operator_name VARCHAR(30) NOT NULL,
    CONSTRAINT fk_followup_application FOREIGN KEY (application_id) REFERENCES t_application(application_id)
);

CREATE TABLE IF NOT EXISTS t_notice (
    notice_id VARCHAR(12) PRIMARY KEY,
    title VARCHAR(80) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    publisher VARCHAR(30) NOT NULL,
    pinned TINYINT(1) NOT NULL DEFAULT 0,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    published_at DATETIME NOT NULL
);

ALTER TABLE t_notice ADD COLUMN pinned TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_notice ADD COLUMN enabled TINYINT(1) NOT NULL DEFAULT 1;

CREATE TABLE IF NOT EXISTS t_audit_log (
    log_id VARCHAR(12) PRIMARY KEY,
    operator VARCHAR(30) NOT NULL,
    action VARCHAR(40) NOT NULL,
    target_type VARCHAR(30) NOT NULL,
    target_id VARCHAR(30) NOT NULL,
    detail VARCHAR(300),
    created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS t_product (
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

ALTER TABLE t_product ADD COLUMN stock INT NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS t_product_order (
    order_id VARCHAR(12) PRIMARY KEY,
    user_id VARCHAR(12) NOT NULL,
    product_id VARCHAR(12) NOT NULL,
    quantity INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    pay_url VARCHAR(255),
    order_status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES t_user(user_id),
    CONSTRAINT fk_order_product FOREIGN KEY (product_id) REFERENCES t_product(product_id)
);

CREATE TABLE IF NOT EXISTS t_donation_record (
    donation_id VARCHAR(12) PRIMARY KEY,
    user_id VARCHAR(12) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    channel_id VARCHAR(12),
    donor_message VARCHAR(300),
    donate_status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_donation_user FOREIGN KEY (user_id) REFERENCES t_user(user_id)
);

CREATE TABLE IF NOT EXISTS t_donation_channel (
    channel_id VARCHAR(12) PRIMARY KEY,
    channel_name VARCHAR(50) NOT NULL,
    qr_url VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS t_article (
    article_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    type_name VARCHAR(40),
    cover_url VARCHAR(255),
    summary VARCHAR(300),
    content TEXT NOT NULL,
    source VARCHAR(80),
    source_url VARCHAR(255),
    tags VARCHAR(255),
    hits INT NOT NULL DEFAULT 0,
    praise_count INT NOT NULL DEFAULT 0,
    published TINYINT(1) NOT NULL DEFAULT 1,
    pinned TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS t_forum_post (
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

CREATE TABLE IF NOT EXISTS t_comment (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(12) NOT NULL,
    source_type VARCHAR(20) NOT NULL,
    source_id INT NOT NULL,
    reply_to_id INT,
    content VARCHAR(600) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES t_user(user_id)
);

CREATE TABLE IF NOT EXISTS t_generic_collect (
    user_id VARCHAR(12) NOT NULL,
    source_type VARCHAR(20) NOT NULL,
    source_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    image_url VARCHAR(255),
    created_at DATETIME NOT NULL,
    PRIMARY KEY (user_id, source_type, source_id),
    CONSTRAINT fk_collect_user FOREIGN KEY (user_id) REFERENCES t_user(user_id)
);

CREATE TABLE IF NOT EXISTS t_slide (
    slide_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(80) NOT NULL,
    content VARCHAR(300),
    image_url VARCHAR(255) NOT NULL,
    link_url VARCHAR(255),
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS t_pet_species (
    species_id INT AUTO_INCREMENT PRIMARY KEY,
    species_name VARCHAR(40) NOT NULL,
    description VARCHAR(300),
    created_at DATETIME NOT NULL
);

ALTER TABLE t_cat ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_medical_record ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_application ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_product_order ADD COLUMN IF NOT EXISTS unit_price DECIMAL(10,2) NOT NULL DEFAULT 0;
ALTER TABLE t_product_order ADD COLUMN IF NOT EXISTS total_amount DECIMAL(10,2) NOT NULL DEFAULT 0;
ALTER TABLE t_notice ADD COLUMN IF NOT EXISTS publisher_id VARCHAR(12);
ALTER TABLE t_notice ADD COLUMN IF NOT EXISTS publish_status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED';
ALTER TABLE t_notice ADD COLUMN IF NOT EXISTS publish_time DATETIME;
ALTER TABLE t_notice ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_article ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_forum_post ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE t_generic_collect ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS cat_tag (
    cat_id VARCHAR(12) NOT NULL,
    tag_name VARCHAR(40) NOT NULL,
    PRIMARY KEY (cat_id, tag_name)
);

CREATE TABLE IF NOT EXISTS adoption_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id VARCHAR(12) NOT NULL,
    audit_type VARCHAR(30) NOT NULL,
    audit_result VARCHAR(30) NOT NULL,
    audit_opinion VARCHAR(500),
    auditor_id VARCHAR(12),
    audit_time DATETIME NOT NULL,
    deleted TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS adoption_agreement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agreement_no VARCHAR(40) NOT NULL,
    application_id VARCHAR(12) NOT NULL,
    agreement_content TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'GENERATED',
    generated_time DATETIME,
    signed_time DATETIME,
    handover_time DATETIME,
    handover_location VARCHAR(200),
    remark VARCHAR(500),
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_time DATETIME,
    update_time DATETIME
);

CREATE TABLE IF NOT EXISTS application_score_reason (
    application_id VARCHAR(12) NOT NULL,
    reason_order INT NOT NULL,
    reason_text VARCHAR(300) NOT NULL,
    score_delta INT,
    PRIMARY KEY (application_id, reason_order)
);

CREATE TABLE IF NOT EXISTS followup_task (
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
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS followup_record (
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
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS warning_record (
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
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS operation_log (
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
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS article_tag (
    article_id INT NOT NULL,
    tag_name VARCHAR(40) NOT NULL,
    PRIMARY KEY (article_id, tag_name)
);

CREATE TABLE IF NOT EXISTS system_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiver_id VARCHAR(12) NOT NULL,
    message_type VARCHAR(30) NOT NULL,
    title VARCHAR(120) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    biz_type VARCHAR(40),
    biz_id VARCHAR(40),
    read_status TINYINT(1) NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'VALID',
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    create_by VARCHAR(12),
    update_by VARCHAR(12),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
