-- Physical 3NF cleanup for database: cat_adoption_system
-- Run after db_normalize_3nf.sql has created and backfilled relation tables.

ALTER TABLE adoption_agreement
    DROP INDEX idx_agreement_adopter,
    DROP INDEX idx_agreement_cat,
    DROP INDEX idx_agreement_user;

ALTER TABLE followup_task
    DROP INDEX idx_followup_task_adopter,
    DROP INDEX idx_followup_task_cat,
    DROP INDEX idx_followup_task_user;

ALTER TABLE followup_record
    DROP INDEX idx_followup_record_adopter,
    DROP INDEX idx_followup_record_application,
    DROP INDEX idx_followup_record_cat,
    DROP INDEX idx_followup_record_user;

ALTER TABLE t_cat
    DROP COLUMN tags;

ALTER TABLE t_article
    DROP COLUMN tags;

ALTER TABLE t_notice
    DROP COLUMN publisher,
    DROP COLUMN published_at,
    DROP COLUMN enabled,
    DROP COLUMN target_roles;

ALTER TABLE t_application
    DROP COLUMN review_note,
    DROP COLUMN interview_note,
    DROP COLUMN agreement_no,
    DROP COLUMN reviewed_at,
    DROP COLUMN handed_over_at,
    DROP COLUMN initial_auditor_id,
    DROP COLUMN initial_audit_note,
    DROP COLUMN initial_audited_at,
    DROP COLUMN final_auditor_id,
    DROP COLUMN final_audit_note,
    DROP COLUMN final_audited_at,
    DROP COLUMN risk_score,
    DROP COLUMN risk_tags,
    DROP COLUMN score_reasons;

ALTER TABLE adoption_audit
    DROP COLUMN cat_id,
    DROP COLUMN user_id,
    DROP COLUMN risk_tags;

ALTER TABLE adoption_agreement
    DROP COLUMN cat_id,
    DROP COLUMN user_id,
    DROP COLUMN adopter_id,
    DROP COLUMN content,
    DROP COLUMN handover_place,
    DROP COLUMN handover_person;

ALTER TABLE followup_task
    DROP COLUMN cat_id,
    DROP COLUMN user_id,
    DROP COLUMN adopter_id,
    DROP COLUMN round_name;

ALTER TABLE followup_record
    DROP COLUMN application_id,
    DROP COLUMN cat_id,
    DROP COLUMN user_id,
    DROP COLUMN agreement_id,
    DROP COLUMN adopter_id,
    DROP COLUMN environment_desc,
    DROP COLUMN abnormal_desc;

ALTER TABLE t_generic_collect
    DROP COLUMN title,
    DROP COLUMN image_url;
