-- Migration notes for converting the legacy demo schema to the strict 3NF schema.
-- Do not run this blindly against a production database. Back up first.
-- Recommended flow:
--   1. Create a new database, for example cat_adoption_system_3nf.
--   2. Run schema-3nf.sql in that database.
--   3. Copy data from the legacy database using the INSERT ... SELECT statements below.
--   4. Point the application to the new database after mapper/service adaptation is complete.

-- The current legacy schema contains compatibility columns that cannot be
-- dropped while the old Java mappers are still running. This file documents
-- the normalization mapping and contains the safe data-copy shape.

-- 1NF fixes: multi-value fields.
-- Legacy: t_cat.tags -> 3NF: cat_tag(cat_id, tag_name)
INSERT IGNORE INTO cat_tag (cat_id, tag_name)
WITH RECURSIVE split_cat_tags AS (
    SELECT cat_id,
           TRIM(SUBSTRING_INDEX(tags, ',', 1)) AS tag_name,
           CASE
               WHEN tags IS NULL OR tags = '' OR tags NOT LIKE '%,%' THEN ''
               ELSE SUBSTRING(tags, LENGTH(SUBSTRING_INDEX(tags, ',', 1)) + 2)
           END AS rest
    FROM cat_adoption_system.t_cat
    WHERE tags IS NOT NULL AND tags <> ''
    UNION ALL
    SELECT cat_id,
           TRIM(SUBSTRING_INDEX(rest, ',', 1)) AS tag_name,
           CASE
               WHEN rest = '' OR rest NOT LIKE '%,%' THEN ''
               ELSE SUBSTRING(rest, LENGTH(SUBSTRING_INDEX(rest, ',', 1)) + 2)
           END AS rest
    FROM split_cat_tags
    WHERE rest <> ''
)
SELECT cat_id, tag_name
FROM split_cat_tags
WHERE tag_name <> '';

-- Legacy: t_article.tags -> 3NF: article_tag(article_id, tag_name)
INSERT IGNORE INTO article_tag (article_id, tag_name)
WITH RECURSIVE split_article_tags AS (
    SELECT article_id,
           TRIM(SUBSTRING_INDEX(tags, ',', 1)) AS tag_name,
           CASE
               WHEN tags IS NULL OR tags = '' OR tags NOT LIKE '%,%' THEN ''
               ELSE SUBSTRING(tags, LENGTH(SUBSTRING_INDEX(tags, ',', 1)) + 2)
           END AS rest
    FROM cat_adoption_system.t_article
    WHERE tags IS NOT NULL AND tags <> ''
    UNION ALL
    SELECT article_id,
           TRIM(SUBSTRING_INDEX(rest, ',', 1)) AS tag_name,
           CASE
               WHEN rest = '' OR rest NOT LIKE '%,%' THEN ''
               ELSE SUBSTRING(rest, LENGTH(SUBSTRING_INDEX(rest, ',', 1)) + 2)
           END AS rest
    FROM split_article_tags
    WHERE rest <> ''
)
SELECT article_id, tag_name
FROM split_article_tags
WHERE tag_name <> '';

-- Legacy: t_application.score_reasons -> 3NF: application_score_reason.
-- score_reasons is free text in the legacy schema, so it is copied as one
-- ordered reason unless the application is later changed to store structured reasons.
INSERT IGNORE INTO application_score_reason (application_id, reason_order, reason_text, score_delta)
SELECT application_id, 1, score_reasons, NULL
FROM cat_adoption_system.t_application
WHERE score_reasons IS NOT NULL AND score_reasons <> '';

-- 2NF/3NF fixes: duplicated compatibility columns.
-- Keep only the canonical columns in schema-3nf.sql:
--   system_message.receiver_id, read_status
--   t_notice.publisher_id, publish_status, publish_time
--   adoption_agreement.agreement_content, handover_location
--   followup_record.environment_description, abnormal_description
--   warning_record.content, handle_comment
--   dict_item.item_label

-- Example data copy patterns for canonical columns:
-- INSERT INTO system_message (...)
-- SELECT COALESCE(receiver_id, user_id), message_type, title, content,
--        COALESCE(biz_type, related_type), COALESCE(biz_id, related_id),
--        CASE WHEN read_flag = 1 THEN 'READ' ELSE COALESCE(read_status, 'UNREAD') END,
--        read_time, status, deleted, create_by, update_by, create_time, update_time
-- FROM cat_adoption_system.system_message;

-- INSERT INTO adoption_agreement (...)
-- SELECT agreement_no, application_id,
--        COALESCE(agreement_content, content),
--        generated_time, handover_time,
--        COALESCE(handover_location, handover_place),
--        handover_user_id, adopter_confirmed, volunteer_confirmed,
--        remark, status, deleted, create_by, update_by, create_time, update_time
-- FROM cat_adoption_system.adoption_agreement;

-- Strict 3NF columns intentionally removed from the clean schema:
--   t_cat.tags
--   t_article.tags
--   t_application.agreement_no, reviewed_at, handed_over_at,
--       initial_auditor_id, initial_audit_note, initial_audited_at,
--       final_auditor_id, final_audit_note, final_audited_at,
--       risk_tags, score_reasons
--   adoption_audit.cat_id, user_id, risk_tags
--   adoption_agreement.cat_id, user_id, adopter_id, content, handover_place, handover_person
--   followup_task.cat_id, user_id, adopter_id, round_name
--   followup_record.application_id, agreement_id, cat_id, user_id, adopter_id,
--       environment_desc, abnormal_desc
--   warning_record.related_type, related_id, description, handle_opinion, handled_time,
--       biz_type, biz_id
--   operation_log.operator_name
--   t_notice.publisher, published_at, enabled
--   system_message.user_id, related_type, related_id, read_flag
--   dict_item.item_value, dict_label, dict_value
--   t_generic_collect.title, image_url
--   t_product_order.amount
