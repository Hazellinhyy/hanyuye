-- Repair cat rows that were created with temporary/debug names.
-- Run this once against the existing MySQL database if the admin cat list
-- already contains names such as Codex??? or question-mark placeholders.

UPDATE t_cat
SET deleted = 1,
    cat_name = '待清理演示数据',
    updated_at = CURRENT_TIMESTAMP
WHERE cat_name IS NULL
   OR TRIM(cat_name) = ''
   OR REPLACE(REPLACE(REPLACE(LOWER(cat_name), CHAR(13), ''), CHAR(10), ''), ' ', '') LIKE 'codex%'
   OR cat_name LIKE '%???%';
