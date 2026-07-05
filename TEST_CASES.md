# 中期验收测试用例

## 1. 前台门户与公告

- 访问 `http://localhost:8080/#/`。
- 预期：页面显示公开公告列表，并提供“浏览可认养猫咪”“上报猫咪线索”入口。
- 验证接口：`GET /api/notices`。

## 2. 站内消息

- 使用普通用户登录后访问 `#/my/messages`。
- 预期：可按“全部/未读/已读”筛选消息。
- 点击单条“标记已读”。
- 预期：该消息状态变为已读。
- 点击“全部标记已读”。
- 预期：当前用户未读消息清零。
- 验证接口：`GET /api/user/messages`、`PUT /api/user/messages/{id}/read`、`PUT /api/user/messages/read-all`。

## 3. 管理驾驶舱

- 使用管理员、志愿者或医院用户访问 `#/admin/dashboard`。
- 预期：显示猫咪、线索、申请、回访、预警、未读消息统计。
- 预期：显示猫咪状态、申请状态、回访状态、预警类型分布。
- 验证接口：`GET /api/admin/dashboard/summary`、`GET /api/admin/dashboard/cat-status`、`GET /api/admin/dashboard/application-status`、`GET /api/admin/dashboard/followup-status`、`GET /api/admin/dashboard/warning-type`。

## 4. 公告管理

- 使用管理员访问 `#/admin/notices`。
- 新增公告，选择 `DRAFT`。
- 发布公告。
- 下架公告。
- 删除公告。
- 预期：列表状态随操作变化，前台门户只展示已发布公告。
- 验证接口：`POST /api/admin/notices`、`PUT /api/admin/notices/{id}`、`PUT /api/admin/notices/{id}/publish`、`PUT /api/admin/notices/{id}/offline`、`DELETE /api/admin/notices/{id}`。

## 5. 字典管理

- 使用管理员访问 `#/admin/dicts`。
- 按 `CAT_STATUS` 筛选。
- 新增字典项，编辑字典项，停用/启用字典项，删除字典项。
- 预期：列表刷新后能看到最新状态。
- 验证接口：`GET /api/admin/dicts`、`POST /api/admin/dicts`、`PUT /api/admin/dicts/{id}`、`PUT /api/admin/dicts/{id}/enabled`、`DELETE /api/admin/dicts/{id}`。

## 6. 操作日志

- 使用管理员访问 `#/admin/logs`。
- 使用操作人、操作类型、业务类型、关键词筛选。
- 点击日志详情。
- 预期：显示操作前值、后值和备注。
- 验证接口：`GET /api/admin/logs`、`GET /api/admin/logs/{id}`。

## 7. 权限边界

- 普通用户直接访问 `#/admin/notices`。
- 预期：显示 403。
- 志愿者访问 `#/admin/dicts`。
- 预期：显示 403。
- 医院用户访问 `#/admin/medical`。
- 预期：允许访问医疗记录菜单。

## 8. 自动消息触发

- 完成线索提交、线索核实、申请审核、协议生成、交接完成、回访提交、预警处理任一流程。
- 预期：相关用户在 `#/my/messages` 能看到业务消息。

## 9. 自动化检查

```powershell
mvn test
node --check src/main/resources/static/mis-router.js
```

预期：两条命令均通过。

## 10. CSV 数据导出

- 使用管理员登录后台。
- 分别访问 `#/admin/cats`、`#/admin/adoption/audits`、`#/admin/followups`、`#/admin/warnings`。
- 设置任意筛选条件，点击“导出 CSV”。
- 预期：浏览器下载 `cats.csv`、`applications.csv`、`followups.csv`、`warnings.csv`。
- 使用志愿者、医院用户或普通用户直接请求 `/api/admin/export/cats`。
- 预期：返回 403 或被前端拦截到无权限页面。

## 11. 后台全局搜索

- 使用管理员或志愿者登录后台。
- 在后台顶部搜索框输入 `CAT260501004`、`APP260626108`、`RP260626001`、`AGR202606260002`、`回访`。
- 预期：搜索结果按猫咪、线索、申请、协议、预警分组展示。
- 点击任一结果。
- 预期：跳转到对应后台页面或带筛选条件的列表页。
- 使用医院用户搜索。
- 预期：只展示医疗相关猫咪档案结果。

## 12. 消息未读数量与处理链路

- 普通用户访问 `#/my/messages`。
- 预期：可看到申请、协议、回访、预警处理消息。
- 点击“全部标记已读”。
- 预期：未读消息变为已读，Dashboard 中当前用户未读数量变化。

## 13. 中期演示主链路

- 按 `DEMO_SCRIPT.md` 从普通用户提交线索开始完整跑一遍。
- 预期：线索、建档、医疗、发布、申请、初审、终审、协议、交接、回访、预警、消息、日志、Dashboard 均能展示。
