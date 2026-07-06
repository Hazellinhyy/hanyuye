INSERT IGNORE INTO t_user (user_id, user_name, school_no, phone, id_card, college, pet_experience, role_code, status, created_at) VALUES
('U2026050701', '周老师', 'A2026001', '13855107001', '340111198608150015', '信息化建设与管理办公室', '负责平台账号、权限、数据备份和业务流程配置。', 'ADMIN', 1, '2026-05-07 09:00:00'),
('U2026050702', '沈同学', '2022210002', '13955107002', '340111200103120021', '校园流浪猫志愿服务队', '负责线索核实、救助建档、认养审核协助和回访跟进。', 'VOLUNTEER', 1, '2026-05-07 09:10:00'),
('U2026050703', '李同学', '2024210001', '13755107003', '340111200201010037', '计算机与信息学院', '照顾过家中猫咪三年，了解疫苗、驱虫、绝育和封窗要求。', 'STUDENT', 1, '2026-05-07 09:20:00'),
('U2026050704', '刘医生', 'H2026001', '13655107004', '340111198011220041', '合肥合作动物医院', '负责健康检查、疫苗、绝育和治疗记录反馈。', 'HOSPITAL', 1, '2026-05-07 09:30:00'),
('UDEMOADMIN', '吴老师', 'A2026002', '13855107005', '340111198902180055', '信息化建设与管理办公室', '负责平台维护、数据备份与权限配置。', 'ADMIN', 1, '2026-05-07 09:40:00'),
('UDEMOVOL', '周同学', '2022210006', '13955107006', '340111199905210066', '校园流浪猫志愿服务队', '负责上报核实、救助建档、审核协助和回访。', 'VOLUNTEER', 1, '2026-05-07 09:50:00'),
('UDEMOHOSP', '黄医生', 'H2026002', '13655107007', '340111199206180101', '校内合作医院', '负责医疗记录录入、体检疫苗绝育和健康信息维护。', 'HOSPITAL', 1, '2026-05-07 10:00:00'),
('UDEMOUSER', '赵同学', '2024210008', '13755107008', '340111200203090085', '计算机与信息学院', '有过照顾校园流浪猫的经历，愿意按平台要求提交线索、申请认养并配合回访。', 'STUDENT', 1, '2026-05-07 10:10:00');

UPDATE t_user
SET school_no = 'A2026002', role_code = 'ADMIN', status = 1, password_hash = ''
WHERE user_id = 'UDEMOADMIN';

UPDATE t_user
SET school_no = '2022210006', role_code = 'VOLUNTEER', status = 1, password_hash = ''
WHERE user_id = 'UDEMOVOL';

UPDATE t_user
SET school_no = 'H2026002', role_code = 'HOSPITAL', status = 1, password_hash = ''
WHERE user_id = 'UDEMOHOSP';

UPDATE t_user
SET school_no = '2024210008', role_code = 'STUDENT', status = 1, password_hash = ''
WHERE user_id = 'UDEMOUSER';

INSERT IGNORE INTO t_cat
(cat_id, cat_name, found_place, found_date, gender, color, age_estimate, personality, health_level, sterilized, vaccinated, cat_status, cover_url, tags, description, created_at, updated_at)
VALUES
('CAT260501001', '蛋黄', '德园食堂旁', '2026-05-01', 'M', '橘白', '一岁半左右', '亲人、活泼、喜欢逗猫棒。', 'A', 1, 1, 'ADOPTABLE', '/uploads/cats/cat_01_01.jpg', '', '适合有稳定住所、愿意长期回访的认养人。', '2026-05-01 10:00:00', '2026-05-08 12:30:00'),
('CAT260501002', '小寒', '德园食堂旁', '2026-05-01', 'F', '彩狸三花', '一岁半左右', '聪明、亲人、爱干净。', 'A', 1, 1, 'ADOPTABLE', '/uploads/cats/cat_02_01.jpg', '', '适合单猫或多猫家庭，需要按期回访。', '2026-05-01 10:05:00', '2026-05-08 12:30:00'),
('CAT260501003', '口水鸡', '德园食堂旁', '2026-05-01', 'F', '奶牛', '一岁半左右', '非常亲人，喜欢互动。', 'A', 1, 1, 'ADOPTABLE', '/uploads/cats/cat_03_01.jpg', '', '适合能提供陪伴和安全室内环境的认养人。', '2026-05-01 10:10:00', '2026-05-08 12:30:00'),
('CAT260501004', '阴阳师', '五号楼', '2026-05-01', 'F', '黑白', '两岁半左右', '话痨、贴人、喜欢蹭腿。', 'A', 1, 1, 'ADOPTED', '/uploads/cats/cat_04_01.jpg', '', '已完成认养交接，进入定期回访阶段。', '2026-05-01 10:15:00', '2026-05-09 13:00:00'),
('CAT260501005', '棉花糖', '二号北楼', '2026-05-01', 'F', '白色', '两岁半左右', '温顺、独立，蓝眼睛但不聋。', 'A', 1, 1, 'ADOPTABLE', '/uploads/cats/cat_05_01.jpg', '', '适合安静、有耐心的家庭。', '2026-05-01 10:20:00', '2026-05-08 12:30:00'),
('CAT260501006', '沃柑', '管理学院大楼', '2026-05-01', 'M', '橘白', '一岁多', '活泼、爱玩、亲人。', 'A', 1, 1, 'OBSERVING', '/uploads/cats/cat_06_01.jpg', '', '正在补充健康档案和行为观察记录。', '2026-05-01 10:25:00', '2026-05-08 12:30:00'),
('CAT260501007', '八嘎', '七号楼', '2026-05-01', 'M', '奶牛', '四岁左右', '情绪稳定、温顺亲人。', 'A', 1, 1, 'ADOPTABLE', '/uploads/cats/cat_07_01.jpg', '', '适合第一次养猫但愿意学习照护知识的认养人。', '2026-05-01 10:30:00', '2026-05-08 12:30:00'),
('CAT260501008', '杜甫', '七号楼', '2026-05-01', 'M', '橘猫', '两岁左右', '安静、慵懒、喜欢晒太阳。', 'A', 1, 1, 'MEDICAL', '/uploads/cats/cat_08_01.jpg', '', '轻微皮肤问题复查中，暂缓发布认养。', '2026-05-01 10:35:00', '2026-05-08 12:30:00');

INSERT IGNORE INTO t_cat_photo
(photo_id, cat_id, photo_url, angle_code, photo_scene, is_cover, recognition_weight, feature_note, uploaded_at)
VALUES
('PH260501001', 'CAT260501001', '/uploads/cats/cat_01_01.jpg', 'FRONT', '正脸', 1, 1.00, '橘白脸部、鼻梁白线明显。', '2026-05-01 10:00:00'),
('PH260501002', 'CAT260501001', '/uploads/cats/cat_01_02.jpg', 'LEFT', '左侧脸', 0, 0.92, '左脸橘白分布用于识别。', '2026-05-01 10:01:00'),
('PH260501003', 'CAT260501001', '/uploads/cats/cat_01_03.jpg', 'FULL_BODY', '全身照', 0, 0.95, '全身橘白比例和尾巴花纹。', '2026-05-01 10:02:00'),
('PH260501004', 'CAT260501002', '/uploads/cats/cat_02_01.jpg', 'FRONT', '正脸', 1, 1.00, '狸花额头纹路清晰。', '2026-05-01 10:05:00'),
('PH260501005', 'CAT260501002', '/uploads/cats/cat_02_02.jpg', 'FULL_BODY', '全身照', 0, 0.95, '背部狸花纹路完整。', '2026-05-01 10:06:00'),
('PH260501006', 'CAT260501003', '/uploads/cats/cat_03_01.jpg', 'FRONT', '正脸', 1, 1.00, '三花脸部色块明显。', '2026-05-01 10:10:00'),
('PH260501007', 'CAT260501003', '/uploads/cats/cat_03_02.jpg', 'LEFT', '左侧脸', 0, 0.91, '左侧橘黑白分区。', '2026-05-01 10:11:00'),
('PH260501008', 'CAT260501003', '/uploads/cats/cat_03_03.jpg', 'FULL_BODY', '全身照', 0, 0.95, '全身三花色块。', '2026-05-01 10:12:00'),
('PH260501009', 'CAT260501004', '/uploads/cats/cat_04_01.jpg', 'FRONT', '正脸', 1, 1.00, '黑白分脸特征。', '2026-05-01 10:15:00'),
('PH260501010', 'CAT260501004', '/uploads/cats/cat_04_02.jpg', 'RIGHT', '右侧脸', 0, 0.90, '右脸黑白边界。', '2026-05-01 10:16:00'),
('PH260501011', 'CAT260501004', '/uploads/cats/cat_04_03.jpg', 'FULL_BODY', '全身照', 0, 0.94, '奶牛猫全身花纹。', '2026-05-01 10:17:00'),
('PH260501012', 'CAT260501005', '/uploads/cats/cat_05_01.jpg', 'FRONT', '正脸', 1, 1.00, '白猫蓝眼睛。', '2026-05-01 10:20:00'),
('PH260501013', 'CAT260501005', '/uploads/cats/cat_05_02.jpg', 'LEFT', '左侧脸', 0, 0.89, '左侧头部轮廓。', '2026-05-01 10:21:00'),
('PH260501014', 'CAT260501005', '/uploads/cats/cat_05_03.jpg', 'FULL_BODY', '全身照', 0, 0.93, '白色全身识别样本。', '2026-05-01 10:22:00'),
('PH260501015', 'CAT260501006', '/uploads/cats/cat_06_01.jpg', 'FRONT', '正脸', 1, 1.00, '橘白面部。', '2026-05-01 10:25:00'),
('PH260501016', 'CAT260501006', '/uploads/cats/cat_06_02.jpg', 'LEFT', '左侧脸', 0, 0.91, '左脸橘白边界。', '2026-05-01 10:26:00'),
('PH260501017', 'CAT260501006', '/uploads/cats/cat_06_03.jpg', 'FULL_BODY', '全身照', 0, 0.95, '背部与尾巴纹路。', '2026-05-01 10:27:00'),
('PH260501018', 'CAT260501007', '/uploads/cats/cat_07_01.jpg', 'FRONT', '正脸', 1, 1.00, '奶牛猫脸部黑白斑。', '2026-05-01 10:30:00'),
('PH260501019', 'CAT260501007', '/uploads/cats/cat_07_02.jpg', 'RIGHT', '右侧脸', 0, 0.91, '右侧脸斑纹。', '2026-05-01 10:31:00'),
('PH260501020', 'CAT260501007', '/uploads/cats/cat_07_03.jpg', 'FULL_BODY', '全身照', 0, 0.94, '全身奶牛花纹。', '2026-05-01 10:32:00'),
('PH260501021', 'CAT260501008', '/uploads/cats/cat_08_01.jpg', 'FRONT', '正脸', 1, 1.00, '橘猫脸部轮廓。', '2026-05-01 10:35:00'),
('PH260501022', 'CAT260501008', '/uploads/cats/cat_08_02.jpg', 'LEFT', '左侧脸', 0, 0.90, '左侧橘色纹路。', '2026-05-01 10:36:00'),
('PH260501023', 'CAT260501008', '/uploads/cats/cat_08_03.jpg', 'FULL_BODY', '全身照', 0, 0.95, '橘猫全身样本。', '2026-05-01 10:37:00');

INSERT IGNORE INTO t_rescue_report
(report_id, reporter_name, reporter_phone, found_place, color, gender, health_description, urgent, photo_url, report_status, reported_at)
VALUES
('RP260508001', '王同学', '13700000000', '翡翠湖校区二食堂东侧', '橘白', 'U', '精神尚可，右后腿疑似受伤，需要志愿者核实。', 1, '/uploads/cats/no-photo.svg', '待紧急核实', '2026-05-08 15:00:00');

INSERT IGNORE INTO t_medical_record
(medical_id, cat_id, check_date, hospital, health_level, vaccinated, sterilized, treatment, doctor_note, created_at)
VALUES
('MD260508001', 'CAT260501008', '2026-05-08', '合肥合作动物医院', 'C', 1, 1, '皮肤镜检查、驱虫、外用药。', '建议七天后复查，暂缓认养。', '2026-05-08 16:00:00');

INSERT IGNORE INTO t_application
(application_id, user_id, cat_id, housing_info, family_attitude, pet_experience, economic_ability, promise_accepted, apply_status, review_note, interview_note, agreement_no, applied_at, reviewed_at, handed_over_at)
VALUES
('APP260509001', 'U2026050703', 'CAT260501004', '校外稳定租住，已封窗。', '家人和室友均同意。', '曾照顾家中猫咪三年。', '可承担猫粮、猫砂、疫苗和医疗费用。', 1, 'HANDED_OVER', '材料完整，通过认养。', '确认长期照护与寒暑假安排。', 'AGR-APP260509001', '2026-05-09 09:00:00', '2026-05-09 11:00:00', '2026-05-09 13:00:00');

INSERT IGNORE INTO t_followup
(followup_id, application_id, followup_time, method, cat_condition, environment_description, result_level, photo_url, suggestion, operator_name)
VALUES
('RF260520001', 'APP260509001', '2026-05-20 20:00:00', '线上', '精神和食欲正常，已适应新环境。', '门窗防护到位，猫砂盆和饮水位置合理。', 'NORMAL', '/uploads/cats/cat_04_03.jpg', '继续按月回访。', '猫咪志愿者小组');

INSERT INTO system_message
(user_id, receiver_id, message_type, title, content, related_type, related_id, biz_type, biz_id,
 read_flag, read_status, status, deleted, create_by, update_by, create_time, update_time)
SELECT u.user_id, u.user_id, 'SYSTEM', '真实猫咪档案已导入',
       '系统已导入校园流浪猫档案、多角度照片、医疗和回访演示数据。',
       'SYSTEM', 'MSG_NOTICE_ARCHIVE_IMPORTED', 'SYSTEM', 'MSG_NOTICE_ARCHIVE_IMPORTED',
       0, 'UNREAD', 'VALID', 0, 'SYSTEM', 'SYSTEM', '2026-05-08 12:35:00', '2026-05-08 12:35:00'
FROM t_user u
WHERE u.status = 1
  AND NOT EXISTS (
      SELECT 1 FROM system_message sm
      WHERE COALESCE(sm.deleted, 0) = 0
        AND sm.receiver_id = u.user_id
        AND sm.title = '真实猫咪档案已导入'
  );

INSERT INTO system_message
(user_id, receiver_id, message_type, title, content, related_type, related_id, biz_type, biz_id,
 read_flag, read_status, status, deleted, create_by, update_by, create_time, update_time)
SELECT u.user_id, u.user_id, 'SYSTEM', '认养前请确认长期照护条件',
       '申请人需如实填写住房、家庭或室友态度、经济能力、寒暑假安排，并同意后续回访。',
       'SYSTEM', 'MSG_LONG_TERM_CARE_CONDITION', 'SYSTEM', 'MSG_LONG_TERM_CARE_CONDITION',
       0, 'UNREAD', 'VALID', 0, 'SYSTEM', 'SYSTEM', '2026-05-08 12:36:00', '2026-05-08 12:36:00'
FROM t_user u
WHERE u.status = 1
  AND NOT EXISTS (
      SELECT 1 FROM system_message sm
      WHERE COALESCE(sm.deleted, 0) = 0
        AND sm.receiver_id = u.user_id
        AND sm.title = '认养前请确认长期照护条件'
  );

INSERT IGNORE INTO t_product (product_id, product_name, category, price, image_url, description, pay_url, status, created_at) VALUES
('PD260508001', '校园猫咪陶瓷杯', '杯子', 29.90, '/uploads/catalog/product-cup.jpg', '三花猫主题陶瓷杯，收益用于校园流浪猫救助。', 'alipay://platformapi/startapp?appId=20000067', 1, '2026-05-08 17:10:00'),
('PD260508002', '校园猫咪抱枕', '抱枕', 39.90, '/uploads/catalog/product-pillow.jpg', '橘猫、三花、奶牛猫主题抱枕。', 'alipay://platformapi/startapp?appId=20000067', 1, '2026-05-08 17:11:00'),
('PD260508003', '猫咪钥匙扣', '钥匙扣', 12.90, '/uploads/catalog/product-keychain.jpg', '图案来自校园真实猫咪。', 'alipay://platformapi/startapp?appId=20000067', 1, '2026-05-08 17:12:00'),
('PD260508004', '校园猫咪卡套', '卡套', 16.80, '/uploads/catalog/product-cardholder.jpg', '校园卡套与挂绳，展示多只猫咪形象。', 'alipay://platformapi/startapp?appId=20000067', 1, '2026-05-08 17:13:00');

INSERT IGNORE INTO t_donation_channel (channel_id, channel_name, qr_url, description, enabled, updated_at) VALUES
('DN260508001', '校园猫咪公益支付宝码', '/uploads/catalog/alipay-qr.jpg', '扫码用于校园猫咪文创购买和爱心捐赠，正式上线时替换为真实公益账户。', 1, '2026-05-08 17:15:00');

UPDATE t_cat
SET color = '彩狸三花',
    tags = '',
    updated_at = CURRENT_TIMESTAMP
WHERE cat_id = 'CAT260501002';

UPDATE t_cat
SET color = '奶牛',
    tags = '',
    updated_at = CURRENT_TIMESTAMP
WHERE cat_id = 'CAT260501003';

UPDATE t_product SET stock = 30 WHERE product_id = 'PD260508001';
UPDATE t_product SET stock = 25 WHERE product_id = 'PD260508002';
UPDATE t_product SET stock = 18 WHERE product_id = 'PD260508003';
UPDATE t_product SET stock = 22 WHERE product_id = 'PD260508004';

INSERT IGNORE INTO t_article (article_id, title, type_name, cover_url, summary, content, source, source_url, tags, hits, praise_count, published, pinned, created_at, updated_at) VALUES
(1, '校园猫咪认养前需要确认的五件事', '认养指南', '/uploads/cats/cat_01_01.jpg', '从住所、预算、假期照护、医疗和回访配合五个角度做认养准备。', '认养不是把猫带回家这一刻结束，而是从这一刻开始。请提前确认门窗防护、同住人态度、长期预算、寒暑假安置和回访配合。', '平台编辑', '', '认养,照护,回访', 12, 3, 1, 1, '2026-05-10 10:00:00', '2026-05-10 10:00:00'),
(2, '猫咪绝育和疫苗记录怎么看', '医疗科普', '/uploads/cats/cat_08_01.jpg', '帮助认养人理解健康等级、疫苗、绝育和复查建议。', '查看医疗记录时，重点关注最近体检时间、健康等级、疫苗和绝育状态。如果出现退养或异常回访，系统会保留完整记录供志愿者追踪。', '合作医院', '', '医疗,疫苗,绝育', 8, 2, 1, 0, '2026-05-11 10:00:00', '2026-05-11 10:00:00');

INSERT IGNORE INTO t_forum_post (post_id, user_id, type_name, title, content, cover_url, hits, praise_count, status, created_at, updated_at) VALUES
(1, 'U2026050703', '照护交流', '第一次接猫回家要准备什么？', '建议提前准备航空箱、猫砂盆、基础猫粮和隔离空间，前几天不要频繁打扰。', '/uploads/cats/cat_02_01.jpg', 5, 1, 'PUBLISHED', '2026-05-12 09:00:00', '2026-05-12 09:00:00');

UPDATE t_product_order o
SET unit_price = COALESCE((SELECT p.price FROM t_product p WHERE p.product_id = o.product_id), o.amount),
    total_amount = o.amount;

INSERT IGNORE INTO cat_tag (cat_id, tag_name)
SELECT cat_id, TRIM(tag_name)
FROM (
    SELECT cat_id, REGEXP_SUBSTR(tags, '[^,]+', 1, 1) AS tag_name FROM t_cat WHERE tags IS NOT NULL
    UNION ALL SELECT cat_id, REGEXP_SUBSTR(tags, '[^,]+', 1, 2) FROM t_cat WHERE tags IS NOT NULL
    UNION ALL SELECT cat_id, REGEXP_SUBSTR(tags, '[^,]+', 1, 3) FROM t_cat WHERE tags IS NOT NULL
    UNION ALL SELECT cat_id, REGEXP_SUBSTR(tags, '[^,]+', 1, 4) FROM t_cat WHERE tags IS NOT NULL
    UNION ALL SELECT cat_id, REGEXP_SUBSTR(tags, '[^,]+', 1, 5) FROM t_cat WHERE tags IS NOT NULL
) parsed_tags
WHERE tag_name IS NOT NULL AND TRIM(tag_name) <> '';

INSERT IGNORE INTO adoption_audit
(application_id, audit_type, audit_result, audit_opinion, audit_time)
SELECT application_id, 'REVIEW', apply_status, review_note, reviewed_at
FROM t_application
WHERE reviewed_at IS NOT NULL;

INSERT IGNORE INTO adoption_agreement
(agreement_no, application_id, agreement_content, status, generated_time, handover_time, create_time, update_time)
SELECT agreement_no, application_id, '', 'HANDED_OVER', reviewed_at, handed_over_at, reviewed_at, reviewed_at
FROM t_application
WHERE agreement_no IS NOT NULL;

INSERT IGNORE INTO article_tag (article_id, tag_name)
SELECT article_id, TRIM(tag_name)
FROM (
    SELECT article_id, REGEXP_SUBSTR(tags, '[^,]+', 1, 1) AS tag_name FROM t_article WHERE tags IS NOT NULL
    UNION ALL SELECT article_id, REGEXP_SUBSTR(tags, '[^,]+', 1, 2) FROM t_article WHERE tags IS NOT NULL
    UNION ALL SELECT article_id, REGEXP_SUBSTR(tags, '[^,]+', 1, 3) FROM t_article WHERE tags IS NOT NULL
    UNION ALL SELECT article_id, REGEXP_SUBSTR(tags, '[^,]+', 1, 4) FROM t_article WHERE tags IS NOT NULL
) parsed_article_tags
WHERE tag_name IS NOT NULL AND TRIM(tag_name) <> '';
