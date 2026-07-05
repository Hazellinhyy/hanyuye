-- Repair demo login accounts in an existing MySQL database.
-- After running this script, all four demo accounts use password 123456.

INSERT IGNORE INTO t_user
(user_id, user_name, school_no, phone, id_card, college, pet_experience, role_code, status, created_at)
VALUES
('UDEMOADMIN', '吴老师', 'A2026002', '13855107005', '340111198902180055', '信息化建设与管理办公室', '负责平台维护、数据备份与权限配置。', 'ADMIN', 1, '2026-05-07 09:40:00'),
('UDEMOVOL', '周同学', '2022210006', '13955107006', '340111199905210066', '校园流浪猫志愿服务队', '负责上报核实、救助建档、审核协助和回访。', 'VOLUNTEER', 1, '2026-05-07 09:50:00'),
('UDEMOHOSP', '黄医生', 'H2026002', '13655107007', '340111199206180101', '校内合作医院', '负责医疗记录录入、体检疫苗绝育和健康信息维护。', 'HOSPITAL', 1, '2026-05-07 10:00:00'),
('UDEMOUSER', '赵同学', '2024210008', '13755107008', '340111200203090085', '计算机与信息学院', '有过照顾校园流浪猫的经历，愿意按平台要求提交线索、申请认养并配合回访。', 'STUDENT', 1, '2026-05-07 10:10:00');

UPDATE t_user
SET user_name = '吴老师',
    school_no = 'A2026002',
    phone = '13855107005',
    id_card = '340111198902180055',
    college = '信息化建设与管理办公室',
    pet_experience = '负责平台维护、数据备份与权限配置。',
    role_code = 'ADMIN',
    status = 1,
    password_hash = ''
WHERE user_id = 'UDEMOADMIN';

UPDATE t_user
SET user_name = '周同学',
    school_no = '2022210006',
    phone = '13955107006',
    id_card = '340111199905210066',
    college = '校园流浪猫志愿服务队',
    pet_experience = '负责上报核实、救助建档、审核协助和回访。',
    role_code = 'VOLUNTEER',
    status = 1,
    password_hash = ''
WHERE user_id = 'UDEMOVOL';

UPDATE t_user
SET user_name = '黄医生',
    school_no = 'H2026002',
    phone = '13655107007',
    id_card = '340111199206180101',
    college = '校内合作医院',
    pet_experience = '负责医疗记录录入、体检疫苗绝育和健康信息维护。',
    role_code = 'HOSPITAL',
    status = 1,
    password_hash = ''
WHERE user_id = 'UDEMOHOSP';

UPDATE t_user
SET user_name = '赵同学',
    pet_experience = '有过照顾校园流浪猫的经历，愿意按平台要求提交线索、申请认养并配合回访。',
    school_no = '2024210008',
    phone = '13755107008',
    id_card = '340111200203090085',
    college = '计算机与信息学院',
    role_code = 'STUDENT',
    status = 1,
    password_hash = ''
WHERE user_id = 'UDEMOUSER';
