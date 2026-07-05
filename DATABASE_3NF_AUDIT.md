# 数据库 3NF 规范化说明

## 当前结论

旧版 `schema.sql` + `schema-mis-upgrade.sql` 是演示迭代式结构，包含兼容字段、重复字段和逗号列表字段，不能严格声明满足第三范式。

本项目新增：

- `src/main/resources/schema-3nf.sql`：严格 3NF 的干净建库脚本。
- `src/main/resources/migrate-to-3nf.sql`：从旧库迁移到 3NF 库的字段映射和拆表脚本。

## 主要规范化处理

### 1. 多值字段拆表

旧结构：

- `t_cat.tags`
- `t_article.tags`
- `t_application.score_reasons`

问题：一个字段保存多个值，违反第一范式。

新结构：

- `cat_tag(cat_id, tag_name)`
- `article_tag(article_id, tag_name)`
- `application_score_reason(application_id, reason_order, reason_text, score_delta)`

### 2. 删除重复兼容字段

旧结构中存在大量语义相同的字段：

- `system_message.user_id / receiver_id`
- `system_message.read_flag / read_status`
- `t_notice.publisher / publisher_id`
- `t_notice.enabled / publish_status`
- `t_notice.published_at / publish_time`
- `adoption_agreement.content / agreement_content`
- `adoption_agreement.handover_place / handover_location`
- `followup_record.environment_description / environment_desc`
- `followup_record.abnormal_description / abnormal_desc`
- `warning_record.description / content`
- `warning_record.handle_opinion / handle_comment`
- `dict_item.item_label / dict_label`
- `dict_item.item_value / dict_value`

新结构只保留一个规范字段，避免同一事实被保存两次。

### 3. 删除传递依赖字段

旧结构中不少字段可以通过外键推导：

- `adoption_agreement.cat_id/user_id/adopter_id` 可由 `application_id -> t_application` 推导。
- `followup_task.cat_id/user_id/adopter_id` 可由 `application_id` 或 `agreement_id` 推导。
- `followup_record.application_id/cat_id/user_id/adopter_id/agreement_id` 可由 `task_id -> followup_task` 推导。
- `t_application.agreement_no` 可由 `adoption_agreement` 查询。

新结构删除这些冗余字段，只保留业务事实所在表的主键依赖字段。

### 4. 审核信息独立成表

旧结构：

- `t_application.initial_auditor_id`
- `t_application.initial_audit_note`
- `t_application.initial_audited_at`
- `t_application.final_auditor_id`
- `t_application.final_audit_note`
- `t_application.final_audited_at`
- 同时又有 `adoption_audit`

问题：申请表同时保存审核事实，和审核记录表重复。

新结构：审核事实统一进入 `adoption_audit`，申请表只保存申请本身和当前状态。

### 5. 商品订单金额规范化

旧结构：

- `t_product_order.amount`

问题：无法区分历史成交单价和总金额。

新结构：

- `unit_price`
- `total_amount`

订单保留成交时单价，避免产品改价后历史订单金额无法解释。

### 6. 外键补全

旧结构中升级表大多只有索引，没有外键。

新结构给以下关系补外键：

- 申请、审核、协议、回访任务、回访记录
- 医疗记录、线索、猫照片、猫标签
- 公告、消息、操作日志
- 订单、捐赠、收藏、评论

这样可以避免孤儿记录，保证引用完整性。

## 不建议直接在旧库 DROP 字段

当前 Java mapper 仍有旧字段引用，例如：

- `CatMapper` 读取 `t_cat.tags`
- `SystemMessageMapper` 写入 `read_flag/read_status`
- `AgreementMapper` 写入 `content/agreement_content`
- `FollowupTaskMapper` 写入 `environment_description/environment_desc`

所以严格 3NF 的落地顺序应该是：

1. 建新库并运行 `schema-3nf.sql`。
2. 按 `migrate-to-3nf.sql` 迁移数据。
3. 修改 mapper/service，使其只访问规范化表。
4. 切换 `MYSQL_URL` 到新库。
5. 验证接口后弃用旧库。
