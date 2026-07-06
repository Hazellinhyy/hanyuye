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

- 普通用户：`2024210008`
- 志愿者：`2022210006`
- 医院用户：`H2026002`
- 管理员：`A2026002`
- 当前本机数据库管理员：`A2026001`

医院用户又称“合作医院”或“医疗协作用户”，统一对应角色 `HOSPITAL`，主要负责医疗记录录入、体检、疫苗、绝育记录和健康信息维护。

## 技术框架与运行环境

本系统采用前后端一体化部署方式，后端负责 REST API 和静态资源托管，前端通过浏览器访问 `index.html` 和 `mis-router.js`。

- 后端框架：Spring Boot 3、Spring Web、MyBatis 注解式 Mapper、Jakarta Validation
- 前端框架：原生 HTML、CSS、JavaScript 单页路由，无需额外前端构建工具
- 数据库：MySQL 8，当前库名为 `cat_adoption_system`
- Java 版本：JDK 17 及以上
- 构建工具：Maven
- 认证方式：后端签发 Token，前端保存在 `localStorage`，请求时通过 `Authorization: Bearer ...` 传递

## 实际运行命令

Windows PowerShell：

```powershell
$env:MYSQL_URL="jdbc:mysql://localhost:3306/cat_adoption_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false"
$env:MYSQL_USERNAME="root"
$env:MYSQL_PASSWORD="root"
.\mvnw.cmd spring-boot:run
```

Linux / macOS Bash：

```bash
export MYSQL_URL="jdbc:mysql://localhost:3306/cat_adoption_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false"
export MYSQL_USERNAME="root"
export MYSQL_PASSWORD="root"
./mvnw spring-boot:run
```

数据库备份和恢复示例：

```bash
mysqldump -uroot -proot cat_adoption_system > cat_adoption_system_backup.sql
mysql -uroot -proot -D cat_adoption_system < db_physical_3nf_cleanup.sql
mysql -uroot -proot -D cat_adoption_system < db_notice_3nf_repair.sql
```

编译与前端脚本检查：

```bash
./mvnw -DskipTests compile
node --check src/main/resources/static/mis-router.js
```

## 代码组成框架

```text
src/main/java/com/hfut/cat_adoption_system
├── auth        权限注解、Token、登录上下文和拦截器
├── common      统一响应、异常处理、数据质量校验
├── config      Web 配置、历史数据补齐、三范式兼容迁移
├── controller  前台和后台 REST 接口
├── dto         前后端传输对象
├── mapper      MyBatis 数据访问层
├── model       业务实体和枚举
└── service     核心业务编排服务

src/main/resources/static
├── index.html      前端入口页面
├── mis-router.js   正式单页应用路由、接口请求、表单和页面渲染
└── styles.css      前台和后台统一样式

db_*.sql            数据库三范式调整、公告修复、风险标签补数据脚本
cat_adoption_system_before_3nf_cleanup.sql  三范式改造前备份
```

## 数据库三范式设计说明

当前物理库按课程要求尽量满足第三范式：

- 主表只保存本实体的直接属性，例如 `t_notice` 只保存公告标题、正文、发布状态、发布人 ID 等。
- 多值字段拆成关联表，例如公告发布范围拆到 `notice_target_role`，猫咪标签拆到 `cat_tag`，文章标签拆到 `article_tag`。
- 风险标签拆成独立关系表，例如 `application_risk_tag`、`adoption_audit_risk_tag`，避免在申请表或审核表中保存逗号分隔字符串。
- 冗余展示字段通过 Mapper 查询时 JOIN 或子查询聚合回来，保证前端接口字段不变。
- 已取消的评论收藏功能已删除对应物理表：`t_comment`、`t_generic_collect`。

三范式相关脚本：

- `db_normalize_3nf.sql`：逻辑规范化和关联表补建脚本
- `db_physical_3nf_cleanup.sql`：物理库冗余字段清理脚本
- `db_remove_comment_collect_and_seed_risk_tags.sql`：删除评论收藏表并补风险标签数据
- `db_notice_3nf_repair.sql`：修复公告发布人和公告角色关联数据



## 核心业务流程

MIS 正式演示流程如下：

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

## 已实现功能

- 用户注册、登录、资料维护、角色权限控制
- 前台公告浏览、公告详情、按角色控制公告可见范围
- 发现线索提交、志愿者核实、无效线索处理、有效线索生成猫咪档案
- 猫咪档案维护、状态流转、标签、照片、生命周期时间线
- 医疗记录维护，包括体检、疫苗、绝育、治疗、异常记录和附件
- 认养申请提交、申请评分、初审、终审、作废和取消
- 协议生成、协议编辑、交接完成、协议取消和逻辑删除
- 7/30/90 天回访任务生成、认养人反馈、后台补录、异常回访
- 逾期回访刷新、异常预警生成、预警处理
- 系统消息通知、操作日志、后台 Dashboard、全局搜索、CSV 导出
- 后台公告管理，包括草稿、发布、下架、删除、图片上传和角色范围

当前不作为正式业务演示的功能：

- 文创商城和商品订单
- 猫咪图像识别
- 社区评论和收藏

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
MYSQL_PASSWORD=root
```

如果你的 MySQL root 密码不是 `root`，用 PowerShell 这样启动：

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
