package com.hfut.cat_adoption_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class HistoricalMessageBackfill implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(HistoricalMessageBackfill.class);

    private final JdbcTemplate jdbcTemplate;

    public HistoricalMessageBackfill(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        backfill("pending clue volunteer messages", pendingClueMessages());
        backfill("completed clue verification thanks", clueVerificationThanks());
        backfill("completed clue archive thanks", clueArchiveThanks());
        backfill("adoption audit thanks", adoptionAuditThanks());
        backfill("final audit todo messages", finalAuditTodoMessages());
        backfill("medical record thanks", medicalRecordThanks());
        backfill("unassigned medical record hospital messages", unassignedMedicalRecordHospitalMessages());
        backfill("application owner messages", applicationOwnerMessages());
        backfill("clue reporter messages", clueReporterMessages());
        backfill("follow-up adopter messages", followupAdopterMessages());
        backfill("follow-up task volunteer messages", followupTaskMessages());
        backfill("follow-up record thanks", followupRecordThanks());
        backfill("empty inbox role messages", emptyInboxRoleMessages());
    }

    private void backfill(String name, String sql) {
        try {
            int inserted = jdbcTemplate.update(sql);
            log.info("Backfilled {} system messages for {}.", inserted, name);
        } catch (Exception error) {
            log.debug("Skipped historical message backfill for {}: {}", name, error.getMessage());
        }
    }

    private String messageInsertPrefix() {
        return """
                INSERT INTO system_message
                (user_id, receiver_id, message_type, title, content, related_type, related_id,
                 biz_type, biz_id, read_flag, read_status, status, deleted, create_by, update_by,
                 create_time, update_time)
                """;
    }

    private String enabledVolunteers() {
        return """
                SELECT user_id
                FROM t_user
                WHERE status = 1 AND UPPER(role_code) = 'VOLUNTEER'
                """;
    }

    private String enabledAdmins() {
        return """
                SELECT user_id
                FROM t_user
                WHERE status = 1 AND UPPER(role_code) = 'ADMIN'
                """;
    }

    private String enabledHospitals() {
        return """
                SELECT user_id
                FROM t_user
                WHERE status = 1
                  AND UPPER(role_code) IN ('HOSPITAL', 'HOSPITAL_USER', 'MEDICAL', 'DOCTOR', 'PARTNER_HOSPITAL')
                """;
    }

    private String notExists(String receiverExpr, String title, String bizTypeExpr, String bizIdExpr) {
        return """
                AND NOT EXISTS (
                    SELECT 1 FROM system_message sm
                    WHERE COALESCE(sm.deleted, 0) = 0
                      AND sm.receiver_id = %s
                      AND sm.title = '%s'
                      AND sm.biz_type = %s
                      AND sm.biz_id = %s
                )
                """.formatted(receiverExpr, title, bizTypeExpr, bizIdExpr);
    }

    private String pendingClueMessages() {
        return messageInsertPrefix() + """
                SELECT v.user_id, v.user_id, 'CLUE', '新的线索待审核',
                       CONCAT('有一条历史猫咪线索需要核实，地点：', COALESCE(r.found_place, '-'), '。请在前台“线索审核”中处理。'),
                       'CLUE', r.report_id, 'CLUE', r.report_id, 0, 'UNREAD', 'VALID', 0,
                       COALESCE(r.reporter_id, 'SYSTEM'), COALESCE(r.reporter_id, 'SYSTEM'),
                       COALESCE(r.reported_at, NOW()), COALESCE(r.reported_at, NOW())
                FROM t_rescue_report r
                CROSS JOIN (%s) v
                WHERE COALESCE(r.deleted, 0) = 0
                  AND r.report_status = 'PENDING_VERIFY'
                """.formatted(enabledVolunteers())
                + notExists("v.user_id", "新的线索待审核", "'CLUE'", "r.report_id");
    }

    private String clueVerificationThanks() {
        return messageInsertPrefix() + """
                SELECT r.verify_user_id, r.verify_user_id, 'CLUE', '感谢完成线索审核',
                       CONCAT('你已完成线索 ', COALESCE(r.report_id, '-'), ' 的核实处理，系统已记录结果。'),
                       'CLUE', r.report_id, 'CLUE', r.report_id, 0, 'UNREAD', 'VALID', 0,
                       COALESCE(r.verify_user_id, 'SYSTEM'), COALESCE(r.verify_user_id, 'SYSTEM'),
                       COALESCE(r.verify_time, r.update_time, NOW()), COALESCE(r.verify_time, r.update_time, NOW())
                FROM t_rescue_report r
                WHERE COALESCE(r.deleted, 0) = 0
                  AND r.verify_user_id IS NOT NULL
                  AND r.report_status IN ('VERIFIED_VALID', 'INVALID', 'DUPLICATE', 'CREATED_CAT')
                """
                + notExists("r.verify_user_id", "感谢完成线索审核", "'CLUE'", "r.report_id");
    }

    private String clueArchiveThanks() {
        return messageInsertPrefix() + """
                SELECT r.verify_user_id, r.verify_user_id, 'CAT', '感谢完成线索建档',
                       CONCAT('你已将线索 ', COALESCE(r.report_id, '-'), ' 转为猫咪档案，系统已记录建档结果。'),
                       'CAT', r.created_cat_id, 'CAT', r.created_cat_id, 0, 'UNREAD', 'VALID', 0,
                       COALESCE(r.verify_user_id, 'SYSTEM'), COALESCE(r.verify_user_id, 'SYSTEM'),
                       COALESCE(r.update_time, r.verify_time, NOW()), COALESCE(r.update_time, r.verify_time, NOW())
                FROM t_rescue_report r
                WHERE COALESCE(r.deleted, 0) = 0
                  AND r.verify_user_id IS NOT NULL
                  AND r.created_cat_id IS NOT NULL
                """
                + notExists("r.verify_user_id", "感谢完成线索建档", "'CAT'", "r.created_cat_id");
    }

    private String adoptionAuditThanks() {
        return messageInsertPrefix() + """
                SELECT a.auditor_id, a.auditor_id, 'APPLICATION',
                       CASE WHEN a.audit_stage = 'FINAL' THEN '感谢完成认养终审' ELSE '感谢完成认养初审' END,
                       CONCAT('你已完成申请 ', a.application_id, ' 的', CASE WHEN a.audit_stage = 'FINAL' THEN '终审' ELSE '初审' END, '，系统已记录处理结果。'),
                       'APPLICATION', a.application_id, 'APPLICATION', a.application_id, 0, 'UNREAD', 'VALID', 0,
                       COALESCE(a.auditor_id, 'SYSTEM'), COALESCE(a.auditor_id, 'SYSTEM'),
                       COALESCE(a.audit_time, a.create_time, NOW()), COALESCE(a.audit_time, a.create_time, NOW())
                FROM adoption_audit a
                WHERE COALESCE(a.deleted, 0) = 0
                  AND a.auditor_id IS NOT NULL
                AND NOT EXISTS (
                    SELECT 1 FROM system_message sm
                    WHERE COALESCE(sm.deleted, 0) = 0
                      AND sm.receiver_id = a.auditor_id
                      AND sm.title = CASE WHEN a.audit_stage = 'FINAL' THEN '感谢完成认养终审' ELSE '感谢完成认养初审' END
                      AND sm.biz_type = 'APPLICATION'
                      AND sm.biz_id = a.application_id
                )
                """;
    }

    private String finalAuditTodoMessages() {
        return messageInsertPrefix() + """
                SELECT admin.user_id, admin.user_id, 'APPLICATION', '新的认养终审任务',
                       CONCAT('申请 ', a.application_id, ' 已通过初审，等待管理员终审。'),
                       'APPLICATION', a.application_id, 'APPLICATION', a.application_id, 0, 'UNREAD', 'VALID', 0,
                       COALESCE(a.auditor_id, 'SYSTEM'), COALESCE(a.auditor_id, 'SYSTEM'),
                       COALESCE(a.audit_time, a.create_time, NOW()), COALESCE(a.audit_time, a.create_time, NOW())
                FROM adoption_audit a
                CROSS JOIN (%s) admin
                WHERE COALESCE(a.deleted, 0) = 0
                  AND a.audit_stage = 'INITIAL'
                  AND a.audit_result = 'APPROVED'
                """.formatted(enabledAdmins())
                + notExists("admin.user_id", "新的认养终审任务", "'APPLICATION'", "a.application_id");
    }

    private String medicalRecordThanks() {
        return messageInsertPrefix() + """
                SELECT m.hospital_user_id, m.hospital_user_id, 'MEDICAL', '感谢完成医疗记录',
                       CONCAT('你已完成猫咪 ', COALESCE(c.cat_name, m.cat_id), ' 的医疗记录录入，系统已同步健康状态。'),
                       'MEDICAL', m.medical_id, 'MEDICAL', m.medical_id, 0, 'UNREAD', 'VALID', 0,
                       COALESCE(m.hospital_user_id, 'SYSTEM'), COALESCE(m.hospital_user_id, 'SYSTEM'),
                       COALESCE(m.created_at, NOW()), COALESCE(m.created_at, NOW())
                FROM t_medical_record m
                LEFT JOIN t_cat c ON c.cat_id = m.cat_id
                WHERE COALESCE(m.deleted, 0) = 0
                  AND m.hospital_user_id IS NOT NULL
                """
                + notExists("m.hospital_user_id", "感谢完成医疗记录", "'MEDICAL'", "m.medical_id");
    }

    private String unassignedMedicalRecordHospitalMessages() {
        return messageInsertPrefix() + """
                SELECT h.user_id, h.user_id, 'MEDICAL', '历史医疗记录已同步',
                       CONCAT('系统已同步猫咪 ', COALESCE(c.cat_name, m.cat_id), ' 的历史医疗记录，记录编号：', m.medical_id, '。'),
                       'MEDICAL', m.medical_id, 'MEDICAL', m.medical_id, 0, 'UNREAD', 'VALID', 0,
                       'SYSTEM', 'SYSTEM', COALESCE(m.created_at, NOW()), COALESCE(m.created_at, NOW())
                FROM t_medical_record m
                CROSS JOIN (%s) h
                LEFT JOIN t_cat c ON c.cat_id = m.cat_id
                WHERE COALESCE(m.deleted, 0) = 0
                  AND (m.hospital_user_id IS NULL OR m.hospital_user_id = '')
                """.formatted(enabledHospitals())
                + notExists("h.user_id", "历史医疗记录已同步", "'MEDICAL'", "m.medical_id");
    }

    private String applicationOwnerMessages() {
        return messageInsertPrefix() + """
                SELECT a.user_id, a.user_id, 'APPLICATION', '认养申请历史状态',
                       CONCAT('你的认养申请 ', a.application_id, ' 当前状态为：',
                              CASE a.apply_status
                                  WHEN 'PENDING_INITIAL' THEN '待初审'
                                  WHEN 'PENDING_FINAL' THEN '待终审'
                                  WHEN 'PENDING_HANDOVER' THEN '待交接'
                                  WHEN 'HANDED_OVER' THEN '已交接'
                                  WHEN 'CANCELLED' THEN '已取消'
                                  WHEN 'WITHDRAWN' THEN '已撤回'
                                  WHEN 'INITIAL_REJECTED' THEN '初审未通过'
                                  WHEN 'FINAL_REJECTED' THEN '终审未通过'
                                  ELSE a.apply_status
                              END, '。'),
                       'APPLICATION', a.application_id, 'APPLICATION', a.application_id, 0, 'UNREAD', 'VALID', 0,
                       'SYSTEM', 'SYSTEM', COALESCE(a.reviewed_at, a.applied_at, NOW()), COALESCE(a.reviewed_at, a.applied_at, NOW())
                FROM t_application a
                WHERE a.user_id IS NOT NULL
                """
                + notExists("a.user_id", "认养申请历史状态", "'APPLICATION'", "a.application_id");
    }

    private String clueReporterMessages() {
        return messageInsertPrefix() + """
                SELECT r.reporter_id, r.reporter_id, 'CLUE', '线索历史状态',
                       CONCAT('你的猫咪线索 ', r.report_id, ' 当前状态为：',
                              CASE r.report_status
                                  WHEN 'PENDING_VERIFY' THEN '待核实'
                                  WHEN 'VERIFIED_VALID' THEN '已核实有效'
                                  WHEN 'CREATED_CAT' THEN '已建档'
                                  WHEN 'INVALID' THEN '无效'
                                  WHEN 'DUPLICATE' THEN '重复'
                                  ELSE r.report_status
                              END, '。'),
                       'CLUE', r.report_id, 'CLUE', r.report_id, 0, 'UNREAD', 'VALID', 0,
                       'SYSTEM', 'SYSTEM', COALESCE(r.verify_time, r.update_time, r.reported_at, NOW()),
                       COALESCE(r.verify_time, r.update_time, r.reported_at, NOW())
                FROM t_rescue_report r
                WHERE COALESCE(r.deleted, 0) = 0
                  AND r.reporter_id IS NOT NULL
                """
                + notExists("r.reporter_id", "线索历史状态", "'CLUE'", "r.report_id");
    }

    private String followupAdopterMessages() {
        return messageInsertPrefix() + """
                SELECT ft.user_id, ft.user_id, 'FOLLOWUP', '回访任务历史状态',
                       CONCAT('你的 ',
                              CASE COALESCE(ft.task_type, ft.round_name)
                                  WHEN 'DAY_7' THEN '7天适应回访'
                                  WHEN 'DAY_30' THEN '30天稳定回访'
                                  WHEN 'DAY_90' THEN '90天长期回访'
                                  ELSE COALESCE(ft.task_type, ft.round_name, '回访')
                              END,
                              ' 任务当前状态为：',
                              CASE ft.status
                                  WHEN 'PENDING' THEN '待回访'
                                  WHEN 'COMPLETED' THEN '已完成'
                                  WHEN 'OVERDUE' THEN '已逾期'
                                  WHEN 'ABNORMAL' THEN '异常'
                                  ELSE ft.status
                              END, '。'),
                       'FOLLOWUP', CAST(ft.id AS CHAR), 'FOLLOWUP', CAST(ft.id AS CHAR), 0, 'UNREAD', 'VALID', 0,
                       'SYSTEM', 'SYSTEM', COALESCE(ft.completed_time, ft.create_time, NOW()), COALESCE(ft.completed_time, ft.create_time, NOW())
                FROM followup_task ft
                WHERE COALESCE(ft.deleted, 0) = 0
                  AND ft.user_id IS NOT NULL
                """
                + notExists("ft.user_id", "回访任务历史状态", "'FOLLOWUP'", "CAST(ft.id AS CHAR)");
    }

    private String followupTaskMessages() {
        return messageInsertPrefix() + """
                SELECT v.user_id, v.user_id, 'FOLLOWUP', '新的回访任务待处理',
                       CONCAT('申请 ', ft.application_id, ' 已生成回访任务，请在前台“回访任务”中跟进。'),
                       'FOLLOWUP', ft.application_id, 'FOLLOWUP', ft.application_id, 0, 'UNREAD', 'VALID', 0,
                       COALESCE(ft.create_by, 'SYSTEM'), COALESCE(ft.create_by, 'SYSTEM'),
                       COALESCE(ft.create_time, NOW()), COALESCE(ft.create_time, NOW())
                FROM followup_task ft
                CROSS JOIN (%s) v
                WHERE COALESCE(ft.deleted, 0) = 0
                  AND ft.status IN ('PENDING', 'OVERDUE', 'ABNORMAL')
                """.formatted(enabledVolunteers())
                + notExists("v.user_id", "新的回访任务待处理", "'FOLLOWUP'", "ft.application_id");
    }

    private String followupRecordThanks() {
        return messageInsertPrefix() + """
                SELECT fr.create_by, fr.create_by, 'FOLLOWUP', '感谢完成回访任务',
                       CONCAT('你已完成猫咪 ', COALESCE(c.cat_name, fr.cat_id), ' 的回访记录，系统已同步任务状态。'),
                       'FOLLOWUP', CAST(fr.task_id AS CHAR), 'FOLLOWUP', CAST(fr.task_id AS CHAR), 0, 'UNREAD', 'VALID', 0,
                       COALESCE(fr.create_by, 'SYSTEM'), COALESCE(fr.create_by, 'SYSTEM'),
                       COALESCE(fr.submit_time, fr.create_time, NOW()), COALESCE(fr.submit_time, fr.create_time, NOW())
                FROM followup_record fr
                JOIN t_user u ON u.user_id = fr.create_by AND UPPER(u.role_code) = 'VOLUNTEER'
                LEFT JOIN t_cat c ON c.cat_id = fr.cat_id
                WHERE COALESCE(fr.deleted, 0) = 0
                """
                + notExists("fr.create_by", "感谢完成回访任务", "'FOLLOWUP'", "CAST(fr.task_id AS CHAR)");
    }

    private String emptyInboxRoleMessages() {
        return messageInsertPrefix() + """
                SELECT u.user_id, u.user_id, 'SYSTEM',
                       CASE
                           WHEN UPPER(u.role_code) = 'VOLUNTEER' THEN '志愿者工作台消息已同步'
                           WHEN UPPER(u.role_code) IN ('HOSPITAL', 'HOSPITAL_USER', 'MEDICAL', 'DOCTOR', 'PARTNER_HOSPITAL') THEN '医疗协作消息已同步'
                           WHEN UPPER(u.role_code) = 'ADMIN' THEN '管理员工作台消息已同步'
                           ELSE '个人门户消息已同步'
                       END,
                       CASE
                           WHEN UPPER(u.role_code) = 'VOLUNTEER' THEN
                               CONCAT('系统已为你同步志愿者工作台。当前可处理线索审核、认养初审和回访任务；如有新的待办，会继续发送提醒。')
                           WHEN UPPER(u.role_code) IN ('HOSPITAL', 'HOSPITAL_USER', 'MEDICAL', 'DOCTOR', 'PARTNER_HOSPITAL') THEN
                               CONCAT('系统已为你同步医疗协作门户。当前可在医疗工作台查看医疗中、观察中猫咪并录入健康记录；新的医疗待办会继续发送提醒。')
                           WHEN UPPER(u.role_code) = 'ADMIN' THEN
                               CONCAT('系统已为你同步管理员工作台。当前可处理终审、协议交接、预警和系统维护任务。')
                           ELSE
                               CONCAT('系统已为你同步个人门户。你可以查看认养申请、回访提醒和系统通知。')
                       END,
                       'SYSTEM', u.user_id, 'SYSTEM', u.user_id, 0, 'UNREAD', 'VALID', 0,
                       'SYSTEM', 'SYSTEM', COALESCE(u.created_at, NOW()), COALESCE(u.created_at, NOW())
                FROM t_user u
                WHERE u.status = 1
                  AND NOT EXISTS (
                      SELECT 1 FROM system_message sm
                      WHERE COALESCE(sm.deleted, 0) = 0
                        AND sm.receiver_id = u.user_id
                  )
                """;
    }
}
