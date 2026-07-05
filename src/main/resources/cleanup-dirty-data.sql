-- Dirty data inspection and soft cleanup script.
-- Usage:
-- 1. Run SELECT sections first and review rows manually.
-- 2. Run UPDATE sections only after confirming the selected rows are invalid test data.
-- 3. This script does not physically DELETE rows and does not change normal demo data intentionally.

SET @dirty_pattern = 'Codex|undefined|null|test|asdf|qwer|测试测试|随便|[?？]{3,}';

-- 1) Suspicious cat records.
SELECT cat_id, cat_name, found_place, description, cat_status, deleted, created_at, updated_at
FROM t_cat
WHERE COALESCE(deleted, 0) = 0
  AND (
      cat_name REGEXP @dirty_pattern
      OR found_place REGEXP @dirty_pattern
      OR description REGEXP @dirty_pattern
      OR cat_name LIKE 'Codex%'
  );

-- Soft-hide suspicious cat records after review.
UPDATE t_cat
SET deleted = 1,
    updated_at = CURRENT_TIMESTAMP
WHERE COALESCE(deleted, 0) = 0
  AND (
      cat_name REGEXP @dirty_pattern
      OR found_place REGEXP @dirty_pattern
      OR description REGEXP @dirty_pattern
      OR cat_name LIKE 'Codex%'
  );

-- 2) Suspicious clue / rescue report records.
SELECT report_id, reporter_id, reporter_name, found_place, health_description,
       urgency_level, report_status, verify_comment, deleted, reported_at
FROM t_rescue_report
WHERE COALESCE(deleted, 0) = 0
  AND (
      reporter_name REGEXP @dirty_pattern
      OR found_place REGEXP @dirty_pattern
      OR health_description REGEXP @dirty_pattern
      OR verify_comment REGEXP @dirty_pattern
  );

-- Mark suspicious clues invalid and soft-hide them after review.
UPDATE t_rescue_report
SET report_status = 'INVALID',
    deleted = 1,
    updated_at = CURRENT_TIMESTAMP
WHERE COALESCE(deleted, 0) = 0
  AND (
      reporter_name REGEXP @dirty_pattern
      OR found_place REGEXP @dirty_pattern
      OR health_description REGEXP @dirty_pattern
      OR verify_comment REGEXP @dirty_pattern
  );

-- 3) Suspicious adoption applications.
SELECT application_id, user_id, cat_id, housing_info, pet_experience,
       family_attitude, economic_ability, commitment_text, extra_reason,
       apply_status, deleted, applied_at
FROM t_application
WHERE COALESCE(deleted, 0) = 0
  AND (
      housing_info REGEXP @dirty_pattern
      OR pet_experience REGEXP @dirty_pattern
      OR family_attitude REGEXP @dirty_pattern
      OR economic_ability REGEXP @dirty_pattern
      OR commitment_text REGEXP @dirty_pattern
      OR extra_reason REGEXP @dirty_pattern
  );

-- Soft-hide suspicious applications after review.
UPDATE t_application
SET deleted = 1,
    updated_at = CURRENT_TIMESTAMP
WHERE COALESCE(deleted, 0) = 0
  AND (
      housing_info REGEXP @dirty_pattern
      OR pet_experience REGEXP @dirty_pattern
      OR family_attitude REGEXP @dirty_pattern
      OR economic_ability REGEXP @dirty_pattern
      OR commitment_text REGEXP @dirty_pattern
      OR extra_reason REGEXP @dirty_pattern
  );

-- 4) Suspicious medical records.
SELECT medical_id, cat_id, hospital, record_type, treatment, doctor_note,
       health_level, deleted, created_at
FROM t_medical_record
WHERE COALESCE(deleted, 0) = 0
  AND (
      hospital REGEXP @dirty_pattern
      OR treatment REGEXP @dirty_pattern
      OR doctor_note REGEXP @dirty_pattern
  );

-- Soft-hide suspicious medical records after review.
UPDATE t_medical_record
SET deleted = 1,
    updated_at = CURRENT_TIMESTAMP
WHERE COALESCE(deleted, 0) = 0
  AND (
      hospital REGEXP @dirty_pattern
      OR treatment REGEXP @dirty_pattern
      OR doctor_note REGEXP @dirty_pattern
  );

-- 5) Suspicious follow-up records.
SELECT id, task_id, content, cat_condition, environment_description,
       environment_desc, abnormal_description, abnormal_desc, deleted, submit_time
FROM followup_record
WHERE COALESCE(deleted, 0) = 0
  AND (
      content REGEXP @dirty_pattern
      OR cat_condition REGEXP @dirty_pattern
      OR environment_description REGEXP @dirty_pattern
      OR environment_desc REGEXP @dirty_pattern
      OR abnormal_description REGEXP @dirty_pattern
      OR abnormal_desc REGEXP @dirty_pattern
  );

-- Soft-hide suspicious follow-up records after review.
UPDATE followup_record
SET deleted = 1,
    update_time = CURRENT_TIMESTAMP
WHERE COALESCE(deleted, 0) = 0
  AND (
      content REGEXP @dirty_pattern
      OR cat_condition REGEXP @dirty_pattern
      OR environment_description REGEXP @dirty_pattern
      OR environment_desc REGEXP @dirty_pattern
      OR abnormal_description REGEXP @dirty_pattern
      OR abnormal_desc REGEXP @dirty_pattern
  );

-- 6) Suspicious user profile records. Review only by default.
SELECT user_id, user_name, school_no, phone, college, pet_experience, status, created_at
FROM t_user
WHERE user_name REGEXP @dirty_pattern
   OR college REGEXP @dirty_pattern
   OR pet_experience REGEXP @dirty_pattern;

-- Optional account disable after manual review. Keep commented to avoid locking normal users.
-- UPDATE t_user
-- SET status = 0
-- WHERE status = 1
--   AND (
--       user_name REGEXP @dirty_pattern
--       OR college REGEXP @dirty_pattern
--       OR pet_experience REGEXP @dirty_pattern
--   );
