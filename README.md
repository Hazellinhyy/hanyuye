# cat-adoption-system

合肥工业大学校园流浪猫在线认养系统，是面向校园流浪猫救助、认养和后续回访的 MIS 系统。当前正式形态为“前台认养门户 + 后台管理系统”：

- 前台用于访客浏览、普通学生注册登录、提交发现线索、提交认养申请、查看个人申请/线索/回访/消息。
- 后台用于志愿者、合作医院和管理员处理线索核实、猫咪档案、医疗记录、认养审核、协议交接、回访任务、异常预警、公告、字典、日志、Dashboard、搜索和导出。
- 正式前端入口是 `index.html -> mis-router.js`。
- 旧 `app.js` 是早期展示型原型逻辑，仅为历史兼容保留，不作为正式演示入口。

## 正式访问入口

启动后浏览器访问：

```text
http://localhost:8080/#/
```

常用入口：

- 前台门户：`http://localhost:8080/#/`
- 登录页：`http://localhost:8080/#/login`
- 注册页：`http://localhost:8080/#/register`
- 管理员后台：`http://localhost:8080/#/admin/dashboard`
- 医院工作台：`http://localhost:8080/#/admin/medical`

后台入口不再单独依赖旧页面。用户登录后，可通过前台中的“进入后台/工作台”按钮进入对应后台模块。普通学生访问任意 `#/admin/*` 页面应显示 403。

## 演示账号

默认密码均为 `123456`：

- 普通用户：`user / 123456`
- 志愿者：`volunteer / 123456`
- 医院用户：`hospital / 123456`
- 管理员：`admin / 123456`

医院用户又称“合作医院”或“医疗协作用户”，统一对应角色 `HOSPITAL`，主要负责医疗记录录入、体检、疫苗、绝育记录和健康信息维护。历史演示账号 `2024210001`、`VOL001`、`HOSPITAL001`、`ADMIN001` 仍可作为兼容账号使用。

## 核心业务流程

新版 MIS 正式演示流程如下：

1. 访客浏览前台首页、公告和可认养猫咪。
2. 普通学生注册/登录后提交发现线索。
3. 志愿者在后台核实线索。
4. 有效线索可一键生成猫咪档案。
5. 医院用户维护猫咪医疗记录、体检、疫苗和绝育信息。
6. 志愿者或管理员在满足条件后发布可认养猫咪。
7. 普通学生对 `ADOPTABLE` 状态猫咪提交认养申请。
8. 志愿者进行初审。
9. 管理员进行终审。
10. 管理员生成认养协议并完成交接。
11. 交接完成后，猫咪进入 `FOLLOWING` 回访中状态。
12. 系统生成 7/30/90 天回访任务。
13. 普通学生提交回访反馈。
14. 异常回访或逾期回访生成预警。
15. 管理员处理预警。
16. 系统消息通知和操作日志贯穿主流程。
17. 全部关键回访完成后，猫咪可进入 `ADOPTED` 已认养状态。

## 角色权限

- 访客：可访问前台首页、公告、可认养猫咪列表和公开详情；不能提交线索、认养申请或访问个人中心。
- 普通学生：可提交发现线索、申请可认养猫咪、查看自己的线索、申请、回访、消息和资料；不能进入后台。
- 志愿者：可核实线索、维护猫咪档案、发布认养状态、初审认养申请、查看回访任务和预警、查看后台 Dashboard。
- 医院用户：可进入医院工作台，查看猫咪档案相关信息，新增、编辑、作废医疗记录。
- 管理员：可终审认养申请、生成协议、完成交接、处理预警、管理用户、公告、字典、日志、导出和后台系统配置。

## 猫咪状态说明

当前代码中的主要猫咪状态包括：

- `PENDING_VERIFY`：待核实
- `OBSERVING`：观察中
- `MEDICAL`：医疗中
- `ADOPTABLE`：可认养
- `APPLYING`：申请中
- `RESERVED`：待交接
- `FOLLOWING`：回访中
- `ADOPTED`：已认养
- `SUSPENDED`：暂停认养
- `RETURN_PENDING`：退养待处理
- `MISSING`：失联

注意：交接完成后不直接标记为 `ADOPTED`。当前正式流程是交接后进入 `FOLLOWING` 回访中，系统生成 7/30/90 天回访任务；关键回访完成后再进入 `ADOPTED`。

## MySQL 配置

默认读取环境变量，未设置时使用下面配置：

```properties
MYSQL_URL=jdbc:mysql://localhost:3306/cat_adoption_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false
MYSQL_USERNAME=root
MYSQL_PASSWORD=123456
```

如果你的 MySQL root 密码不是 `123456`，用 PowerShell 这样启动：

```powershell
$env:MYSQL_USERNAME="root"
$env:MYSQL_PASSWORD="你的密码"
.\mvnw.cmd spring-boot:run
```

## 数据库初始化说明

新建干净数据库时，优先使用当前整合后的基础脚本：

```sql
SOURCE src/main/resources/schema.sql;
SOURCE src/main/resources/data.sql;
```

说明：

- `schema.sql` 已包含当前运行所需的基础表和 MIS 扩展表结构。
- `data.sql` 使用大量 `INSERT IGNORE`，重复启动不会重复导入基础猫咪数据。
- 如果 `schema.sql` 已经包含 MIS 表结构，不要重复执行会追加相同字段的 `schema-mis-upgrade.sql`。
- `schema-mis-upgrade.sql` 仅用于从旧演示库升级到 MIS 版本。
- `data-mis-demo.sql` 用于补充 MIS 演示数据，重复执行前应确认是否会产生重复日志或演示记录。
- 不确定当前库状态时，请先备份数据库。

如果课程或验收要求严格满足第三范式，请使用 `src/main/resources/schema-3nf.sql` 作为干净建库脚本。旧版兼容脚本保留了若干历史字段，不应作为严格 3NF 设计提交。

## 运行

```powershell
.\mvnw.cmd spring-boot:run
```

浏览器访问：

```text
http://localhost:8080/#/
```

## 验证

测试使用 H2 的 MySQL 兼容模式，不依赖本机 MySQL 密码：

```powershell
.\mvnw.cmd test
```

前端路由脚本语法检查：

```powershell
node --check src/main/resources/static/mis-router.js
```

## 正式主要接口

以下接口是新版 MIS 正式前端和正式演示流程使用的接口。

### 认证与用户

- `POST /api/users/login`
- `POST /api/users`
- `GET /api/users/me`
- `PATCH /api/users/me`

### 前台猫咪

- `GET /api/cats/public`
- `GET /api/cats/public/{id}`
- `GET /api/cats/{id}/timeline`

### 线索

- `POST /api/clues`
- `GET /api/my/clues`
- `GET /api/admin/clues`
- `PUT /api/admin/clues/{id}/verify`
- `POST /api/admin/clues/{id}/create-cat`
- `PUT /api/admin/clues/{id}/invalid`

登录用户可提交发现线索，普通学生是主要使用者；志愿者和管理员通过后台处理线索。

### 猫咪后台

- `GET /api/admin/cats`
- `POST /api/admin/cats`
- `PUT /api/admin/cats/{id}`
- `PUT /api/admin/cats/{id}/status`
- `DELETE /api/admin/cats/{id}`

后台删除/归档以业务安全为准，优先使用逻辑删除或归档，不作为物理删除演示。

### 医疗

- `POST /api/admin/cats/{catId}/medical-records`
- `PUT /api/admin/medical-records/{id}`
- `PUT /api/admin/medical-records/{id}/void`

### 认养申请

- `POST /api/adoption/applications`
- `GET /api/my/adoption/applications`
- `PUT /api/my/adoption/applications/{id}/cancel`
- `GET /api/admin/adoption/applications`
- `PUT /api/admin/adoption/applications/{id}/initial-audit`
- `PUT /api/admin/adoption/applications/{id}/final-audit`
- `PUT /api/admin/adoption/applications/{id}/void`

### 协议交接

- `GET /api/admin/agreements/pending`
- `POST /api/admin/agreements/{applicationId}/generate`
- `PUT /api/admin/agreements/{id}/handover`
- `PUT /api/admin/agreements/{id}/cancel`

### 回访

- `GET /api/my/followup/tasks`
- `POST /api/my/followup/tasks/{id}/records`
- `GET /api/admin/followup/tasks`
- `PUT /api/admin/followup/tasks/{id}/mark-abnormal`
- `POST /api/admin/followup/tasks/refresh-overdue`

### 预警

- `GET /api/admin/warnings`
- `PUT /api/admin/warnings/{id}/handle`

### 消息

- `GET /api/user/messages`
- `GET /api/user/messages/unread-count`
- `PUT /api/user/messages/{id}/read`
- `PUT /api/user/messages/read-all`

### 公告

- `GET /api/notices`
- `GET /api/admin/notices`
- `POST /api/admin/notices`
- `PUT /api/admin/notices/{id}`
- `PUT /api/admin/notices/{id}/publish`
- `PUT /api/admin/notices/{id}/offline`
- `DELETE /api/admin/notices/{id}`

### 字典

- `GET /api/dicts/{dictType}`
- `GET /api/admin/dicts`
- `POST /api/admin/dicts`
- `PUT /api/admin/dicts/{id}`
- `PUT /api/admin/dicts/{id}/enabled`
- `DELETE /api/admin/dicts/{id}`

### 日志、Dashboard、搜索、导出

- `GET /api/admin/logs`
- `GET /api/admin/dashboard/summary`
- `GET /api/admin/dashboard/cat-status`
- `GET /api/admin/dashboard/application-status`
- `GET /api/admin/dashboard/followup-status`
- `GET /api/admin/dashboard/warning-type`
- `GET /api/admin/search`
- `GET /api/admin/export/cats`
- `GET /api/admin/export/applications`
- `GET /api/admin/export/followups`
- `GET /api/admin/export/warnings`

## 历史兼容接口说明

以下接口或文件为早期原型或兼容保留，不作为中期正式演示入口：

- `/api/reports`
- `/api/applications`
- `/api/followups`
- `/api/medical-records`
- `/api/cats` 的后台增改删能力
- 旧 `app.js`

正式演示以 `mis-router.js` 和新版 MIS 接口为准。旧接口不删除，是为了兼容历史页面、历史测试或已有数据，但不应再写入正式演示脚本的主流程。

## 旧 app.js 说明

当前 `index.html` 只加载：

```html
<script src="/mis-router.js"></script>
```

`app.js` 是早期展示型原型逻辑，不是正式前端工作台。不要使用旧 `#auth/#forms` 页面或旧原型路由作为演示入口。

## 猫咪识别模型

识别功能由后端统一接入专业视觉 embedding 模型，前端只上传图片，不再让用户填写模型地址。默认模型名为 `openai/clip-vit-base-patch32`，默认调用地址为：

```properties
RECOGNITION_MODEL_ENDPOINT=http://localhost:9000/v1/image-embedding
RECOGNITION_MODEL_NAME=openai/clip-vit-base-patch32
RECOGNITION_MODEL_API_KEY=
```

模型服务接口接收 `multipart/form-data`：

- `model`：模型名
- `image`：图片文件

返回格式支持：

```json
{"model":"openai/clip-vit-base-patch32","embedding":[0.0123,0.0456]}
```

也兼容 OpenAI 风格的 `data[0].embedding`。如果模型服务短暂不可用，系统会按 `RECOGNITION_MODEL_FALLBACK_TO_LOCAL=true` 降级到内置颜色/轮廓特征，保证业务可用。

项目已内置一个可部署的 CLIP 模型服务目录：`model-service/`。启动方式：

```powershell
cd model-service
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
uvicorn app:app --host 0.0.0.0 --port 9000
```

上线生产环境建议把 `RECOGNITION_MODEL_ENDPOINT` 指向内网模型服务地址；如果需要更强精度，也可以把 `model-service/app.py` 中的模型替换为 DINOv2、ResNet embedding 或已微调的猫脸重识别模型。

## 展示建议

1. 先展示前台门户、公告和可认养猫咪。
2. 再按普通用户、志愿者、医院用户、管理员四类角色跑主流程。
3. 最后展示 Dashboard、全局搜索、CSV 导出、操作日志和 `MIDTERM_FEATURES.md`。

