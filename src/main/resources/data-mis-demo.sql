-- Demo data for MIS stage 2. Run after schema.sql, data.sql and schema-mis-upgrade.sql.

INSERT IGNORE INTO t_rescue_report
(report_id, reporter_id, reporter_name, reporter_phone, found_place, found_area, found_time,
 color, gender, health_description, urgent, urgency_level, photo_url, report_status,
 reported_at, update_time, deleted)
VALUES
('RP260626001', 'U2026050703', '李同学', '13700000000', '翡翠湖校区二食堂北门', '翡翠湖校区', '2026-06-26 08:30:00',
 '橘白', 'U', '体型偏瘦，常在二食堂北门附近讨食。', 0, 'NORMAL', '/uploads/cats/cat_01_01.jpg', 'PENDING_VERIFY',
 '2026-06-26 08:35:00', '2026-06-26 08:35:00', 0),
('RP260626002', 'U2026050703', '李同学', '13700000000', '屯溪路校区主教学楼东侧', '屯溪路校区', '2026-06-26 09:15:00',
 '狸花', 'U', '疑似腿部受伤，行动缓慢，需要志愿者尽快查看。', 1, 'URGENT', '/uploads/cats/cat_02_01.jpg', 'PENDING_VERIFY',
 '2026-06-26 09:20:00', '2026-06-26 09:20:00', 0),
('RP260626003', 'U2026050703', '李同学', '13700000000', '翡翠湖校区图书馆西门', '翡翠湖校区', '2026-06-26 10:05:00',
 '奶牛', 'U', '亲人，常跟随学生进入大厅，建议核实是否适合建档。', 0, 'HIGH', '/uploads/cats/cat_03_01.jpg', 'PENDING_VERIFY',
 '2026-06-26 10:10:00', '2026-06-26 10:10:00', 0),
('RP260626004', 'U2026050703', '李同学', '13700000000', '翡翠湖校区二食堂北门', '翡翠湖校区', '2026-06-26 11:00:00',
 '橘白', 'U', '与 RP260626001 疑似同一只猫。', 0, 'NORMAL', '/uploads/cats/cat_01_02.jpg', 'DUPLICATE',
 '2026-06-26 11:05:00', '2026-06-26 11:30:00', 0),
('RP260626005', 'U2026050703', '李同学', '13700000000', '屯溪路校区南门外', '屯溪路校区', '2026-06-26 12:00:00',
 '未知', 'U', '照片模糊，现场未再发现猫咪。', 0, 'NORMAL', '/uploads/cats/no-photo.svg', 'INVALID',
 '2026-06-26 12:05:00', '2026-06-26 12:40:00', 0),
('RP260626006', 'U2026050703', '李同学', '13700000000', '翡翠湖校区材料楼后侧', '翡翠湖校区', '2026-06-25 16:20:00',
 '三花', 'F', '已由志愿者核实并建立档案。', 0, 'NORMAL', '/uploads/cats/cat_04_01.jpg', 'CREATED_CAT',
 '2026-06-25 16:25:00', '2026-06-25 18:00:00', 0);

UPDATE t_rescue_report
SET verify_user_id = 'U2026050702', verify_result = 'DUPLICATE', verify_comment = '与 RP260626001 地点和照片高度相似。',
    verify_time = '2026-06-26 11:30:00'
WHERE report_id = 'RP260626004';

UPDATE t_rescue_report
SET verify_user_id = 'U2026050702', verify_result = 'INVALID', verify_comment = '现场未找到猫咪，照片无法判断。',
    verify_time = '2026-06-26 12:40:00'
WHERE report_id = 'RP260626005';

UPDATE t_rescue_report
SET verify_user_id = 'U2026050702', verify_result = 'VALID', verify_comment = '已核实并建档。',
    verify_time = '2026-06-25 18:00:00', created_cat_id = 'CAT260501004'
WHERE report_id = 'RP260626006';

-- Stage 3 demo cats and medical records.
UPDATE t_cat SET cat_status = 'ADOPTABLE', health_level = 'A', vaccinated = 1, sterilized = 1, updated_at = '2026-06-26 13:00:00'
WHERE cat_id IN ('CAT260501001', 'CAT260501002', 'CAT260501003');

UPDATE t_cat SET cat_status = 'OBSERVING', health_level = 'B', updated_at = '2026-06-26 13:05:00'
WHERE cat_id IN ('CAT260501005', 'CAT260501006');

UPDATE t_cat SET cat_status = 'MEDICAL', health_level = 'C', updated_at = '2026-06-26 13:10:00'
WHERE cat_id IN ('CAT260501007', 'CAT260501008');

INSERT IGNORE INTO t_medical_record
(medical_id, cat_id, check_date, hospital, health_level, vaccinated, sterilized, treatment, doctor_note,
 record_type, hospital_user_id, cost, attachment_url, abnormal_flag, create_by, update_by, created_at, updated_at, deleted)
VALUES
('MD260626001', 'CAT260501001', '2026-06-20', '合肥合作动物医院', 'A', 1, 1, '常规体检，精神状态良好。', '符合认养发布条件。',
 'CHECKUP', 'U2026050704', 68.00, '/uploads/cats/cat_01_01.jpg', 0, 'U2026050704', 'U2026050704', '2026-06-20 10:00:00', '2026-06-20 10:00:00', 0),
('MD260626002', 'CAT260501002', '2026-06-21', '合肥合作动物医院', 'A', 1, 1, '完成疫苗补打。', '建议按期复查。',
 'VACCINE', 'U2026050704', 120.00, '/uploads/cats/cat_02_01.jpg', 0, 'U2026050704', 'U2026050704', '2026-06-21 11:00:00', '2026-06-21 11:00:00', 0),
('MD260626003', 'CAT260501003', '2026-06-22', '合肥合作动物医院', 'A', 1, 1, '绝育术后恢复良好。', '伤口愈合良好。',
 'STERILIZATION', 'U2026050704', 360.00, '/uploads/cats/cat_03_01.jpg', 0, 'U2026050704', 'U2026050704', '2026-06-22 14:00:00', '2026-06-22 14:00:00', 0),
('MD260626004', 'CAT260501007', '2026-06-23', '合肥合作动物医院', 'C', 0, 0, '有呼吸道症状，需治疗观察。', '异常医疗记录，暂不适合发布认养。',
 'TREATMENT', 'U2026050704', 188.00, '/uploads/cats/cat_07_01.jpg', 1, 'U2026050704', 'U2026050704', '2026-06-23 16:00:00', '2026-06-23 16:00:00', 0);

-- Stage 4 demo adoption applications.
INSERT IGNORE INTO t_application
(application_id, user_id, cat_id, housing_info, family_attitude, pet_experience, economic_ability,
 promise_accepted, commitment_text, extra_reason, score, risk_level, score_reasons, apply_status,
 applied_at, reviewed_at, handed_over_at, create_by, update_by, updated_at, deleted)
VALUES
('APP260626101', 'U2026050703', 'CAT260501001', '校外稳定租住，已封窗。', '家人支持认养。', '有三年养猫经验。', '能承担疫苗、绝育和医疗费用。',
 1, '承诺不弃养并接受回访。', '希望长期照顾蛋黄。', 100, 'LOW', '有养宠经验 +20；居住稳定 +20；接受回访 +20；家庭支持 +15；承诺不弃养 +15；可承担费用 +10', 'PENDING_INITIAL',
 '2026-06-26 14:00:00', NULL, NULL, 'U2026050703', 'U2026050703', '2026-06-26 14:00:00', 0),
('APP260626102', 'U2026050703', 'CAT260501002', '宿舍居住，暂未确认是否允许。', '家人反对但本人想试试。', '没有经验。', '费用不确定。',
 0, '暂未明确承诺。', '冲动认养，后续再看。', 0, 'HIGH', '信息不完整 -10；存在风险描述 -20', 'PENDING_INITIAL',
 '2026-06-26 14:10:00', NULL, NULL, 'U2026050703', 'U2026050703', '2026-06-26 14:10:00', 0),
('APP260626103', 'U2026050703', 'CAT260501003', '校外长期租住，门窗防护到位。', '室友支持。', '照顾过家中猫咪。', '有稳定生活费预算。',
 1, '接受回访，承诺不弃养。', '喜欢奶牛猫。', 85, 'LOW', '有养宠经验 +20；居住稳定 +20；接受回访 +20；家庭支持 +15；可承担费用 +10', 'PENDING_INITIAL',
 '2026-06-26 14:20:00', NULL, NULL, 'U2026050703', 'U2026050703', '2026-06-26 14:20:00', 0),
('APP260626104', 'U2026050703', 'CAT260501009', '校外稳定住所。', '家人同意。', '有养宠经验。', '可承担医疗费用。',
 1, '承诺长期照护。', '申请初审通过待终审。', 90, 'LOW', '材料完整，低风险', 'PENDING_FINAL',
 '2026-06-26 13:00:00', '2026-06-26 15:00:00', NULL, 'U2026050703', 'U2026050702', '2026-06-26 15:00:00', 0),
('APP260626105', 'U2026050703', 'CAT260501010', '校外稳定住所。', '室友同意。', '有照护经验。', '可承担费用。',
 1, '承诺接受回访。', '第二条待终审演示。', 82, 'LOW', '材料较完整', 'PENDING_FINAL',
 '2026-06-26 13:10:00', '2026-06-26 15:10:00', NULL, 'U2026050703', 'U2026050702', '2026-06-26 15:10:00', 0),
('APP260626106', 'U2026050703', 'CAT260501011', '宿舍，条件不稳定。', '家人反对。', '无经验。', '预算不足。',
 0, '未承诺。', '初审拒绝演示。', 15, 'HIGH', '高风险，初审拒绝', 'INITIAL_REJECTED',
 '2026-06-26 12:00:00', '2026-06-26 15:20:00', NULL, 'U2026050703', 'U2026050702', '2026-06-26 15:20:00', 0),
('APP260626107', 'U2026050703', 'CAT260501012', '住所基本稳定。', '家人态度一般。', '有短期经验。', '可承担基础费用。',
 1, '承诺接受回访。', '终审拒绝演示。', 62, 'MEDIUM', '中风险，终审拒绝', 'FINAL_REJECTED',
 '2026-06-26 12:10:00', '2026-06-26 15:40:00', NULL, 'U2026050703', 'U2026050701', '2026-06-26 15:40:00', 0),
('APP260626108', 'U2026050703', 'CAT260501013', '校外稳定住所。', '家人支持。', '有经验。', '可承担费用。',
 1, '承诺不弃养。', '待交接演示。', 95, 'LOW', '低风险，终审通过', 'PENDING_HANDOVER',
 '2026-06-26 12:20:00', '2026-06-26 16:00:00', NULL, 'U2026050703', 'U2026050701', '2026-06-26 16:00:00', 0);

INSERT IGNORE INTO adoption_audit
(application_id, auditor_id, audit_stage, audit_result, audit_opinion, before_status, after_status, audit_time,
 status, create_by, update_by, create_time, update_time)
VALUES
('APP260626104', 'U2026050702', 'INITIAL', 'APPROVED', '材料完整，进入终审。', 'PENDING_INITIAL', 'PENDING_FINAL', '2026-06-26 15:00:00', 'VALID', 'U2026050702', 'U2026050702', '2026-06-26 15:00:00', '2026-06-26 15:00:00'),
('APP260626105', 'U2026050702', 'INITIAL', 'APPROVED', '初审通过。', 'PENDING_INITIAL', 'PENDING_FINAL', '2026-06-26 15:10:00', 'VALID', 'U2026050702', 'U2026050702', '2026-06-26 15:10:00', '2026-06-26 15:10:00'),
('APP260626106', 'U2026050702', 'INITIAL', 'REJECTED', '居住条件不符合认养要求。', 'PENDING_INITIAL', 'INITIAL_REJECTED', '2026-06-26 15:20:00', 'VALID', 'U2026050702', 'U2026050702', '2026-06-26 15:20:00', '2026-06-26 15:20:00'),
('APP260626107', 'U2026050702', 'INITIAL', 'APPROVED', '进入终审。', 'PENDING_INITIAL', 'PENDING_FINAL', '2026-06-26 15:30:00', 'VALID', 'U2026050702', 'U2026050702', '2026-06-26 15:30:00', '2026-06-26 15:30:00'),
('APP260626107', 'U2026050701', 'FINAL', 'REJECTED', '终审认为支持条件不足。', 'PENDING_FINAL', 'FINAL_REJECTED', '2026-06-26 15:40:00', 'VALID', 'U2026050701', 'U2026050701', '2026-06-26 15:40:00', '2026-06-26 15:40:00'),
('APP260626108', 'U2026050702', 'INITIAL', 'APPROVED', '初审通过。', 'PENDING_INITIAL', 'PENDING_FINAL', '2026-06-26 15:50:00', 'VALID', 'U2026050702', 'U2026050702', '2026-06-26 15:50:00', '2026-06-26 15:50:00'),
('APP260626108', 'U2026050701', 'FINAL', 'APPROVED', '终审通过，进入待交接。', 'PENDING_FINAL', 'PENDING_HANDOVER', '2026-06-26 16:00:00', 'VALID', 'U2026050701', 'U2026050701', '2026-06-26 16:00:00', '2026-06-26 16:00:00');

-- Stage 5 demo agreement handover and follow-up task data.
INSERT IGNORE INTO t_cat
(cat_id, cat_name, found_place, found_date, gender, color, age_estimate, personality, health_level,
 sterilized, vaccinated, cat_status, cover_url, tags, description, created_at, updated_at)
VALUES
('CAT260501013', '奶盖', '翡翠湖校区图书馆西门', '2026-05-18', 'F', '奶牛', '一岁左右', '亲人，适合室内饲养。', 'A',
 1, 1, 'APPLYING', '/uploads/cats/cat_03_01.jpg', '', '第五阶段待交接演示猫咪。', '2026-05-18 10:00:00', '2026-06-26 16:00:00'),
('CAT260501014', '青团', '翡翠湖校区材料楼后侧', '2026-05-19', 'M', '狸花', '两岁左右', '安静，喜欢固定活动范围。', 'A',
 1, 1, 'APPLYING', '/uploads/cats/cat_02_01.jpg', '', '第五阶段未生成协议演示猫咪。', '2026-05-19 10:00:00', '2026-06-26 16:10:00'),
('CAT260501015', '豆包', '屯溪路校区主教学楼东侧', '2026-05-20', 'F', '橘白', '八个月左右', '活泼亲人，适合有陪伴时间的认养人。', 'A',
 1, 1, 'APPLYING', '/uploads/cats/cat_01_01.jpg', '', '第五阶段已生成协议待交接演示猫咪。', '2026-05-20 10:00:00', '2026-06-26 16:20:00');

INSERT IGNORE INTO t_application
(application_id, user_id, cat_id, housing_info, family_attitude, pet_experience, economic_ability,
 promise_accepted, commitment_text, extra_reason, score, risk_level, score_reasons, apply_status,
 applied_at, reviewed_at, handed_over_at, create_by, update_by, updated_at, deleted)
VALUES
('APP260626109', 'U2026050703', 'CAT260501014', '校外稳定住所，已封窗。', '家人支持。', '有养猫经验。', '可承担长期费用。',
 1, '承诺不弃养并接受回访。', '待交接且尚未生成协议。', 92, 'LOW', '低风险，终审通过', 'PENDING_HANDOVER',
 '2026-06-26 12:30:00', '2026-06-26 16:10:00', NULL, 'U2026050703', 'U2026050701', '2026-06-26 16:10:00', 0),
('APP260626110', 'U2026050703', 'CAT260501015', '校外合租，室友同意。', '家庭支持。', '有照护经验。', '可承担医疗费用。',
 1, '承诺接受 7/30/90 天回访。', '已生成协议但未交接。', 88, 'LOW', '低风险，终审通过', 'PENDING_HANDOVER',
 '2026-06-26 12:40:00', '2026-06-26 16:20:00', NULL, 'U2026050703', 'U2026050701', '2026-06-26 16:20:00', 0),
('APP260626111', 'U2026050703', 'CAT260501004', '校外稳定住所。', '家人和室友均支持。', '有三年养猫经验。', '可承担全部必要费用。',
 1, '承诺不弃养并配合回访。', '已完成交接并生成回访任务。', 96, 'LOW', '低风险，交接完成', 'HANDED_OVER',
 '2026-06-26 12:50:00', '2026-06-26 16:30:00', '2026-06-26 17:00:00', 'U2026050703', 'U2026050701', '2026-06-26 17:00:00', 0);

INSERT IGNORE INTO t_application
(application_id, user_id, cat_id, housing_info, family_attitude, pet_experience, economic_ability,
 promise_accepted, commitment_text, extra_reason, score, risk_level, score_reasons, apply_status,
 applied_at, reviewed_at, handed_over_at, create_by, update_by, updated_at, deleted)
VALUES
('APP260630201', 'UDEMOVOL', 'CAT260501007', '校外长期租住，已封窗并安装阳台防护网。', '家人支持，志愿服务队已知情。', '长期参与校园猫照护，熟悉疫苗、驱虫和绝育回访要求。', '有稳定收入，可承担猫粮、猫砂、疫苗和必要医疗费用。',
 1, '承诺不弃养，不私自转送，按 7/30/90 天节点提交回访。', '志愿者本人正式认养八嘎，仍按普通认养流程接受审核和回访。', 95, 'LOW', '志愿者有长期照护经验，住所稳定，回访配合度高', 'HANDED_OVER',
 '2026-06-30 14:10:00', '2026-06-30 16:20:00', '2026-06-30 17:30:00', 'UDEMOVOL', 'U2026050701', '2026-06-30 17:30:00', 0);

INSERT IGNORE INTO adoption_audit
(application_id, auditor_id, audit_stage, audit_result, audit_opinion, before_status, after_status, audit_time,
 status, create_by, update_by, create_time, update_time)
VALUES
('APP260626109', 'U2026050701', 'FINAL', 'APPROVED', '终审通过，进入待交接。', 'PENDING_FINAL', 'PENDING_HANDOVER', '2026-06-26 16:10:00', 'VALID', 'U2026050701', 'U2026050701', '2026-06-26 16:10:00', '2026-06-26 16:10:00'),
('APP260626110', 'U2026050701', 'FINAL', 'APPROVED', '终审通过，协议已生成。', 'PENDING_FINAL', 'PENDING_HANDOVER', '2026-06-26 16:20:00', 'VALID', 'U2026050701', 'U2026050701', '2026-06-26 16:20:00', '2026-06-26 16:20:00'),
('APP260626111', 'U2026050701', 'FINAL', 'APPROVED', '终审通过并完成交接。', 'PENDING_FINAL', 'PENDING_HANDOVER', '2026-06-26 16:30:00', 'VALID', 'U2026050701', 'U2026050701', '2026-06-26 16:30:00', '2026-06-26 16:30:00');

INSERT IGNORE INTO adoption_audit
(application_id, auditor_id, audit_stage, audit_result, audit_opinion, before_status, after_status, audit_time,
 status, create_by, update_by, create_time, update_time)
VALUES
('APP260630201', 'U2026050701', 'FINAL', 'APPROVED', '志愿者本人认养，照护经验充分，终审通过并完成交接。', 'PENDING_FINAL', 'PENDING_HANDOVER', '2026-06-30 16:20:00', 'VALID', 'U2026050701', 'U2026050701', '2026-06-30 16:20:00', '2026-06-30 16:20:00');

INSERT IGNORE INTO adoption_agreement
(agreement_no, application_id, cat_id, user_id, adopter_id, content, agreement_content, status, generated_time,
 handover_time, handover_place, handover_location, handover_person, handover_user_id,
 adopter_confirmed, volunteer_confirmed, remark, deleted, create_by, update_by, create_time, update_time)
VALUES
('AGR202606260001', 'APP260626110', 'CAT260501015', 'U2026050703', 'U2026050703',
 '演示协议：认养人承诺不弃养、接受定期回访、及时医疗、不私自转送。', '演示协议：认养人承诺不弃养、接受定期回访、及时医疗、不私自转送。',
 'GENERATED', '2026-06-26 16:25:00', NULL, NULL, NULL, NULL, NULL, 0, 0, '已生成待交接演示记录', 0,
 'U2026050701', 'U2026050701', '2026-06-26 16:25:00', '2026-06-26 16:25:00'),
('AGR202606260002', 'APP260626111', 'CAT260501004', 'U2026050703', 'U2026050703',
 '演示协议：已完成交接，系统已生成 7/30/90 天回访任务。', '演示协议：已完成交接，系统已生成 7/30/90 天回访任务。',
 'HANDED_OVER', '2026-06-26 16:35:00', '2026-06-26 17:00:00', '翡翠湖校区志愿者服务点', '翡翠湖校区志愿者服务点',
 'U2026050702', 'U2026050702', 1, 1, '现场确认健康状态正常', 0,
 'U2026050701', 'U2026050701', '2026-06-26 16:35:00', '2026-06-26 17:00:00');

INSERT IGNORE INTO adoption_agreement
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

UPDATE t_cat SET cat_status = 'FOLLOWING', updated_at = '2026-06-26 17:00:00'
WHERE cat_id = 'CAT260501004';

UPDATE t_cat SET cat_status = 'FOLLOWING', updated_at = '2026-06-30 17:30:00'
WHERE cat_id = 'CAT260501007';

INSERT IGNORE INTO followup_task
(application_id, agreement_id, cat_id, user_id, adopter_id, plan_date, actual_date, round_name, task_type,
 status, handler_id, abnormal_flag, deleted, create_by, update_by, create_time, update_time)
SELECT 'APP260626111', ag.id, 'CAT260501004', 'U2026050703', 'U2026050703', '2026-07-03', NULL,
       'DAY_7', 'DAY_7', 'PENDING', 'U2026050702', 0, 0, 'U2026050701', 'U2026050701', '2026-06-26 17:00:00', '2026-06-26 17:00:00'
FROM adoption_agreement ag WHERE ag.application_id = 'APP260626111'
UNION ALL
SELECT 'APP260626111', ag.id, 'CAT260501004', 'U2026050703', 'U2026050703', '2026-07-26', NULL,
       'DAY_30', 'DAY_30', 'PENDING', 'U2026050702', 0, 0, 'U2026050701', 'U2026050701', '2026-06-26 17:00:00', '2026-06-26 17:00:00'
FROM adoption_agreement ag WHERE ag.application_id = 'APP260626111'
UNION ALL
SELECT 'APP260626111', ag.id, 'CAT260501004', 'U2026050703', 'U2026050703', '2026-09-24', NULL,
       'DAY_90', 'DAY_90', 'PENDING', 'U2026050702', 0, 0, 'U2026050701', 'U2026050701', '2026-06-26 17:00:00', '2026-06-26 17:00:00'
FROM adoption_agreement ag WHERE ag.application_id = 'APP260626111';

-- Stage 6 demo follow-up feedback, overdue tasks and warnings.
UPDATE followup_task
SET status = 'COMPLETED', actual_date = '2026-07-03', update_time = '2026-07-03 20:00:00'
WHERE application_id = 'APP260626111' AND task_type = 'DAY_7';

UPDATE followup_task
SET status = 'COMPLETED', actual_date = '2026-07-26', update_time = '2026-07-26 20:00:00'
WHERE application_id = 'APP260626111' AND task_type = 'DAY_30';

UPDATE followup_task
SET status = 'ABNORMAL', actual_date = '2026-09-24', abnormal_flag = 1, update_time = '2026-09-24 20:00:00'
WHERE application_id = 'APP260626111' AND task_type = 'DAY_90';

INSERT IGNORE INTO followup_record
(task_id, application_id, agreement_id, cat_id, user_id, adopter_id, content, cat_condition,
 environment_description, environment_desc, photo_url, abnormal_flag, abnormal_description, abnormal_desc,
 volunteer_comment, submit_time, status, deleted, create_by, update_by, create_time, update_time)
SELECT ft.id, ft.application_id, ft.agreement_id, ft.cat_id, ft.user_id, COALESCE(ft.adopter_id, ft.user_id),
       '第 7 天回访：猫咪吃喝正常，已适应新环境。', '精神状态良好，食欲正常。',
       '门窗已封好，有猫砂盆和饮水点。', '门窗已封好，有猫砂盆和饮水点。',
       '/uploads/cats/cat_04_03.jpg', 0, NULL, NULL, NULL,
       '2026-07-03 20:00:00', 'SUBMITTED', 0, 'U2026050703', 'U2026050703', '2026-07-03 20:00:00', '2026-07-03 20:00:00'
FROM followup_task ft WHERE ft.application_id = 'APP260626111' AND ft.task_type = 'DAY_7'
UNION ALL
SELECT ft.id, ft.application_id, ft.agreement_id, ft.cat_id, ft.user_id, COALESCE(ft.adopter_id, ft.user_id),
       '第 90 天回访：猫咪最近食欲下降，需要志愿者协助判断。', '食欲下降，活动减少。',
       '居住环境正常，饮水和猫砂正常。', '居住环境正常，饮水和猫砂正常。',
       '/uploads/cats/cat_04_01.jpg', 1, '连续两天食欲下降，担心健康异常。', '连续两天食欲下降，担心健康异常。',
       '志愿者已建议尽快就医复查。',
       '2026-09-24 20:00:00', 'SUBMITTED', 0, 'U2026050703', 'U2026050702', '2026-09-24 20:00:00', '2026-09-24 20:00:00'
FROM followup_task ft WHERE ft.application_id = 'APP260626111' AND ft.task_type = 'DAY_90';

DELETE fr FROM followup_record fr
JOIN followup_task ft ON ft.id = fr.task_id
WHERE ft.application_id = 'APP260626110';

DELETE FROM followup_task
WHERE application_id = 'APP260626110';

INSERT IGNORE INTO followup_task
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

INSERT IGNORE INTO followup_record
(task_id, application_id, agreement_id, cat_id, user_id, adopter_id, content, cat_condition,
 environment_description, environment_desc, photo_url, abnormal_flag, abnormal_description, abnormal_desc,
 volunteer_comment, submit_time, status, deleted, create_by, update_by, create_time, update_time)
SELECT ft.id, ft.application_id, ft.agreement_id, ft.cat_id, ft.user_id, COALESCE(ft.adopter_id, ft.user_id),
       '志愿者认养第 7 天回访：八嘎已适应新家，会主动进食和使用猫砂盆。', '精神稳定，食欲正常，排便正常。',
       '阳台和窗户已安装防护网，饮水、猫砂盆和躲藏空间齐全。', '阳台和窗户已安装防护网，饮水、猫砂盆和躲藏空间齐全。',
       '/uploads/cats/cat_07_03.jpg', 0, NULL, NULL, '系统自动生成任务，认养人按时提交。',
       '2026-07-07 20:10:00', 'SUBMITTED', 0, 'UDEMOVOL', 'UDEMOVOL', '2026-07-07 20:10:00', '2026-07-07 20:10:00'
FROM followup_task ft WHERE ft.application_id = 'APP260630201' AND ft.task_type = 'DAY_7';

INSERT IGNORE INTO adoption_agreement
(agreement_no, application_id, cat_id, user_id, adopter_id, content, agreement_content, status, generated_time,
 handover_time, handover_place, handover_location, handover_person, handover_user_id,
 adopter_confirmed, volunteer_confirmed, remark, deleted, create_by, update_by, create_time, update_time)
VALUES
('AGR202606260003', 'APP260626108', 'CAT260501013', 'U2026050703', 'U2026050703',
 '演示协议：逾期回访任务使用。', '演示协议：逾期回访任务使用。',
 'HANDED_OVER', '2026-06-26 16:05:00', '2026-06-26 17:05:00', '翡翠湖校区志愿者服务点', '翡翠湖校区志愿者服务点',
 'U2026050702', 'U2026050702', 1, 1, '逾期刷新演示记录', 0,
 'U2026050701', 'U2026050701', '2026-06-26 16:05:00', '2026-06-26 17:05:00');

INSERT IGNORE INTO followup_task
(application_id, agreement_id, cat_id, user_id, adopter_id, plan_date, actual_date, round_name, task_type,
 status, handler_id, abnormal_flag, deleted, create_by, update_by, create_time, update_time)
SELECT 'APP260626108', ag.id, 'CAT260501013', 'U2026050703', 'U2026050703', '2026-06-20', NULL,
       'DAY_7', 'DAY_7', 'OVERDUE', NULL, 0, 0, 'U2026050701', 'U2026050701', '2026-06-26 17:05:00', '2026-06-27 09:00:00'
FROM adoption_agreement ag WHERE ag.application_id = 'APP260626108'
UNION ALL
SELECT 'APP260626108', ag.id, 'CAT260501013', 'U2026050703', 'U2026050703', '2026-07-26', NULL,
       'DAY_30', 'DAY_30', 'PENDING', NULL, 0, 0, 'U2026050701', 'U2026050701', '2026-06-26 17:05:00', '2026-06-26 17:05:00'
FROM adoption_agreement ag WHERE ag.application_id = 'APP260626108'
UNION ALL
SELECT 'APP260626108', ag.id, 'CAT260501013', 'U2026050703', 'U2026050703', '2026-09-24', NULL,
       'DAY_90', 'DAY_90', 'PENDING', NULL, 0, 0, 'U2026050701', 'U2026050701', '2026-06-26 17:05:00', '2026-06-26 17:05:00'
FROM adoption_agreement ag WHERE ag.application_id = 'APP260626108';

INSERT IGNORE INTO warning_record
(warning_type, warning_level, related_type, related_id, biz_type, biz_id, cat_id, user_id,
 application_id, task_id, title, description, content, handle_status, handler_id, handle_opinion,
 handle_comment, handled_time, handle_time, status, deleted, create_by, update_by, create_time, update_time)
SELECT 'FOLLOWUP_ABNORMAL', 'HIGH', 'FOLLOWUP_TASK', ft.id, 'FOLLOWUP_TASK', ft.id, ft.cat_id, ft.user_id,
       ft.application_id, ft.id, '回访异常：阴阳师', '90 天回访反馈异常，需管理员跟进。', '90 天回访反馈异常，需管理员跟进。',
       'PENDING', NULL, NULL, NULL, NULL, NULL, 'VALID', 0, 'U2026050703', 'U2026050703', '2026-09-24 20:05:00', '2026-09-24 20:05:00'
FROM followup_task ft WHERE ft.application_id = 'APP260626111' AND ft.task_type = 'DAY_90'
UNION ALL
SELECT 'FOLLOWUP_OVERDUE', 'MEDIUM', 'FOLLOWUP_TASK', ft.id, 'FOLLOWUP_TASK', ft.id, ft.cat_id, ft.user_id,
       ft.application_id, ft.id, '回访逾期：奶盖', '第 7 天回访已逾期。', '第 7 天回访已逾期。',
       'HANDLED', 'U2026050701', '已电话联系认养人，约定补交回访。', '已电话联系认养人，约定补交回访。',
       '2026-06-27 10:00:00', '2026-06-27 10:00:00', 'VALID', 0, 'U2026050701', 'U2026050701', '2026-06-27 09:00:00', '2026-06-27 10:00:00'
FROM followup_task ft WHERE ft.application_id = 'APP260626108' AND ft.task_type = 'DAY_7';

-- Stage 7 demo system management data.
INSERT IGNORE INTO t_notice
(notice_id, title, content, publisher, pinned, enabled, published_at, notice_type, publish_status,
 publisher_id, publish_time, sort_order, deleted, create_time, update_time)
VALUES
('NT260626701', 'Adoption open day notice', 'Campus adoption open day will be held this Friday afternoon.', 'System Admin', 1, 1,
 '2026-06-26 09:00:00', 'ADOPTION', 'PUBLISHED', 'U2026050701', '2026-06-26 09:00:00', 30, 0,
 '2026-06-26 08:30:00', '2026-06-26 09:00:00'),
('NT260626702', 'Follow-up photo reminder', 'Please submit clear environment and cat photos for follow-up tasks.', 'System Admin', 0, 1,
 '2026-06-26 10:00:00', 'FOLLOWUP', 'PUBLISHED', 'U2026050701', '2026-06-26 10:00:00', 20, 0,
 '2026-06-26 09:30:00', '2026-06-26 10:00:00'),
('NT260626703', 'Draft medical cooperation notice', 'Draft notice for partner hospital duty schedule.', 'System Admin', 0, 0,
 NULL, 'SYSTEM', 'DRAFT', 'U2026050701', NULL, 10, 0,
 '2026-06-26 11:00:00', '2026-06-26 11:00:00');

INSERT INTO system_message
(user_id, receiver_id, message_type, title, content, related_type, related_id, biz_type, biz_id,
 read_flag, read_status, status, deleted, create_by, update_by, create_time, update_time)
VALUES
('U2026050703', 'U2026050703', 'APPLICATION', '认养申请进入交接', '你的认养申请 APP260626108 已通过终审，请等待协议交接安排。', 'APPLICATION', 'APP260626108', 'APPLICATION', 'APP260626108',
 0, 'UNREAD', 'VALID', 0, 'U2026050701', 'U2026050701', '2026-06-26 16:05:00', '2026-06-26 16:05:00'),
('U2026050703', 'U2026050703', 'AGREEMENT', '认养协议已生成', '认养协议 AGR202606260002 已生成，请按通知完成线下交接。', 'AGREEMENT', 'AGR202606260002', 'AGREEMENT', 'AGR202606260002',
 1, 'READ', 'VALID', 0, 'U2026050701', 'U2026050701', '2026-06-26 16:35:00', '2026-06-26 17:00:00'),
('U2026050703', 'U2026050703', 'FOLLOWUP', '回访任务已生成', '交接完成后，系统已自动生成 7/30/90 天回访任务，请按时提交反馈。', 'FOLLOWUP_TASK', 'APP260626111', 'FOLLOWUP_TASK', 'APP260626111',
 0, 'UNREAD', 'VALID', 0, 'SYSTEM', 'SYSTEM', '2026-06-26 17:00:00', '2026-06-26 17:00:00'),
('U2026050702', 'U2026050702', 'WARNING', '新的回访异常预警', '有一条回访异常预警需要志愿者关注。', 'WARNING', 'FOLLOWUP_ABNORMAL', 'WARNING', 'FOLLOWUP_ABNORMAL',
 0, 'UNREAD', 'VALID', 0, 'SYSTEM', 'SYSTEM', '2026-09-24 20:05:00', '2026-09-24 20:05:00'),
('U2026050701', 'U2026050701', 'NOTICE', '后台数据已更新', '线索、申请、协议、回访和预警业务数据已同步更新。', 'NOTICE', 'NT260626701', 'NOTICE', 'NT260626701',
 0, 'UNREAD', 'VALID', 0, 'SYSTEM', 'SYSTEM', '2026-06-26 18:00:00', '2026-06-26 18:00:00');

INSERT IGNORE INTO dict_item
(dict_type, item_code, item_label, item_value, dict_label, dict_value, sort_order, enabled, remark,
 status, deleted, create_by, update_by, create_time, update_time)
VALUES
('CAT_STATUS', 'ADOPTABLE', 'Adoptable', 'ADOPTABLE', 'Adoptable', 'ADOPTABLE', 10, 1, 'Cat can be published for adoption', 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('CAT_STATUS', 'FOLLOWING', 'Following', 'FOLLOWING', 'Following', 'FOLLOWING', 20, 1, 'Cat is in follow-up period', 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('CAT_STATUS', 'SUSPENDED', 'Suspended', 'SUSPENDED', 'Suspended', 'SUSPENDED', 30, 1, 'Adoption temporarily suspended', 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('CLUE_STATUS', 'PENDING_VERIFY', 'Pending verify', 'PENDING_VERIFY', 'Pending verify', 'PENDING_VERIFY', 10, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('CLUE_STATUS', 'CREATED_CAT', 'Created cat', 'CREATED_CAT', 'Created cat', 'CREATED_CAT', 20, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('APPLICATION_STATUS', 'PENDING_INITIAL', 'Pending initial audit', 'PENDING_INITIAL', 'Pending initial audit', 'PENDING_INITIAL', 10, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('APPLICATION_STATUS', 'PENDING_FINAL', 'Pending final audit', 'PENDING_FINAL', 'Pending final audit', 'PENDING_FINAL', 20, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('APPLICATION_STATUS', 'PENDING_HANDOVER', 'Pending handover', 'PENDING_HANDOVER', 'Pending handover', 'PENDING_HANDOVER', 30, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('FOLLOWUP_STATUS', 'PENDING', 'Pending', 'PENDING', 'Pending', 'PENDING', 10, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('FOLLOWUP_STATUS', 'COMPLETED', 'Completed', 'COMPLETED', 'Completed', 'COMPLETED', 20, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('FOLLOWUP_STATUS', 'OVERDUE', 'Overdue', 'OVERDUE', 'Overdue', 'OVERDUE', 30, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('WARNING_TYPE', 'FOLLOWUP_OVERDUE', 'Follow-up overdue', 'FOLLOWUP_OVERDUE', 'Follow-up overdue', 'FOLLOWUP_OVERDUE', 10, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('WARNING_TYPE', 'FOLLOWUP_ABNORMAL', 'Follow-up abnormal', 'FOLLOWUP_ABNORMAL', 'Follow-up abnormal', 'FOLLOWUP_ABNORMAL', 20, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('NOTICE_TYPE', 'SYSTEM', 'System notice', 'SYSTEM', 'System notice', 'SYSTEM', 10, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('NOTICE_TYPE', 'ADOPTION', 'Adoption notice', 'ADOPTION', 'Adoption notice', 'ADOPTION', 20, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('NOTICE_STATUS', 'DRAFT', 'Draft', 'DRAFT', 'Draft', 'DRAFT', 10, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('NOTICE_STATUS', 'PUBLISHED', 'Published', 'PUBLISHED', 'Published', 'PUBLISHED', 20, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('READ_STATUS', 'UNREAD', 'Unread', 'UNREAD', 'Unread', 'UNREAD', 10, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('READ_STATUS', 'READ', 'Read', 'READ', 'Read', 'READ', 20, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('ROLE_CODE', 'STUDENT', 'Student', 'STUDENT', 'Student', 'STUDENT', 10, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('ROLE_CODE', 'VOLUNTEER', 'Volunteer', 'VOLUNTEER', 'Volunteer', 'VOLUNTEER', 20, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW()),
('ROLE_CODE', 'ADMIN', 'Admin', 'ADMIN', 'Admin', 'ADMIN', 30, 1, NULL, 'VALID', 0, 'U2026050701', 'U2026050701', NOW(), NOW());

INSERT INTO operation_log
(operator_id, operator_name, operation_type, target_type, target_id, request_ip, before_data, after_data,
 remark, status, deleted, create_by, update_by, create_time, update_time)
VALUES
('U2026050702', '猫咪志愿者小组', '核实线索', 'CLUE', 'RP260626006', '127.0.0.1', 'PENDING_VERIFY', 'VERIFIED_VALID', '业务流程演示记录', 'SUCCESS', 0, 'U2026050702', 'U2026050702', '2026-06-25 18:00:00', '2026-06-25 18:00:00'),
('U2026050702', '猫咪志愿者小组', '线索转猫咪档案', 'CAT', 'CAT260501004', '127.0.0.1', NULL, 'CREATED_CAT', '业务流程演示记录', 'SUCCESS', 0, 'U2026050702', 'U2026050702', '2026-06-25 18:05:00', '2026-06-25 18:05:00'),
('U2026050704', '合肥合作动物医院', '新增医疗记录', 'MEDICAL', 'MD260626004', '127.0.0.1', NULL, 'TREATMENT', '业务流程演示记录', 'SUCCESS', 0, 'U2026050704', 'U2026050704', '2026-06-23 16:00:00', '2026-06-23 16:00:00'),
('U2026050701', '系统管理员', '认养申请终审', 'APPLICATION', 'APP260626108', '127.0.0.1', 'PENDING_FINAL', 'PENDING_HANDOVER', '业务流程演示记录', 'SUCCESS', 0, 'U2026050701', 'U2026050701', '2026-06-26 16:00:00', '2026-06-26 16:00:00'),
('U2026050701', '系统管理员', '生成认养协议', 'AGREEMENT', 'AGR202606260002', '127.0.0.1', NULL, 'GENERATED', '业务流程演示记录', 'SUCCESS', 0, 'U2026050701', 'U2026050701', '2026-06-26 16:35:00', '2026-06-26 16:35:00'),
('U2026050701', '系统管理员', '完成协议交接', 'AGREEMENT', 'AGR202606260002', '127.0.0.1', 'GENERATED', 'HANDED_OVER', '业务流程演示记录', 'SUCCESS', 0, 'U2026050701', 'U2026050701', '2026-06-26 17:00:00', '2026-06-26 17:00:00'),
('U2026050703', '李同学', '提交回访记录', 'FOLLOWUP_TASK', 'APP260626111', '127.0.0.1', 'PENDING', 'COMPLETED', '业务流程演示记录', 'SUCCESS', 0, 'U2026050703', 'U2026050703', '2026-07-03 20:00:00', '2026-07-03 20:00:00'),
('U2026050701', '系统管理员', '处理异常预警', 'WARNING', 'FOLLOWUP_OVERDUE', '127.0.0.1', 'PENDING', 'HANDLED', '业务流程演示记录', 'SUCCESS', 0, 'U2026050701', 'U2026050701', '2026-06-27 10:00:00', '2026-06-27 10:00:00');

-- Stage 8 demo completion data: extra warnings, messages and audit logs.
INSERT INTO warning_record
(warning_type, warning_level, related_type, related_id, biz_type, biz_id, cat_id, user_id,
 application_id, task_id, title, description, content, handle_status, status, deleted,
 create_by, update_by, create_time, update_time)
SELECT 'HEALTH_ABNORMAL', 'HIGH', 'MEDICAL', 'MD260626004', 'MEDICAL', 'MD260626004', 'CAT260501007', NULL,
       NULL, NULL, '医疗异常预警', '医疗记录显示猫咪健康状态异常，需要跟进。',
       '医疗记录显示猫咪健康状态异常，需要跟进。', 'PENDING', 'VALID', 0,
       'SYSTEM', 'SYSTEM', '2026-06-26 18:10:00', '2026-06-26 18:10:00'
WHERE NOT EXISTS (SELECT 1 FROM warning_record WHERE warning_type = 'HEALTH_ABNORMAL' AND related_id = 'MD260626004')
UNION ALL
SELECT 'HIGH_RISK_APPLICATION', 'MEDIUM', 'APPLICATION', 'APP260626102', 'APPLICATION', 'APP260626102', 'CAT260501002', 'U2026050703',
       'APP260626102', NULL, '高风险认养申请预警', '认养申请评分较低，需要管理员重点复核。',
       '认养申请评分较低，需要管理员重点复核。', 'PENDING', 'VALID', 0,
       'SYSTEM', 'SYSTEM', '2026-06-26 18:15:00', '2026-06-26 18:15:00'
WHERE NOT EXISTS (SELECT 1 FROM warning_record WHERE warning_type = 'HIGH_RISK_APPLICATION' AND related_id = 'APP260626102');

INSERT INTO system_message
(user_id, receiver_id, message_type, title, content, related_type, related_id, biz_type, biz_id,
 read_flag, read_status, status, deleted, create_by, update_by, create_time, update_time)
SELECT 'U2026050703', 'U2026050703', 'WARNING', '异常预警已处理', '你的回访异常预警已由管理员处理，请查看处理意见。', 'WARNING', 'FOLLOWUP_OVERDUE', 'WARNING', 'FOLLOWUP_OVERDUE',
       0, 'UNREAD', 'VALID', 0, 'U2026050701', 'U2026050701', '2026-06-27 10:05:00', '2026-06-27 10:05:00'
WHERE NOT EXISTS (SELECT 1 FROM system_message WHERE receiver_id = 'U2026050703' AND title = '异常预警已处理');

INSERT INTO operation_log
(operator_id, operator_name, operation_type, target_type, target_id, request_ip, before_data, after_data,
 remark, status, deleted, create_by, update_by, create_time, update_time)
SELECT 'U2026050701', '系统管理员', '导出猫咪档案', 'EXPORT', 'CATS', '127.0.0.1', NULL, 'cats.csv', '业务流程演示记录', 'SUCCESS', 0, 'U2026050701', 'U2026050701', '2026-06-26 18:20:00', '2026-06-26 18:20:00'
WHERE NOT EXISTS (SELECT 1 FROM operation_log WHERE operation_type = '导出猫咪档案' AND target_id = 'CATS')
UNION ALL
SELECT 'U2026050701', '系统管理员', '导出认养申请', 'EXPORT', 'APPLICATIONS', '127.0.0.1', NULL, 'applications.csv', '业务流程演示记录', 'SUCCESS', 0, 'U2026050701', 'U2026050701', '2026-06-26 18:21:00', '2026-06-26 18:21:00'
WHERE NOT EXISTS (SELECT 1 FROM operation_log WHERE operation_type = '导出认养申请' AND target_id = 'APPLICATIONS')
UNION ALL
SELECT 'U2026050701', '系统管理员', '导出回访任务', 'EXPORT', 'FOLLOWUPS', '127.0.0.1', NULL, 'followups.csv', '业务流程演示记录', 'SUCCESS', 0, 'U2026050701', 'U2026050701', '2026-06-26 18:22:00', '2026-06-26 18:22:00'
WHERE NOT EXISTS (SELECT 1 FROM operation_log WHERE operation_type = '导出回访任务' AND target_id = 'FOLLOWUPS')
UNION ALL
SELECT 'U2026050701', '系统管理员', '导出异常预警', 'EXPORT', 'WARNINGS', '127.0.0.1', NULL, 'warnings.csv', '业务流程演示记录', 'SUCCESS', 0, 'U2026050701', 'U2026050701', '2026-06-26 18:23:00', '2026-06-26 18:23:00'
WHERE NOT EXISTS (SELECT 1 FROM operation_log WHERE operation_type = '导出异常预警' AND target_id = 'WARNINGS')
UNION ALL
SELECT 'U2026050702', '猫咪志愿者小组', '全局搜索', 'SEARCH', 'CAT260501004', '127.0.0.1', NULL, 'cats/clues/applications', '业务流程演示记录', 'SUCCESS', 0, 'U2026050702', 'U2026050702', '2026-06-26 18:24:00', '2026-06-26 18:24:00'
WHERE NOT EXISTS (SELECT 1 FROM operation_log WHERE operation_type = '全局搜索' AND target_id = 'CAT260501004')
UNION ALL
SELECT 'U2026050701', '系统管理员', '发布公告', 'NOTICE', 'NT260626701', '127.0.0.1', 'DRAFT', 'PUBLISHED', '业务流程演示记录', 'SUCCESS', 0, 'U2026050701', 'U2026050701', '2026-06-26 18:25:00', '2026-06-26 18:25:00'
WHERE NOT EXISTS (SELECT 1 FROM operation_log WHERE operation_type = '发布公告' AND target_id = 'NT260626701')
UNION ALL
SELECT 'U2026050701', '系统管理员', '启停字典项', 'DICT', 'CAT_STATUS', '127.0.0.1', 'disabled', 'enabled', '业务流程演示记录', 'SUCCESS', 0, 'U2026050701', 'U2026050701', '2026-06-26 18:26:00', '2026-06-26 18:26:00'
WHERE NOT EXISTS (SELECT 1 FROM operation_log WHERE operation_type = '启停字典项' AND target_id = 'CAT_STATUS');

