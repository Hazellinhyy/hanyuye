-- Repair notice data after physical 3NF normalization.
-- Database: cat_adoption_system

UPDATE t_notice
SET publisher_id = COALESCE(
        publisher_id,
        (SELECT user_id FROM t_user WHERE role_code = 'ADMIN' ORDER BY created_at LIMIT 1)
    )
WHERE publisher_id IS NULL;

INSERT IGNORE INTO notice_target_role (notice_id, role_code)
SELECT n.notice_id, r.role_code
FROM t_notice n
JOIN (
    SELECT 'STUDENT' AS role_code
    UNION ALL SELECT 'VOLUNTEER'
    UNION ALL SELECT 'HOSPITAL'
    UNION ALL SELECT 'ADMIN'
) r
WHERE NOT EXISTS (
    SELECT 1
    FROM notice_target_role existing
    WHERE existing.notice_id = n.notice_id
);

UPDATE t_notice
SET publish_time = COALESCE(publish_time, create_time, CURRENT_TIMESTAMP)
WHERE publish_status = 'PUBLISHED';

SELECT COUNT(*) AS notice_count FROM t_notice;
SELECT COUNT(*) AS notice_target_role_count FROM notice_target_role;
