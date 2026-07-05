SET NAMES utf8mb4;

UPDATE t_user
SET user_name = '李同学',
    school_no = '2024210001',
    phone = '13755107003',
    id_card = '340111200201010037',
    college = '计算机与信息学院',
    pet_experience = '照顾过家中猫咪三年，了解疫苗、驱虫、绝育和封窗要求。',
    role_code = 'STUDENT',
    status = 1
WHERE user_id = 'U2026050703';

UPDATE t_user
SET user_name = '周同学',
    school_no = '2022210006',
    phone = '13955107006',
    id_card = '340111199905210066',
    college = '校园流浪猫志愿服务队',
    pet_experience = '负责上报核实、救助建档、审核协助和回访。',
    role_code = 'VOLUNTEER',
    status = 1
WHERE user_id = 'UDEMOVOL';

DELETE fr FROM followup_record fr
JOIN followup_task ft ON ft.id = fr.task_id
WHERE ft.application_id IN ('APP260626110', 'APP260630201');

DELETE FROM followup_task
WHERE application_id IN ('APP260626110', 'APP260630201');

DELETE FROM adoption_agreement
WHERE application_id = 'APP260630201';

DELETE FROM adoption_audit
WHERE application_id = 'APP260630201';

DELETE FROM t_application
WHERE application_id = 'APP260630201';

INSERT INTO t_application
(application_id, user_id, cat_id, housing_info, family_attitude, pet_experience, economic_ability,
 promise_accepted, commitment_text, extra_reason, score, risk_level, score_reasons, apply_status,
 applied_at, reviewed_at, handed_over_at, create_by, update_by, updated_at, deleted)
VALUES
('APP260630201', 'UDEMOVOL', 'CAT260501007', '校外长期租住，已封窗并安装阳台防护网。', '家人支持，志愿服务队已知情。', '长期参与校园猫照护，熟悉疫苗、驱虫和绝育回访要求。', '有稳定收入，可承担猫粮、猫砂、疫苗和必要医疗费用。',
 1, '承诺不弃养，不私自转送，按 7/30/90 天节点提交回访。', '志愿者本人正式认养八嘎，仍按普通认养流程接受审核和回访。', 95, 'LOW', '志愿者有长期照护经验，住所稳定，回访配合度高', 'HANDED_OVER',
 '2026-06-30 14:10:00', '2026-06-30 16:20:00', '2026-06-30 17:30:00', 'UDEMOVOL', 'U2026050701', '2026-06-30 17:30:00', 0);

INSERT INTO adoption_audit
(application_id, auditor_id, audit_stage, audit_result, audit_opinion, before_status, after_status, audit_time,
 status, create_by, update_by, create_time, update_time)
VALUES
('APP260630201', 'U2026050701', 'FINAL', 'APPROVED', '志愿者本人认养，照护经验充分，终审通过并完成交接。', 'PENDING_FINAL', 'PENDING_HANDOVER', '2026-06-30 16:20:00', 'VALID', 'U2026050701', 'U2026050701', '2026-06-30 16:20:00', '2026-06-30 16:20:00');

INSERT INTO adoption_agreement
(agreement_no, application_id, cat_id, user_id, adopter_id, content, agreement_content, status, generated_time,
 handover_time, handover_place, handover_location, handover_person, handover_user_id,
 adopter_confirmed, volunteer_confirmed, remark, deleted, create_by, update_by, create_time, update_time)
VALUES
('AGR202606300201', 'APP260630201', 'CAT260501007', 'UDEMOVOL', 'UDEMOVOL',
 '志愿者认养协议：认养人承诺继续按平台流程接受 7/30/90 天回访，不因志愿者身份免除回访义务。',
 '志愿者认养协议：认养人承诺继续按平台流程接受 7/30/90 天回访，不因志愿者身份免除回访义务。',
 'HANDED_OVER', '2026-06-30 16:30:00', '2026-06-30 17:30:00', '翡翠湖校区志愿者服务点', '翡翠湖校区志愿者服务点',
 'U2026050701', 'U2026050701', 1, 1, '志愿者本人认养，系统自动生成个人回访任务', 0,
 'U2026050701', 'U2026050701', '2026-06-30 16:30:00', '2026-06-30 17:30:00');

UPDATE t_cat
SET cat_status = 'FOLLOWING', updated_at = '2026-06-30 17:30:00'
WHERE cat_id = 'CAT260501007';

INSERT INTO followup_task
(application_id, agreement_id, cat_id, user_id, adopter_id, plan_date, actual_date, round_name, task_type,
 status, handler_id, abnormal_flag, deleted, create_by, update_by, create_time, update_time)
SELECT 'APP260630201', ag.id, 'CAT260501007', 'UDEMOVOL', 'UDEMOVOL', '2026-07-07', '2026-07-07',
       'DAY_7', 'DAY_7', 'COMPLETED', NULL, 0, 0, 'U2026050701', 'UDEMOVOL', '2026-06-30 17:30:00', '2026-07-07 20:10:00'
FROM adoption_agreement ag WHERE ag.application_id = 'APP260630201'
UNION ALL
SELECT 'APP260630201', ag.id, 'CAT260501007', 'UDEMOVOL', 'UDEMOVOL', '2026-07-30', NULL,
       'DAY_30', 'DAY_30', 'PENDING', NULL, 0, 0, 'U2026050701', 'U2026050701', '2026-06-30 17:30:00', '2026-06-30 17:30:00'
FROM adoption_agreement ag WHERE ag.application_id = 'APP260630201'
UNION ALL
SELECT 'APP260630201', ag.id, 'CAT260501007', 'UDEMOVOL', 'UDEMOVOL', '2026-09-28', NULL,
       'DAY_90', 'DAY_90', 'PENDING', NULL, 0, 0, 'U2026050701', 'U2026050701', '2026-06-30 17:30:00', '2026-06-30 17:30:00'
FROM adoption_agreement ag WHERE ag.application_id = 'APP260630201';

INSERT INTO followup_record
(task_id, application_id, agreement_id, cat_id, user_id, adopter_id, content, cat_condition,
 environment_description, environment_desc, photo_url, abnormal_flag, abnormal_description, abnormal_desc,
 volunteer_comment, submit_time, status, deleted, create_by, update_by, create_time, update_time)
SELECT ft.id, ft.application_id, ft.agreement_id, ft.cat_id, ft.user_id, COALESCE(ft.adopter_id, ft.user_id),
       '志愿者认养第 7 天回访：八嘎已适应新家，会主动进食和使用猫砂盆。', '精神稳定，食欲正常，排便正常。',
       '阳台和窗户已安装防护网，饮水、猫砂盆和躲藏空间齐全。', '阳台和窗户已安装防护网，饮水、猫砂盆和躲藏空间齐全。',
       '/uploads/cats/cat_07_03.jpg', 0, NULL, NULL, '系统自动生成任务，认养人按时提交。',
       '2026-07-07 20:10:00', 'SUBMITTED', 0, 'UDEMOVOL', 'UDEMOVOL', '2026-07-07 20:10:00', '2026-07-07 20:10:00'
FROM followup_task ft
WHERE ft.application_id = 'APP260630201' AND ft.task_type = 'DAY_7';
