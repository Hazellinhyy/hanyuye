SET NAMES utf8mb4;

UPDATE t_user
SET user_name = '周老师',
    school_no = 'A2026001',
    phone = '13855107001',
    id_card = '340111198608150015',
    college = '信息化建设与管理办公室',
    pet_experience = '负责平台账号、权限、数据备份和业务流程配置。',
    role_code = 'ADMIN',
    status = 1
WHERE user_id = 'U2026050701';

UPDATE t_user
SET user_name = '沈同学',
    school_no = '2022210002',
    phone = '13955107002',
    id_card = '340111200103120021',
    college = '校园流浪猫志愿服务队',
    pet_experience = '负责线索核实、救助建档、认养审核协助和回访跟进。',
    role_code = 'VOLUNTEER',
    status = 1
WHERE user_id = 'U2026050702';

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
SET user_name = '刘医生',
    school_no = 'H2026001',
    phone = '13655107004',
    id_card = '340111198011220041',
    college = '合肥合作动物医院',
    pet_experience = '负责健康检查、疫苗、绝育和治疗记录反馈。',
    role_code = 'HOSPITAL',
    status = 1
WHERE user_id = 'U2026050704';

UPDATE t_user
SET user_name = '张同学',
    school_no = '2024210031',
    phone = '13755107031',
    id_card = '340111200204260111',
    college = '计算机与信息学院',
    pet_experience = '有一年校园流浪猫投喂和照护经历，能按要求配合回访。',
    role_code = 'STUDENT',
    status = 1
WHERE user_id = 'U2026062801';

UPDATE t_user
SET user_name = '王同学',
    school_no = '2024210032',
    phone = '13755107032',
    id_card = '340111200205170126',
    college = '计算机与信息学院',
    pet_experience = '宿舍外长期参与固定投喂，了解封窗、疫苗和绝育要求。',
    role_code = 'STUDENT',
    status = 1
WHERE user_id = 'U2026062802';

UPDATE t_user
SET user_name = '陈同学',
    school_no = '2023210456',
    phone = '13955107033',
    id_card = '340111200101200132',
    college = '计算机与信息学院',
    pet_experience = '家中有养猫经验，能承担猫粮、猫砂和基础医疗费用。',
    role_code = 'STUDENT',
    status = 1
WHERE user_id = 'U2026062803';

UPDATE t_user
SET user_name = '赵同学',
    school_no = '2024210507',
    phone = '13855107034',
    id_card = '340111200202110152',
    college = '计算机与信息学院',
    pet_experience = '有基础养宠经验，愿意按平台要求提交线索、申请认养并配合回访。',
    role_code = 'STUDENT',
    status = 1
WHERE user_id = 'U2026062804';

UPDATE t_user
SET user_name = '孙同学',
    school_no = '2022210507',
    phone = '13955107035',
    id_card = '34011119980723016X',
    college = '校园流浪猫志愿服务队',
    pet_experience = '负责线索复核、现场拍照、救助记录和回访跟进。',
    role_code = 'VOLUNTEER',
    status = 1
WHERE user_id = 'U2026062805';

UPDATE t_user
SET user_name = '吴老师',
    school_no = 'A2026002',
    phone = '13855107005',
    id_card = '340111198902180055',
    college = '信息化建设与管理办公室',
    pet_experience = '负责平台维护、数据备份与权限配置。',
    role_code = 'ADMIN',
    status = 1
WHERE user_id = 'UDEMOADMIN';

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

UPDATE t_user
SET user_name = '黄医生',
    school_no = 'H2026002',
    phone = '13655107007',
    id_card = '340111199206180101',
    college = '校内合作医院',
    pet_experience = '负责医疗记录录入、体检疫苗绝育和健康信息维护。',
    role_code = 'HOSPITAL',
    status = 1
WHERE user_id = 'UDEMOHOSP';

UPDATE t_user
SET user_name = '赵同学',
    school_no = '2024210008',
    phone = '13755107008',
    id_card = '340111200203090085',
    college = '计算机与信息学院',
    pet_experience = '有过照顾校园流浪猫的经历，愿意按平台要求提交线索、申请认养并配合回访。',
    role_code = 'STUDENT',
    status = 1
WHERE user_id = 'UDEMOUSER';

UPDATE t_rescue_report
SET reporter_name = '李同学'
WHERE reporter_id = 'U2026050703'
  AND reporter_name REGEXP '验收|普通学生用户|普通用户|临时|演示';

UPDATE t_rescue_report
SET reporter_name = '王同学'
WHERE reporter_id = 'U2026062802'
  AND reporter_name REGEXP '验收|普通学生用户|普通用户|临时|演示';

UPDATE t_rescue_report
SET reporter_name = '赵同学'
WHERE reporter_id = 'U2026062804'
  AND reporter_name REGEXP '验收|普通学生用户|普通用户|临时|演示';

UPDATE system_message
SET title = '认养申请进入交接',
    content = '你的认养申请 APP260626108 已通过终审，请等待协议交接安排。'
WHERE receiver_id = 'U2026050703' AND title = 'Application approved';

UPDATE system_message
SET title = '认养协议已生成',
    content = '认养协议 AGR202606260002 已生成，请按通知完成线下交接。'
WHERE receiver_id = 'U2026050703' AND title = 'Agreement generated';

UPDATE system_message
SET title = '回访任务已生成',
    content = '交接完成后，系统已自动生成 7/30/90 天回访任务，请按时提交反馈。'
WHERE receiver_id = 'U2026050703' AND title = 'Follow-up task created';

UPDATE system_message
SET title = '后台数据已更新',
    content = '线索、申请、协议、回访和预警业务数据已同步更新。'
WHERE receiver_id = 'U2026050701' AND title = 'Dashboard data ready';

UPDATE system_message
SET title = '异常预警已处理',
    content = '你的回访异常预警已由管理员处理，请查看处理意见。'
WHERE receiver_id = 'U2026050703' AND title = 'Warning handled';

UPDATE operation_log
SET operator_name = CASE operator_id
        WHEN 'U2026050701' THEN '系统管理员'
        WHEN 'U2026050702' THEN '猫咪志愿者小组'
        WHEN 'U2026050703' THEN '李同学'
        WHEN 'U2026050704' THEN '合肥合作动物医院'
        ELSE operator_name
    END,
    operation_type = CASE operation_type
        WHEN 'Verify clue' THEN '核实线索'
        WHEN 'Create cat from clue' THEN '线索转猫咪档案'
        WHEN 'Create medical record' THEN '新增医疗记录'
        WHEN 'Final audit application' THEN '认养申请终审'
        WHEN 'Generate agreement' THEN '生成认养协议'
        WHEN 'Complete handover' THEN '完成协议交接'
        WHEN 'Submit follow-up record' THEN '提交回访记录'
        WHEN 'Handle warning' THEN '处理异常预警'
        WHEN 'Export cats CSV' THEN '导出猫咪档案'
        WHEN 'Export applications CSV' THEN '导出认养申请'
        WHEN 'Export followups CSV' THEN '导出回访任务'
        WHEN 'Export warnings CSV' THEN '导出异常预警'
        WHEN 'Global search' THEN '全局搜索'
        WHEN 'Publish notice' THEN '发布公告'
        WHEN 'Toggle dict item' THEN '启停字典项'
        ELSE operation_type
    END,
    remark = CASE
        WHEN remark LIKE 'Stage % demo log' THEN '业务流程演示记录'
        ELSE remark
    END
WHERE operator_name LIKE '%Demo%' OR remark LIKE 'Stage % demo log';

DELETE fr FROM followup_record fr
JOIN followup_task ft ON ft.id = fr.task_id
JOIN t_application app ON app.application_id = ft.application_id
WHERE app.handed_over_at IS NULL AND app.apply_status <> 'HANDED_OVER';

DELETE ft FROM followup_task ft
JOIN t_application app ON app.application_id = ft.application_id
WHERE app.handed_over_at IS NULL AND app.apply_status <> 'HANDED_OVER';

UPDATE warning_record wr
JOIN followup_task ft ON ft.id = wr.task_id
JOIN t_application app ON app.application_id = ft.application_id
SET wr.deleted = 1,
    wr.handle_status = 'IGNORED',
    wr.handle_comment = '关联申请未完成交接，回访任务已按业务规则清理。'
WHERE app.handed_over_at IS NULL AND app.apply_status <> 'HANDED_OVER';
