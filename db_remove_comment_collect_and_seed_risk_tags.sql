-- Remove unused comment/collect feature tables and seed normalized risk tag tables.
-- Database: cat_adoption_system

DROP TABLE IF EXISTS t_comment;
DROP TABLE IF EXISTS t_generic_collect;

INSERT IGNORE INTO application_risk_tag (application_id, tag_name)
SELECT application_id,
       CASE risk_level
           WHEN 'LOW' THEN 'RISK_LOW'
           WHEN 'MEDIUM' THEN 'RISK_MEDIUM'
           WHEN 'HIGH' THEN 'RISK_HIGH'
           ELSE CONCAT('RISK_', risk_level)
       END
FROM t_application
WHERE risk_level IS NOT NULL AND TRIM(risk_level) <> '';

INSERT IGNORE INTO application_risk_tag (application_id, tag_name)
SELECT application_id,
       CASE
           WHEN score >= 90 THEN 'SCORE_EXCELLENT'
           WHEN score >= 80 THEN 'SCORE_GOOD'
           WHEN score >= 60 THEN 'SCORE_PASS'
           ELSE 'SCORE_LOW'
       END
FROM t_application
WHERE score IS NOT NULL;

INSERT IGNORE INTO adoption_audit_risk_tag (audit_id, tag_name)
SELECT id,
       CASE audit_result
           WHEN 'APPROVED' THEN 'AUDIT_APPROVED'
           WHEN 'REJECTED' THEN 'AUDIT_REJECTED'
           ELSE CONCAT('AUDIT_', audit_result)
       END
FROM adoption_audit
WHERE audit_result IS NOT NULL AND TRIM(audit_result) <> '';

INSERT IGNORE INTO adoption_audit_risk_tag (audit_id, tag_name)
SELECT id,
       CASE audit_stage
           WHEN 'INITIAL' THEN 'STAGE_INITIAL'
           WHEN 'FINAL' THEN 'STAGE_FINAL'
           ELSE CONCAT('STAGE_', audit_stage)
       END
FROM adoption_audit
WHERE audit_stage IS NOT NULL AND TRIM(audit_stage) <> '';

SELECT 'application_risk_tag' AS table_name, COUNT(*) AS row_count FROM application_risk_tag
UNION ALL
SELECT 'adoption_audit_risk_tag', COUNT(*) FROM adoption_audit_risk_tag;
