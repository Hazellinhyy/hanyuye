CREATE TABLE IF NOT EXISTS notice_target_role (
    notice_id varchar(12) NOT NULL,
    role_code varchar(20) NOT NULL,
    PRIMARY KEY (notice_id, role_code),
    CONSTRAINT fk_notice_target_role_notice
        FOREIGN KEY (notice_id) REFERENCES t_notice (notice_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS application_risk_tag (
    application_id varchar(12) NOT NULL,
    tag_name varchar(40) NOT NULL,
    PRIMARY KEY (application_id, tag_name),
    CONSTRAINT fk_application_risk_tag_application
        FOREIGN KEY (application_id) REFERENCES t_application (application_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS adoption_audit_risk_tag (
    audit_id bigint NOT NULL,
    tag_name varchar(40) NOT NULL,
    PRIMARY KEY (audit_id, tag_name),
    CONSTRAINT fk_audit_risk_tag_audit
        FOREIGN KEY (audit_id) REFERENCES adoption_audit (id)
        ON DELETE CASCADE
);

INSERT IGNORE INTO cat_tag (cat_id, tag_name)
SELECT c.cat_id, TRIM(j.tag_name)
FROM t_cat c
JOIN JSON_TABLE(
    CONCAT(
        '["',
        REPLACE(REPLACE(c.tags, ';', ','), ',', '","'),
        '"]'
    ),
    '$[*]' COLUMNS(tag_name varchar(40) PATH '$')
) j
WHERE c.tags IS NOT NULL
  AND TRIM(c.tags) <> ''
  AND TRIM(j.tag_name) <> '';

INSERT IGNORE INTO article_tag (article_id, tag_name)
SELECT a.article_id, TRIM(j.tag_name)
FROM t_article a
JOIN JSON_TABLE(
    CONCAT(
        '["',
        REPLACE(REPLACE(a.tags, ';', ','), ',', '","'),
        '"]'
    ),
    '$[*]' COLUMNS(tag_name varchar(40) PATH '$')
) j
WHERE a.tags IS NOT NULL
  AND TRIM(a.tags) <> ''
  AND TRIM(j.tag_name) <> '';

INSERT IGNORE INTO notice_target_role (notice_id, role_code)
SELECT n.notice_id, TRIM(j.role_code)
FROM t_notice n
JOIN JSON_TABLE(
    CONCAT(
        '["',
        REPLACE(REPLACE(COALESCE(NULLIF(TRIM(n.target_roles), ''), 'STUDENT,VOLUNTEER,HOSPITAL,ADMIN'), ';', ','), ',', '","'),
        '"]'
    ),
    '$[*]' COLUMNS(role_code varchar(20) PATH '$')
) j
WHERE TRIM(j.role_code) <> '';

INSERT IGNORE INTO application_risk_tag (application_id, tag_name)
SELECT a.application_id, TRIM(j.tag_name)
FROM t_application a
JOIN JSON_TABLE(
    CONCAT(
        '["',
        REPLACE(REPLACE(a.risk_tags, ';', ','), ',', '","'),
        '"]'
    ),
    '$[*]' COLUMNS(tag_name varchar(40) PATH '$')
) j
WHERE a.risk_tags IS NOT NULL
  AND TRIM(a.risk_tags) <> ''
  AND TRIM(j.tag_name) <> '';

INSERT IGNORE INTO adoption_audit_risk_tag (audit_id, tag_name)
SELECT aa.id, TRIM(j.tag_name)
FROM adoption_audit aa
JOIN JSON_TABLE(
    CONCAT(
        '["',
        REPLACE(REPLACE(aa.risk_tags, ';', ','), ',', '","'),
        '"]'
    ),
    '$[*]' COLUMNS(tag_name varchar(40) PATH '$')
) j
WHERE aa.risk_tags IS NOT NULL
  AND TRIM(aa.risk_tags) <> ''
  AND TRIM(j.tag_name) <> '';

SELECT 'notice_target_role' AS table_name, COUNT(*) AS row_count FROM notice_target_role
UNION ALL
SELECT 'application_risk_tag', COUNT(*) FROM application_risk_tag
UNION ALL
SELECT 'adoption_audit_risk_tag', COUNT(*) FROM adoption_audit_risk_tag;
