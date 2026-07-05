(function () {
    const shell = document.getElementById("mis-shell");
    if (!shell) {
        return;
    }

    let screenClockTimer = null;

    const roleLabels = {
        STUDENT: "普通用户",
        VOLUNTEER: "志愿者",
        HOSPITAL: "合作医院 / 医疗协作用户",
        HOSPITAL_USER: "合作医院 / 医疗协作用户",
        MEDICAL: "合作医院 / 医疗协作用户",
        DOCTOR: "合作医院 / 医疗协作用户",
        "合作医院": "合作医院 / 医疗协作用户",
        "医疗协作用户": "合作医院 / 医疗协作用户",
        ADMIN: "管理员"
    };

    const clueStatuses = {
        PENDING_VERIFY: { label: "待核实", tone: "warning" },
        VERIFIED_VALID: { label: "已核实有效", tone: "primary" },
        CREATED_CAT: { label: "已建档", tone: "success" },
        DUPLICATE: { label: "重复线索", tone: "info" },
        INVALID: { label: "无效线索", tone: "danger" },
        "待核实": { label: "待核实", tone: "warning" },
        "待紧急核实": { label: "待核实", tone: "warning" }
    };

    const urgencyLabels = {
        NORMAL: { label: "普通", tone: "info" },
        HIGH: { label: "较急", tone: "warning" },
        URGENT: { label: "紧急", tone: "danger" }
    };

    const catStatusLabels = {
        PENDING_VERIFY: { label: "待核实", tone: "warning" },
        OBSERVING: { label: "观察中", tone: "info" },
        MEDICAL: { label: "医疗中", tone: "danger" },
        ADOPTABLE: { label: "可认养", tone: "success" },
        APPLYING: { label: "申请中", tone: "warning" },
        ADOPTED: { label: "已认养", tone: "primary" },
        FOLLOWING: { label: "回访中", tone: "primary" },
        SUSPENDED: { label: "暂停认养", tone: "info" },
        RETURN_PENDING: { label: "退养待处理", tone: "danger" },
        RESERVED: { label: "待交接", tone: "warning" },
        MISSING: { label: "失联", tone: "danger" }
    };

    const healthLabels = {
        A: { label: "健康", tone: "success" },
        B: { label: "需观察", tone: "warning" },
        C: { label: "需治疗", tone: "danger" }
    };

    const applicationStatusLabels = {
        PENDING_INITIAL: { label: "待初审", tone: "warning" },
        PENDING_FINAL: { label: "待终审", tone: "warning" },
        INITIAL_REJECTED: { label: "初审拒绝", tone: "danger" },
        FINAL_REJECTED: { label: "终审拒绝", tone: "danger" },
        PENDING_HANDOVER: { label: "待交接", tone: "primary" },
        HANDED_OVER: { label: "已交接", tone: "success" },
        CANCELLED: { label: "已取消", tone: "info" },
        PENDING: { label: "待审核", tone: "warning" },
        APPROVED: { label: "已通过", tone: "primary" },
        REJECTED: { label: "已拒绝", tone: "danger" },
        WITHDRAWN: { label: "已撤回", tone: "info" }
    };

    const riskLabels = {
        LOW: { label: "低风险", tone: "success" },
        MEDIUM: { label: "中风险", tone: "warning" },
        HIGH: { label: "高风险", tone: "danger" }
    };

    const agreementStatusLabels = {
        NOT_GENERATED: { label: "未生成", tone: "info" },
        DRAFT: { label: "草稿", tone: "info" },
        GENERATED: { label: "已生成", tone: "warning" },
        HANDED_OVER: { label: "已交接", tone: "success" },
        CANCELLED: { label: "已取消", tone: "danger" }
    };

    const followupTaskLabels = {
        DAY_7: { label: "7天适应回访", tone: "primary" },
        DAY_30: { label: "30天稳定回访", tone: "primary" },
        DAY_90: { label: "90天长期回访", tone: "primary" },
        PENDING: { label: "待回访", tone: "warning" },
        COMPLETED: { label: "已完成", tone: "success" },
        OVERDUE: { label: "逾期", tone: "danger" },
        ABNORMAL: { label: "异常", tone: "danger" }
    };

    const warningTypeLabels = {
        FOLLOWUP_OVERDUE: { label: "回访逾期", tone: "warning" },
        FOLLOWUP_ABNORMAL: { label: "回访异常", tone: "danger" },
        HEALTH_ABNORMAL: { label: "健康异常", tone: "danger" },
        HIGH_RISK_APPLICATION: { label: "高风险申请", tone: "warning" },
        RETURN_REQUEST: { label: "退养申请", tone: "danger" }
    };

    const warningStatusLabels = {
        PENDING: { label: "待处理", tone: "danger" },
        PROCESSING: { label: "处理中", tone: "warning" },
        HANDLED: { label: "已处理", tone: "success" },
        IGNORED: { label: "已忽略", tone: "info" }
    };

    const userRoutes = [
        { path: "#/", label: "门户首页", title: "合肥工业大学校园流浪猫在线认养系统", text: "校园公益认养与流浪猫全生命周期管理平台。" },
        { path: "#/data-screen", label: "数据大屏", title: "实时数据大屏", text: "查看猫咪档案、认养进度、公告和健康管理实时概览。" },
        { path: "#/cats", label: "可认养猫咪", title: "可认养猫咪", text: "公开猫咪列表、搜索筛选和认养入口。" },
        { path: "#/notices", label: "公告", title: "系统公告", text: "查看认养、回访、志愿协同相关公告。" },
        { path: "#/clues/submit", label: "上报线索", title: "线索上报", text: "普通用户提交发现地点、时间、照片、描述和联系方式。" },
        { path: "#/my/clues", label: "我的线索", title: "我的线索", text: "查看本人提交线索的核实和建档进度。" },
        { path: "#/my/applications", label: "我的申请", title: "我的申请", text: "查看认养申请状态、审核意见和协议交接信息。" },
        { path: "#/my/followups", label: "我的回访", title: "我的回访", text: "查看待回访任务并提交回访反馈。" },
        { path: "#/my/messages", label: "我的消息", title: "我的消息", text: "查看系统通知、审核结果、协议和回访提醒。" },
        { path: "#/hospital", label: "医疗协作", roles: ["HOSPITAL"], title: "医疗协作门户", text: "合作医院查看待医疗猫咪、最近医疗记录和后台入口。" },
        { path: "#/profile", label: "个人中心", title: "个人中心", text: "维护个人资料，汇总线索、申请、回访和消息。" },
        { path: "#/login", label: "登录", title: "登录", text: "使用统一账号进入前台或后台。" },
        { path: "#/register", label: "注册", title: "注册", text: "创建普通用户账号，提交线索和认养申请。" }
    ];

    const adminRoutes = [
        { path: "#/admin/dashboard", label: "Dashboard", roles: ["VOLUNTEER", "ADMIN"], title: "后台首页", text: "展示猫咪、线索、申请、回访、预警等 MIS 指标。" },
        { path: "#/admin/hospital", label: "医院首页", roles: ["HOSPITAL", "ADMIN"], title: "医院协作后台", text: "合作医院查看待处理猫咪、本院医疗记录和健康异常。" },
        { path: "#/admin/clues", label: "线索核实", roles: ["VOLUNTEER", "ADMIN"], title: "线索核实管理", text: "志愿者核实线索，管理员查看和追踪处理结果。" },
        { path: "#/admin/cats", label: "猫咪档案", roles: ["VOLUNTEER", "ADMIN"], title: "猫咪档案管理", text: "维护猫咪档案、照片、标签和生命周期状态。" },
        { path: "#/admin/medical", label: "医疗工作台", roles: ["HOSPITAL", "ADMIN"], title: "医疗记录管理", text: "医院用户录入体检、疫苗、绝育、治疗和异常记录。" },
        { path: "#/admin/adoption/audits", label: "申请审核", roles: ["VOLUNTEER", "ADMIN"], title: "认养申请审核", text: "志愿者初审，管理员终审，沉淀审核记录。" },
        { path: "#/admin/agreements", label: "协议交接", roles: ["VOLUNTEER", "ADMIN"], title: "协议交接管理", text: "管理员生成协议、登记交接并触发回访任务。" },
        { path: "#/admin/followups", label: "回访任务", roles: ["VOLUNTEER", "ADMIN"], title: "回访任务管理", text: "查看待回访、已完成、逾期和异常回访任务。" },
        { path: "#/admin/warnings", label: "异常预警", roles: ["VOLUNTEER", "ADMIN"], title: "异常预警中心", text: "处理回访逾期、回访异常、医疗异常和高风险申请。" },
        { path: "#/admin/users", label: "用户角色", roles: ["ADMIN"], title: "用户与角色管理", text: "管理员启停用户、分配角色并记录操作日志。" },
        { path: "#/admin/notices", label: "公告管理", roles: ["ADMIN"], title: "公告管理", text: "维护草稿、已发布、已下架公告。" },
        { path: "#/admin/logs", label: "操作日志", roles: ["ADMIN"], title: "操作日志", text: "查看审核、状态变更、交接、预警处理等关键日志。" },
        { path: "#/admin/dicts", label: "字典管理", roles: ["ADMIN"], title: "字典管理", text: "维护猫咪状态、审核状态、紧急程度、预警类型等字典。" }
    ];

    const privateFrontRoutePaths = new Set([
        "#/clues/submit",
        "#/my/clues",
        "#/adoption/apply/:catId",
        "#/my/applications",
        "#/my/followups",
        "#/my/messages",
        "#/hospital",
        "#/profile"
    ]);

    let authState = null;
    let authChecked = false;

    function normalizeRole(role) {
        const value = String(role || "").trim().toUpperCase();
        if (["HOSPITAL", "HOSPITAL_USER", "MEDICAL", "DOCTOR", "PARTNER_HOSPITAL"].includes(value)
                || role === "合作医院" || role === "医疗协作用户" || role === "医院用户") {
            return "HOSPITAL";
        }
        return value || "GUEST";
    }

    function isHospitalUser(userOrRole) {
        return normalizeRole(typeof userOrRole === "string" ? userOrRole : userOrRole?.role) === "HOSPITAL";
    }

    function readAuth() {
        try {
            return JSON.parse(localStorage.getItem("hfut-cat-auth") || "null");
        } catch (error) {
            return null;
        }
    }

    function currentUser() {
        if (!authState || !authState.token || !authState.user) return null;
        return { ...authState.user, role: normalizeRole(authState.user.role) };
    }

    function token() {
        return authState?.token || readAuth()?.token || "";
    }

    function saveAuth(result) {
        authState = result?.user ? { ...result, user: { ...result.user, role: normalizeRole(result.user.role) } } : result;
        localStorage.setItem("hfut-cat-auth", JSON.stringify(authState));
    }

    function clearAuth() {
        authState = null;
        ["hfut-cat-auth", "token", "userInfo", "role", "currentUser", "auth", "jwt"].forEach(key => localStorage.removeItem(key));
        sessionStorage.clear();
    }

    async function verifyAuth() {
        const saved = readAuth();
        if (!saved || !saved.token) {
            clearAuth();
            authChecked = true;
            return null;
        }
        authState = saved;
        try {
            const user = await api("/api/users/me");
            saveAuth({ token: saved.token, user });
            authChecked = true;
            return user;
        } catch (error) {
            clearAuth();
            authChecked = true;
            return null;
        }
    }

    function escapeHtml(value) {
        return String(value || "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    const defaultCatImage = "/uploads/cats/no-photo.svg";

    function normalizeImageUrl(url) {
        const value = String(url || "").trim();
        if (!value || value === "null" || value === "undefined") {
            return defaultCatImage;
        }
        return value;
    }

    function imageAttrs(url, className, alt) {
        return `class="${className}" src="${escapeHtml(normalizeImageUrl(url))}" alt="${escapeHtml(alt || "图片")}" onerror="this.onerror=null;this.src='${defaultCatImage}'"`;
    }

    function noticeTime(notice) {
        return String(notice?.publishedAt || notice?.publishTime || "").replace("T", " ");
    }

    function noticeKind(notice) {
        const text = `${notice?.title || ""} ${notice?.content || ""}`.toLowerCase();
        if (/follow|回访|photo|照片|反馈|提醒/.test(text)) return "followup";
        if (/adoption|认养|领养|开放日|open/.test(text)) return "adoption";
        if (/volunteer|志愿|协同|招募/.test(text)) return "volunteer";
        return "system";
    }

    function noticeMeta(notice) {
        const kind = noticeKind(notice);
        const meta = {
            adoption: {
                label: "认养活动",
                image: "/uploads/cats/cat_03_01.jpg",
                title: "校园认养开放日通知",
                summary: "本周五下午开放校园流浪猫认养咨询、资料核验和见面沟通，请有意向的同学提前安排时间。",
                sections: [
                    { title: "活动安排", text: "开放日将集中介绍可认养猫咪信息、认养条件、申请材料和后续回访要求。现场会有志愿者协助确认基础信息，并说明从提交申请到完成交接的完整流程。" },
                    { title: "参与提醒", text: "建议提前查看可认养猫咪档案，准备学生证明、联系方式和居住环境说明。已有意向对象的同学可以记录猫咪名称，到场后直接咨询。 " },
                    { title: "后续流程", text: "活动结束后，符合条件的申请会进入初审、终审、协议生成和交接环节。交接完成后仍需按 7 天、30 天、90 天节点提交回访。 " }
                ]
            },
            followup: {
                label: "回访提醒",
                image: "/uploads/cats/cat_12_01.jpg",
                title: "回访照片提交提醒",
                summary: "请按时提交清晰的环境照和猫咪近照，帮助志愿者确认猫咪适应情况和健康状态。",
                sections: [
                    { title: "照片要求", text: "请上传猫咪正面近照、日常活动环境、食水区域和猫砂区域照片。照片需要清晰、无遮挡，尽量在自然光或明亮室内环境下拍摄。" },
                    { title: "文字说明", text: "回访反馈中请说明猫咪饮食、排便、精神状态、与人的互动情况。如出现躲藏、拒食、呕吐、腹泻等异常，请在备注中写清楚发生时间和频次。" },
                    { title: "异常处理", text: "系统会对逾期回访和异常回访生成预警，志愿者或管理员会根据情况联系认养人，必要时协助转诊合作医院。" }
                ]
            },
            volunteer: {
                label: "志愿协同",
                image: "/uploads/cats/cat_08_01.jpg",
                title: "志愿协同事项公告",
                summary: "请志愿者关注线索核实、猫咪建档、医疗记录和认养审核的协同安排。",
                sections: [
                    { title: "工作重点", text: "近期重点处理待核实线索、待建档猫咪和待初审认养申请。请志愿者在后台及时更新处理结果，避免线索和申请长时间停留。" },
                    { title: "记录规范", text: "核实、建档、审核和回访处理时，请尽量补充清晰原因、照片和时间信息，方便管理员追踪业务闭环。" },
                    { title: "协作说明", text: "涉及医疗异常、退养申请或高风险认养申请时，请先保留完整记录，再交由管理员进行最终处理。" }
                ]
            },
            system: {
                label: "系统公告",
                image: "/uploads/cats/cat_01_01.jpg",
                title: "系统服务公告",
                summary: "这里发布系统使用、认养流程、资料维护和平台服务相关通知。",
                sections: [
                    { title: "公告说明", text: "平台公告用于同步认养流程、回访要求、志愿协作和系统服务变更。请在提交申请或处理业务前先查看最新公告。" },
                    { title: "使用建议", text: "普通用户可关注认养、线索和回访通知；志愿者、医院用户和管理员可关注后台协同、医疗记录、预警处理等事项。" },
                    { title: "消息同步", text: "重要事项会同时通过站内消息提醒。登录后可在个人中心查看与自己相关的申请、回访和系统通知。" }
                ]
            }
        }[kind];
        return meta;
    }

    function noticeTitle(notice) {
        const title = String(notice?.title || "").trim();
        const lower = title.toLowerCase();
        if (/adoption open day/.test(lower)) return "校园认养开放日通知";
        if (/follow-up photo reminder/.test(lower)) return "回访照片提交提醒";
        return title || noticeMeta(notice).title;
    }

    function noticeSummary(notice) {
        const content = String(notice?.content || "").trim();
        if (/campus adoption open day/i.test(content)) return noticeMeta(notice).summary;
        if (/submit clear environment and cat photos/i.test(content)) return noticeMeta(notice).summary;
        return content || noticeMeta(notice).summary;
    }

    function backendEntry(user) {
        const role = normalizeRole(user?.role);
        if (!user || role === "STUDENT") return "";
        if (role === "HOSPITAL") {
            return `<a class="primary-btn compact" href="#/admin/hospital">医院后台</a>`;
        }
        if (role === "VOLUNTEER") {
            return `<a class="primary-btn compact" href="#/admin/dashboard">志愿者工作台</a>`;
        }
        if (role === "ADMIN") {
            return `<a class="primary-btn compact" href="#/admin/dashboard">管理后台</a>`;
        }
        return "";
    }

    function rolePortal(user, data = {}) {
        const role = normalizeRole(user?.role);
        const unread = data.unread || 0;
        const pendingTasks = data.pendingTasks || 0;
        const summary = data.summary || {};
        const stats = data.stats || {};
        if (!user) {
            return `
                <section class="role-portal guest cute-portal">
                    <div class="portal-copy">
                        <p class="eyebrow">Visitor</p>
                        <div class="cute-title-row">
                            <span class="cute-icon">🐾</span>
                            <h2>校园流浪猫认养门户</h2>
                        </div>
                        <p>浏览可认养猫咪、查看公告与流程。想看实时统计，可以进入数据大屏。</p>
                        <div class="role-actions portal-actions">
                            <a class="primary-btn" href="#/login">登录</a>
                            <a class="ghost-btn" href="#/register">注册</a>
                            <a class="ghost-btn" href="#/cats">浏览猫咪</a>
                            <a class="ghost-btn" href="#/login?redirect=%23%2Fclues%2Fsubmit">提交线索</a>
                        </div>
                    </div>
                    <a class="cute-screen-card" href="#/data-screen">
                        <span class="cute-screen-badge">Live</span>
                        <div class="cute-screen-top">
                            <div class="cute-cat-face">ฅ</div>
                            <div>
                                <strong>实时数据大屏</strong>
                                <span>查看认养、健康和公告动态</span>
                            </div>
                        </div>
                        <div class="cute-screen-grid">
                            <span>猫咪档案 <strong>${stats.catCount || 0}</strong></span>
                            <span>可认养 <strong>${stats.adoptableCount || 0}</strong></span>
                            <span>待审核 <strong>${stats.pendingApplicationCount || 0}</strong></span>
                        </div>
                        <p>点击进入完整可视化面板</p>
                    </a>
                </section>
            `;
        }
        if (role === "STUDENT") {
            return `
                <section class="role-portal student">
                    <div>
                        <p class="eyebrow">普通用户 / 认养人</p>
                        <h2>${escapeHtml(user.userName)}，欢迎回到校园认养门户</h2>
                        <p>${unread ? `你有 ${unread} 条未读消息。` : "暂无未读消息。"}${pendingTasks ? ` 还有 ${pendingTasks} 个待回访任务。` : ""}</p>
                    </div>
                    <div class="role-actions">
                        <a class="primary-btn" href="#/cats">我要认养</a>
                        <a class="ghost-btn" href="#/clues/submit">提交线索</a>
                        <a class="ghost-btn" href="#/my/clues">我的线索</a>
                        <a class="ghost-btn" href="#/my/applications">我的申请</a>
                        <a class="ghost-btn" href="#/my/followups">我的回访</a>
                        <a class="ghost-btn" href="#/my/messages">我的消息${unread ? `(${unread})` : ""}</a>
                    </div>
                </section>
            `;
        }
        if (role === "VOLUNTEER") {
            return `
                <section class="role-portal volunteer">
                    <div>
                        <p class="eyebrow">志愿者</p>
                        <h2>${escapeHtml(user.userName)}，志愿者协同待办</h2>
                        <p>待核实线索 ${summary.pendingClueCount ?? "-"}，待初审申请 ${summary.pendingInitialApplicationCount ?? "-"}，后台待回访任务 ${summary.pendingFollowupTaskCount ?? "-"}。${pendingTasks ? ` 你作为认养人还有 ${pendingTasks} 个系统生成的待回访任务。` : ""}${unread ? ` 未读消息 ${unread} 条。` : ""}</p>
                    </div>
                    <div class="role-actions">
                        <a class="primary-btn" href="#/admin/dashboard">进入志愿者工作台</a>
                        <a class="ghost-btn" href="#/cats">我要认养</a>
                        <a class="ghost-btn" href="#/my/applications">我的申请</a>
                        <a class="ghost-btn" href="#/my/followups">我的回访${pendingTasks ? `(${pendingTasks})` : ""}</a>
                        <a class="ghost-btn" href="#/my/messages">我的消息${unread ? `(${unread})` : ""}</a>
                        <a class="ghost-btn" href="#/admin/clues">待核实线索</a>
                        <a class="ghost-btn" href="#/admin/cats">猫咪档案管理</a>
                        <a class="ghost-btn" href="#/admin/adoption/audits">认养初审</a>
                        <a class="ghost-btn" href="#/admin/followups">后台回访任务</a>
                    </div>
                </section>
            `;
        }
        if (isHospitalUser(user)) {
            return `
                <section class="role-portal hospital">
                    <div>
                        <p class="eyebrow">合作医院 / 医疗协作用户</p>
                        <h2>${escapeHtml(user.userName)}，进入医院工作台维护医疗记录</h2>
                        <p>医疗中猫咪 ${summary.medicalCatCount ?? "-"}，需观察 ${summary.observingCatCount ?? "-"}，健康异常记录 ${summary.abnormalRecordCount ?? "-"}。</p>
                    </div>
                    <div class="role-actions">
                        <a class="primary-btn" href="#/hospital">进入医疗协作门户</a>
                        <a class="ghost-btn" href="#/admin/hospital">医院后台首页</a>
                        <a class="ghost-btn" href="#/admin/medical">待医疗猫咪</a>
                        <a class="ghost-btn" href="#/admin/medical">医疗记录管理</a>
                        <a class="ghost-btn" href="#/admin/medical">健康异常记录</a>
                    </div>
                </section>
            `;
        }
        return `
            <section class="role-portal admin">
                <div>
                    <p class="eyebrow">系统管理员</p>
                    <h2>${escapeHtml(user.userName)}，系统管理概览</h2>
                    <p>待处理预警 ${summary.pendingWarningCount ?? "-"}，待终审申请 ${summary.pendingFinalApplicationCount ?? "-"}，高风险申请 ${summary.highRiskApplicationCount ?? "-"}。</p>
                </div>
                <div class="role-actions">
                    <a class="primary-btn" href="#/admin/dashboard">进入管理后台</a>
                    <a class="ghost-btn" href="#/admin/dashboard">Dashboard</a>
                    <a class="ghost-btn" href="#/admin/warnings">待处理预警</a>
                    <a class="ghost-btn" href="#/admin/users">用户管理</a>
                    <a class="ghost-btn" href="#/admin/notices">公告管理</a>
                    <a class="ghost-btn" href="#/admin/logs">操作日志</a>
                </div>
            </section>
        `;
    }

    async function api(path, options) {
        const headers = { "Content-Type": "application/json", ...(options && options.headers ? options.headers : {}) };
        const authToken = token();
        if (authToken) {
            headers.Authorization = `Bearer ${authToken}`;
        }
        const response = await fetch(path, { ...options, headers });
        const payload = await response.json().catch(() => ({ success: false, message: "接口返回异常" }));
        if (response.status === 401) {
            clearAuth();
                    throw new Error("Agent is temporarily unavailable. Please try again later.");
        }
        if (response.status === 403) {
            window.location.hash = "#/403";
                    throw new Error("Agent is temporarily unavailable. Please try again later.");
        }
        if (!response.ok || !payload.success) {
                    throw new Error("Agent is temporarily unavailable. Please try again later.");
        }
        return payload.data;
    }

    function validateCleanText(label, value, min, max) {
        const text = String(value || "").trim();
        if (!text) return `${label}不能为空`;
        if (text.length < min) return `${label}内容过短`;
        if (text.length > max) return `${label}内容过长`;
        const compact = text.replace(/\s+/g, "");
        const lower = compact.toLowerCase();
        if (/[?？]{3,}/.test(compact) || compact.includes("???")) return `${label}包含无效占位字符`;
        if (lower.includes("codex") || lower.includes("undefined") || lower.includes("null")) return `${label}包含无效内容`;
        if (lower.includes("test") || lower.includes("asdf") || lower.includes("qwer") || compact.includes("测试测试") || compact.includes("随便")) return `${label}包含明显测试内容`;
        if (/^[\p{P}\p{S}]+$/u.test(compact)) return `${label}不能全是标点符号`;
        if (/(.)\1{5,}/u.test(compact)) return `${label}包含过多重复字符`;
        return "";
    }

    function requireValid(messageEl, checks) {
        const message = checks.find(Boolean);
        if (message) {
            if (messageEl) messageEl.textContent = message;
                    throw new Error("Agent is temporarily unavailable. Please try again later.");
        }
    }

    function setSubmitting(form, submitting) {
        const button = form?.querySelector("button[type='submit'], button.primary-btn");
        if (button) {
            button.disabled = submitting;
            button.dataset.originalText ||= button.textContent;
            button.textContent = submitting ? "提交中..." : button.dataset.originalText;
        }
    }

    async function downloadCsv(path, filename) {
        const headers = {};
        const authToken = token();
        if (authToken) {
            headers.Authorization = `Bearer ${authToken}`;
        }
        const response = await fetch(path, { headers });
        if (!response.ok) {
            let message = "导出失败，请稍后重试";
            try {
                const payload = await response.json();
                message = payload.message || message;
                history[history.length - 1] = { role: "assistant", content: thinking.textContent };
            } catch (error) {
                // Keep the friendly fallback message.
            }
                    throw new Error("Agent is temporarily unavailable. Please try again later.");
        }
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        link.remove();
        URL.revokeObjectURL(url);
    }

    function uploadFileWithProgress(path, file, onProgress) {
        return new Promise((resolve, reject) => {
            const formData = new FormData();
            formData.append("file", file);
            const request = new XMLHttpRequest();
            request.open("POST", path);
            const authToken = token();
            if (authToken) {
                request.setRequestHeader("Authorization", `Bearer ${authToken}`);
            }
            request.upload.addEventListener("progress", event => {
                if (event.lengthComputable && onProgress) {
                    onProgress(Math.round((event.loaded / event.total) * 100));
                }
            });
            request.addEventListener("load", () => {
                let payload = null;
                try {
                    payload = JSON.parse(request.responseText || "{}");
                } catch (error) {
                    reject(new Error("上传接口返回异常"));
                    return;
                }
                if (request.status === 401) {
                    clearAuth();
                    reject(new Error(payload.message || "请先登录"));
                    return;
                }
                if (request.status === 403) {
                    window.location.hash = "#/403";
                    reject(new Error(payload.message || "无权限访问"));
                    return;
                }
                if (request.status < 200 || request.status >= 300 || !payload.success) {
                    reject(new Error(payload.message || "上传失败"));
                    return;
                }
                resolve(payload.data);
            });
            request.addEventListener("error", () => reject(new Error("网络异常，上传失败")));
            request.addEventListener("abort", () => reject(new Error("上传已取消")));
            request.send(formData);
        });
    }

    function bindAdminGlobalSearch() {
        const input = document.getElementById("admin-global-search");
        const panel = document.getElementById("admin-search-panel");
        if (!input || !panel || input.dataset.bound) {
            return;
        }
        input.dataset.bound = "1";
        let timer;
        input.addEventListener("input", () => {
            clearTimeout(timer);
            const keyword = input.value.trim();
            if (keyword.length < 2) {
                panel.classList.remove("active");
                panel.innerHTML = "";
                return;
            }
            panel.classList.add("active");
            panel.innerHTML = `<div class="mis-loading compact">搜索中...</div>`;
            timer = setTimeout(async () => {
                try {
                    const groups = await api(`/api/admin/search?keyword=${encodeURIComponent(keyword)}`);
                    panel.innerHTML = searchPanel(groups);
                    panel.querySelectorAll("a").forEach(link => link.addEventListener("click", () => {
                        panel.classList.remove("active");
                        input.value = "";
                    }));
                } catch (error) {
                    panel.innerHTML = `<div class="mis-error compact">${escapeHtml(error.message)}</div>`;
                }
            }, 280);
        });
        document.addEventListener("click", event => {
            if (!panel.contains(event.target) && event.target !== input) {
                panel.classList.remove("active");
            }
        });
    }

    function searchPanel(groups) {
        const visible = (groups || []).filter(group => group.items && group.items.length);
        if (!visible.length) {
            return `<div class="mis-empty compact">未找到相关记录</div>`;
        }
        return visible.map(group => `
            <section>
                <strong>${escapeHtml(group.label)}</strong>
                ${group.items.map(item => `<a href="${escapeHtml(item.targetHash)}"><span>${escapeHtml(item.title)}</span><small>${escapeHtml(item.subtitle || "")}</small>${item.status ? `<em>${escapeHtml(item.status)}</em>` : ""}</a>`).join("")}
            </section>
        `).join("");
    }

    function normalizeHash() {
        const hash = window.location.hash || "#/";
        if (hash === "#auth" || hash === "#/auth") {
            window.location.hash = "#/login";
            return "#/login";
        }
        if (hash === "#home") {
            window.location.hash = "#/";
            return "#/";
        }
        if (hash === "#cats") {
            window.location.hash = "#/cats";
            return "#/cats";
        }
        return hash.startsWith("#/") ? hash : "";
    }

    function findRoute(hash) {
        if (/^#\/cats\/[^/]+$/.test(hash)) {
            const catId = hash.split("/").pop();
            return { path: "#/cats/:id", label: "猫咪详情", title: `猫咪详情：${catId}`, text: "展示公开档案、医疗时间线、生命周期时间线和认养入口。" };
        }
        if (/^#\/notices\/[^/]+$/.test(hash)) {
            const noticeId = hash.split("/").pop();
            return { path: "#/notices/:id", label: "公告详情", title: `公告详情：${noticeId}`, text: "查看公告正文和发布时间。" };
        }
        if (/^#\/adoption\/apply\/[^/]+$/.test(hash)) {
            const catId = hash.split("/").pop();
            return { path: "#/adoption/apply/:catId", label: "认养申请", title: `提交认养申请：${catId}`, text: "填写居住、经验、费用、回访和承诺信息，系统会自动评分。" };
        }
        if (/^#\/admin\/cats\/[^/]+$/.test(hash)) {
            const catId = hash.split("/").pop();
            return { path: "#/admin/cats/:id", label: "猫咪详情", roles: ["VOLUNTEER", "HOSPITAL", "ADMIN"], title: `猫咪档案：${catId}`, text: "展示完整猫咪档案、医疗记录和生命周期时间线。" };
        }
        return userRoutes.concat(adminRoutes).find(route => route.path === hash);
    }

    function canVisit(route, role) {
        return !route.roles || route.roles.includes(normalizeRole(role));
    }

    function isPrivateFrontRoute(route) {
        return Boolean(route && !route.path.startsWith("#/admin") && privateFrontRoutePaths.has(route.path));
    }

    function userNav(activePath, user) {
        return userRoutes.filter(route => !["#/login", "#/register"].includes(route.path))
            .filter(route => user || !privateFrontRoutePaths.has(route.path))
            .filter(route => !route.roles || (user && canVisit(route, normalizeRole(user.role)))).map(route => `
            <a class="${activePath === route.path ? "active" : ""}" href="${route.path}">${route.label}</a>
        `).join("");
    }

    function adminNav(role, activePath) {
        const normalizedRole = normalizeRole(role);
        return adminRoutes
            .filter(route => canVisit(route, normalizedRole))
            .map(route => `<a class="${activePath === route.path ? "active" : ""}" href="${route.path}">${route.label}</a>`)
            .join("");
    }

    function tag(value, labels) {
        const item = labels[value] || { label: value || "-", tone: "info" };
        return `<span class="mis-tag ${item.tone}">${escapeHtml(item.label)}</span>`;
    }

    function canCreateCat(clue) {
        return ["VERIFIED_VALID"].includes(clue.status) && !clue.createdCatId;
    }

    function boolLabel(value, yes, no) {
        return value ? yes : no;
    }

    function splitTags(value) {
        if (!value) {
            return [];
        }
        return Array.isArray(value) ? value : String(value).split(",").map(item => item.trim()).filter(Boolean);
    }

    function catStatus(cat) {
        return String(cat?.status || cat?.catStatus || cat?.cat_status || "").trim().toUpperCase();
    }

    function isAdoptableCat(cat) {
        return catStatus(cat) === "ADOPTABLE";
    }

    function canSubmitAdoptionApplication(user) {
        const role = normalizeRole(user?.role);
        return role === "STUDENT" || role === "VOLUNTEER";
    }

    function adoptionAction(cat, user) {
        if (!isAdoptableCat(cat)) {
            return `<button class="ghost-btn" disabled>暂不可认养</button>`;
        }
        const target = `#/adoption/apply/${encodeURIComponent(cat.catId)}`;
        if (!user) {
            return `<a class="primary-btn" href="${loginHashFor(target)}">申请认养</a>`;
        }
        if (!canSubmitAdoptionApplication(user)) {
            return `<button class="ghost-btn" disabled>暂不可认养</button>`;
        }
        return `<a class="primary-btn" href="${escapeHtml(target)}">申请认养</a>`;
    }

    const adoptionReadingDocs = {
        followup: {
            title: "定期回访事宜",
            unlockText: "已阅读定期回访事宜，可勾选",
            content: `
                <p>认养交接完成后，平台会自动生成 7 天、30 天、90 天回访任务。认养人需按时提交猫咪近况、居住环境变化和照片，志愿者会根据反馈判断猫咪适应情况。</p>
                <p>如出现猫咪走失、生病、攻击行为、搬家、无法继续饲养等情况，应第一时间联系平台或志愿者，不得私自转送、遗弃或长期失联。</p>
                <p>逾期未回访、回访内容异常或无法联系时，系统会生成预警，管理员和志愿者将跟进核实。</p>
            `
        },
        commitment: {
            title: "认养承诺条款",
            unlockText: "已阅读认养承诺条款，可勾选",
            content: `
                <p>认养人承诺提供安全、稳定、适合猫咪生活的室内环境，做好门窗防护，承担猫粮、猫砂、疫苗、绝育、驱虫和必要医疗费用。</p>
                <p>认养人承诺不弃养、不虐待、不私自转让猫咪；因学习、工作、搬家、家庭变化等原因无法继续饲养时，应提前联系平台协商处理。</p>
                <p>认养人承诺如实填写申请资料，配合初审、终审、协议交接和后续回访。平台发现隐瞒重要事实或违反承诺时，可暂停申请、撤销认养或启动异常处理。</p>
            `
        }
    };

    function openAdoptionReadingDialog(kind, onUnlocked) {
        const doc = adoptionReadingDocs[kind];
        if (!doc) {
            return;
        }
        const existing = document.querySelector(".reading-modal-backdrop");
        if (existing) {
            existing.remove();
        }
        const modal = document.createElement("div");
        modal.className = "reading-modal-backdrop";
        modal.innerHTML = `
            <section class="reading-modal" role="dialog" aria-modal="true" aria-labelledby="reading-modal-title">
                <div class="reading-modal-head">
                    <h3 id="reading-modal-title">${escapeHtml(doc.title)}</h3>
                    <button class="icon-btn" type="button" data-reading-close aria-label="关闭">×</button>
                </div>
                <div class="reading-modal-body">${doc.content}</div>
                <div class="reading-modal-actions">
                    <span data-reading-countdown>请阅读 10 秒后继续</span>
                    <button class="primary-btn" type="button" data-reading-confirm disabled>我已阅读</button>
                </div>
            </section>
        `;
        document.body.appendChild(modal);
        const countdown = modal.querySelector("[data-reading-countdown]");
        const confirmButton = modal.querySelector("[data-reading-confirm]");
        let remaining = 10;
        const timer = window.setInterval(() => {
            remaining -= 1;
            if (remaining > 0) {
                countdown.textContent = `请阅读 ${remaining} 秒后继续`;
                return;
            }
            window.clearInterval(timer);
            countdown.textContent = doc.unlockText;
            confirmButton.disabled = false;
        }, 1000);
        const close = () => {
            window.clearInterval(timer);
            modal.remove();
        };
        modal.querySelector("[data-reading-close]").addEventListener("click", close);
        modal.addEventListener("click", event => {
            if (event.target === modal) {
                close();
            }
        });
        confirmButton.addEventListener("click", () => {
            onUnlocked();
            close();
        });
    }

    function renderForbidden(user) {
        document.body.classList.add("mis-active");
        shell.innerHTML = `
            <main class="mis-forbidden">
                <section>
                    <strong>403</strong>
                    <h1>无权限访问</h1>
                    <p>${user ? `${escapeHtml(roleLabels[normalizeRole(user.role)] || normalizeRole(user.role))} 当前不能进入该页面。` : "请先登录后再访问需要权限的后台页面。"}</p>
                    <div class="mis-actions">
                        <a class="primary-btn" href="#/">回到前台</a>
                        <a class="ghost-btn" href="#/login">前往登录</a>
                    </div>
                </section>
            </main>
        `;
    }

    function userShell(route, user, content) {
        if (screenClockTimer) {
            window.clearInterval(screenClockTimer);
            screenClockTimer = null;
        }
        const role = normalizeRole(user?.role);
        shell.innerHTML = `
            <div class="mis-user-layout">
                <header class="mis-user-header">
                    <a class="mis-brand" href="#/"><span>HFUT</span><strong>校园流浪猫认养门户</strong></a>
                    <nav>${userNav(route.path, user)}</nav>
                    <div class="mis-user-chip">${user ? `${backendEntry(user)}<a href="#/profile">${escapeHtml(user.userName)} · ${escapeHtml(roleLabels[role] || role)}</a><button id="mis-logout" type="button">退出</button>` : `<a href="#/login">登录</a><a href="#/register">注册</a>`}</div>
                </header>
                <main class="mis-user-main">${content}</main>
            </div>
        `;
        const logoutButton = document.getElementById("mis-logout");
        if (logoutButton) {
            logoutButton.addEventListener("click", async () => {
                try {
                    await api("/api/users/logout", { method: "POST" });
                } catch (error) {
                    // Local cleanup still matters when the token is already invalid.
                }
                clearAuth();
                window.location.hash = "#/";
                render();
            });
        }
    }

    function adminShell(route, user, content) {
        const role = normalizeRole(user?.role);
        shell.innerHTML = `
            <div class="mis-admin-layout">
                <aside class="mis-sidebar">
                    <a class="mis-brand admin" href="${escapeHtml(roleLanding(user))}"><span>MIS</span><strong>后台管理系统</strong></a>
                    <nav>${adminNav(user.role, route.path)}</nav>
                </aside>
                <div class="mis-admin-body">
                    <header class="mis-admin-topbar">
                        <div><small>后台 / ${escapeHtml(route.label)}</small><strong>${escapeHtml(route.title)}</strong></div>
                        ${role === "HOSPITAL" ? "" : `<div class="mis-admin-search">
                            <input id="admin-global-search" placeholder="全局搜索猫咪、线索、申请、协议、预警">
                            <div id="admin-search-panel" class="mis-search-panel"></div>
                        </div>`}
                        <div class="mis-user-chip"><a class="ghost-btn compact" href="#/">返回前台</a><span>${escapeHtml(user.userName)} · ${escapeHtml(roleLabels[role] || role)}</span><button id="mis-admin-logout" type="button">退出</button></div>
                    </header>
                    <main class="mis-admin-main">${content}</main>
                </div>
            </div>
        `;
        setTimeout(bindAdminGlobalSearch, 0);
        const logoutButton = document.getElementById("mis-admin-logout");
        if (logoutButton) {
            logoutButton.addEventListener("click", async () => {
                try {
                    await api("/api/users/logout", { method: "POST" });
                } catch (error) {
                    // Local cleanup still matters when the token is already invalid.
                }
                clearAuth();
                window.location.hash = "#/";
                render();
            });
        }
    }

    function pageHero(route) {
        return `
            <section class="mis-page-hero">
                <p>前台用户门户</p>
                <h1>${escapeHtml(route.title)}</h1>
                <span>${escapeHtml(route.text)}</span>
            </section>
        `;
    }

    function adminHero(route) {
        return `
            <section class="mis-admin-panel">
                <p>后台管理系统</p>
                <h1>${escapeHtml(route.title)}</h1>
                <span>${escapeHtml(route.text)}</span>
            </section>
        `;
    }

    function placeholderGrid() {
        return `
            <section class="mis-placeholder-grid">
                <article><strong>流程入口</strong><span>后续接入真实表单、列表、状态查询和消息提醒。</span></article>
                <article><strong>数据来源</strong><span>复用现有 Spring Boot 接口，逐步切换到 /api/my 与 /api/cats/public。</span></article>
                <article><strong>权限边界</strong><span>普通用户只能查看和操作自己的线索、申请、回访、消息。</span></article>
            </section>
        `;
    }

    function localDateTimeValue(date) {
        const pad = value => String(value).padStart(2, "0");
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
    }

    function enhanceClueSelectField(input, title, options) {
        if (!input) return;
        const label = input.closest("label");
        if (!label) return;
        label.classList.add("wide", "clue-select-field");
        if (label.firstChild) label.firstChild.textContent = title;
        input.required = false;
        input.classList.add("clue-custom-input");
        input.placeholder = title === "\u53d1\u73b0\u5730\u70b9" ? "\u4e0b\u62c9\u6846\u6ca1\u6709\u65f6\uff0c\u5728\u8fd9\u91cc\u8f93\u5165\u65b0\u5730\u70b9" : "\u4e0b\u62c9\u6846\u6ca1\u6709\u65f6\uff0c\u5728\u8fd9\u91cc\u8f93\u5165\u65b0\u533a\u57df";

        const select = document.createElement("select");
        select.className = "clue-choice-select";
        const defaultOption = document.createElement("option");
        defaultOption.value = "";
        defaultOption.textContent = `\u8bf7\u9009\u62e9${title}`;
        select.appendChild(defaultOption);
        const customOption = document.createElement("option");
        customOption.value = "__custom__";
        customOption.textContent = `\u65b0\u589e/\u81ea\u5b9a\u4e49${title}`;
        const addOption = (value, text = value) => {
            const cleanValue = String(value || "").trim();
            if (!cleanValue) return;
            const exists = [...select.options].some(option => option.value === cleanValue);
            if (exists) return;
            const option = document.createElement("option");
            option.value = cleanValue;
            option.textContent = text;
            select.insertBefore(option, customOption);
        };
        options.forEach(([value, text]) => addOption(value, text));
        select.appendChild(customOption);

        const addButton = document.createElement("button");
        addButton.type = "button";
        addButton.className = "ghost-btn compact add-choice-btn";
        addButton.textContent = "\u4fdd\u5b58\u5230\u4e0b\u62c9\u6846";

        const setCustomMode = active => {
            input.classList.toggle("is-visible", active);
            addButton.classList.toggle("is-visible", active);
            if (active) input.focus();
        };
        select.addEventListener("change", () => {
            if (select.value === "__custom__") {
                input.value = "";
                setCustomMode(true);
                return;
            }
            input.value = select.value;
            setCustomMode(false);
        });
        addButton.addEventListener("click", () => {
            const value = input.value.trim();
            if (!value) return;
            addOption(value);
            select.value = value;
            setCustomMode(false);
        });
        input.addEventListener("keydown", event => {
            if (event.key !== "Enter" || !input.classList.contains("is-visible")) return;
            event.preventDefault();
            addButton.click();
        });

        input.insertAdjacentElement("beforebegin", select);
        input.insertAdjacentElement("afterend", addButton);
        setCustomMode(false);
    }

    function bindClueSelectField(select, input) {
        if (!select || !input) return;
        const label = select.closest("label");
        const saveButton = label?.querySelector("[data-save-custom]");
        const customOption = [...select.options].find(option => option.value === "__custom__");
        const setCustomMode = active => {
            input.classList.toggle("is-visible", active);
            saveButton?.classList.toggle("is-visible", active);
            if (active) {
                input.value = "";
                input.focus();
            }
        };
        select.addEventListener("change", () => {
            if (select.value === "__custom__") {
                setCustomMode(true);
                return;
            }
            input.value = select.value;
            setCustomMode(false);
        });
        const saveCustom = () => {
            const value = input.value.trim();
            if (!value || !customOption) return;
            const exists = [...select.options].some(option => option.value === value);
            if (!exists) {
                const option = document.createElement("option");
                option.value = value;
                option.textContent = value;
                select.insertBefore(option, customOption);
            }
            select.value = value;
            input.value = value;
            setCustomMode(false);
        };
        saveButton?.addEventListener("click", saveCustom);
        input.addEventListener("keydown", event => {
            if (event.key !== "Enter" || !input.classList.contains("is-visible")) return;
            event.preventDefault();
            saveCustom();
        });
        setCustomMode(false);
    }

    function renderClueSubmit(route, user) {
        userShell(route, user, `
            ${pageHero(route)}
            <form class="mis-form" id="clue-submit-form">
                <label class="wide clue-select-field">发现地点
                    <select id="found-location-select" class="clue-choice-select">
                        <option value="">请选择发现地点</option>
                        <option value="翡翠湖校区二食堂北门">二食堂北门</option>
                        <option value="翡翠湖校区图书馆东侧">图书馆东侧</option>
                        <option value="翡翠湖校区宿舍区楼下">宿舍区楼下</option>
                        <option value="翡翠湖校区教学楼附近">教学楼附近</option>
                        <option value="翡翠湖校区东门附近">东门附近</option>
                        <option value="翡翠湖校区操场看台">操场看台</option>
                        <option value="翡翠湖校区快递站附近">快递站附近</option>
                        <option value="翡翠湖校区校医院门口">校医院门口</option>
                        <option value="翡翠湖校区湖边草坪">湖边草坪</option>
                        <option value="屯溪路校区主楼附近">屯溪路主楼</option>
                        <option value="屯溪路校区南门附近">屯溪路南门</option>
                        <option value="宣城校区食堂附近">宣城食堂</option>
                        <option value="__custom__">新增/自定义发现地点</option>
                    </select>
                    <input name="foundLocation" class="clue-custom-input" placeholder="请输入新的发现地点">
                    <button class="ghost-btn compact add-choice-btn" type="button" data-save-custom="foundLocation">保存到下拉框</button>
                </label>
                <label class="wide clue-select-field">校园区域
                    <select id="found-area-select" class="clue-choice-select">
                        <option value="">请选择校园区域</option>
                        <option value="翡翠湖校区">翡翠湖校区</option>
                        <option value="屯溪路校区">屯溪路校区</option>
                        <option value="宣城校区">宣城校区</option>
                        <option value="教学区">教学区</option>
                        <option value="宿舍区">宿舍区</option>
                        <option value="食堂周边">食堂周边</option>
                        <option value="图书馆周边">图书馆周边</option>
                        <option value="运动场周边">运动场周边</option>
                        <option value="校医院周边">校医院周边</option>
                        <option value="快递站周边">快递站周边</option>
                        <option value="校门周边">校门周边</option>
                        <option value="绿化带/草坪">绿化带/草坪</option>
                        <option value="__custom__">新增/自定义校园区域</option>
                    </select>
                    <input name="foundArea" class="clue-custom-input" placeholder="请输入新的校园区域">
                    <button class="ghost-btn compact add-choice-btn" type="button" data-save-custom="foundArea">保存到下拉框</button>
                </label>
                <label class="clue-time-field">发现时间
                    <span class="clue-time-row">
                        <input name="foundTime" type="datetime-local">
                        <button class="ghost-btn compact clue-now-btn" type="button" id="clue-now-time">当前时间</button>
                    </span>
                </label>
                <label class="wide clue-photo-field">线索照片
                    <input type="hidden" name="photoUrl" required>
                    <div class="clue-upload-panel">
                        <div class="clue-upload-preview">
                            <img id="clue-photo-preview" class="upload-preview-image" src="/uploads/cats/no-photo.svg" alt="线索照片预览">
                        </div>
                        <div class="clue-upload-controls">
                            <input id="clue-photo-file" type="file" accept="image/jpeg,image/png,image/webp,image/gif">
                            <button class="ghost-btn" type="button" id="clue-photo-upload" disabled>上传照片</button>
                            <div class="upload-progress" aria-live="polite">
                                <div class="upload-progress-bar"><span id="clue-upload-progress-bar"></span></div>
                                <strong id="clue-upload-progress-text">请选择照片</strong>
                            </div>
                        </div>
                    </div>
                </label>
                <label>紧急程度<select name="urgencyLevel">
                    <option value="NORMAL">普通</option>
                    <option value="HIGH">较急</option>
                    <option value="URGENT">紧急</option>
                </select></label>
                <label class="wide">描述<textarea name="description" required placeholder="描述猫咪外观、健康状态、停留位置等"></textarea></label>
                <div class="mis-form-actions"><button class="primary-btn">提交线索</button><span id="clue-submit-message"></span></div>
            </form>
        `);
        const photoFileInput = document.getElementById("clue-photo-file");
        const uploadButton = document.getElementById("clue-photo-upload");
        const photoUrlInput = document.querySelector("#clue-submit-form input[name='photoUrl']");
        const preview = document.getElementById("clue-photo-preview");
        const progressBar = document.getElementById("clue-upload-progress-bar");
        const progressText = document.getElementById("clue-upload-progress-text");
        const foundLocationInput = document.querySelector("#clue-submit-form input[name='foundLocation']");
        const foundAreaInput = document.querySelector("#clue-submit-form input[name='foundArea']");
        const foundTimeInput = document.querySelector("#clue-submit-form input[name='foundTime']");
        bindClueSelectField(document.getElementById("found-location-select"), foundLocationInput);
        bindClueSelectField(document.getElementById("found-area-select"), foundAreaInput);
        document.getElementById("clue-now-time")?.addEventListener("click", () => {
            if (!foundTimeInput) return;
            foundTimeInput.value = localDateTimeValue(new Date());
            foundTimeInput.focus();
        });
        let previewObjectUrl = "";
        photoFileInput.addEventListener("change", () => {
            const file = photoFileInput.files?.[0];
            photoUrlInput.value = "";
            progressBar.style.width = "0%";
            progressText.textContent = file ? "待上传" : "请选择照片";
            uploadButton.disabled = !file;
            if (previewObjectUrl) {
                URL.revokeObjectURL(previewObjectUrl);
                previewObjectUrl = "";
            }
            if (!file) {
                preview.src = "/uploads/cats/no-photo.svg";
                return;
            }
            if (!file.type.startsWith("image/")) {
                progressText.textContent = "请选择图片文件";
                uploadButton.disabled = true;
                preview.src = "/uploads/cats/no-photo.svg";
                return;
            }
            previewObjectUrl = URL.createObjectURL(file);
            preview.src = previewObjectUrl;
        });
        uploadButton.addEventListener("click", async () => {
            const file = photoFileInput.files?.[0];
            if (!file) {
                progressText.textContent = "请先选择照片";
                return;
            }
            if (file.size > 5 * 1024 * 1024) {
                progressText.textContent = "照片不能超过 5MB";
                return;
            }
            try {
                uploadButton.disabled = true;
                progressText.textContent = "上传中 0%";
                progressBar.style.width = "0%";
                const result = await uploadFileWithProgress("/api/uploads/clues", file, percent => {
                    progressBar.style.width = `${percent}%`;
                    progressText.textContent = `上传中 ${percent}%`;
                });
                photoUrlInput.value = result.url;
                preview.src = result.url;
                progressBar.style.width = "100%";
                progressText.textContent = "上传完成";
            } catch (error) {
                photoUrlInput.value = "";
                progressText.textContent = error.message;
                uploadButton.disabled = false;
            }
        });
        document.getElementById("clue-submit-form").addEventListener("submit", async event => {
            event.preventDefault();
            const form = new FormData(event.currentTarget);
            const message = document.getElementById("clue-submit-message");
            const body = Object.fromEntries(form.entries());
            body.foundTime = body.foundTime ? `${body.foundTime}:00` : null;
            message.textContent = "";
            try {
                setSubmitting(event.currentTarget, true);
                requireValid(message, [
                    validateCleanText("发现地点", body.foundLocation, 2, 100),
                    body.foundArea ? validateCleanText("校园区域", body.foundArea, 2, 80) : "",
                    body.photoUrl ? "" : "请先选择并上传线索照片",
                    validateCleanText("描述", body.description, 5, 500)
                ]);
                await api("/api/clues", { method: "POST", body: JSON.stringify(body) });
                message.textContent = "提交成功，正在跳转到我的线索";
                window.location.hash = "#/my/clues";
            } catch (error) {
                message.textContent = error.message;
            } finally {
                setSubmitting(event.currentTarget, false);
            }
        });
    }

    async function renderMyClues(route, user) {
        userShell(route, user, `${pageHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载我的线索...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const status = new URLSearchParams(location.hash.split("?")[1] || "").get("status") || "";
            const clues = await api(`/api/my/clues${status ? `?status=${encodeURIComponent(status)}` : ""}`);
            panel.innerHTML = `
                ${summaryCards([
                    { label: "我的线索", value: clues.length },
                    { label: "待核实", value: countRows(clues, item => item.status === "PENDING_VERIFY") },
                    { label: "已核实", value: countRows(clues, item => item.status === "VERIFIED_VALID") },
                    { label: "已建档", value: countRows(clues, item => item.status === "CREATED_CAT") },
                    { label: "无效/重复", value: countRows(clues, item => ["INVALID", "DUPLICATE"].includes(item.status)) }
                ], "business-summary")}
                <div class="mis-filter-row">
                    <select id="my-clue-status">
                        <option value="">全部状态</option>
                        ${Object.keys(clueStatuses).filter(key => !key.startsWith("待")).map(key => `<option value="${key}" ${status === key ? "selected" : ""}>${clueStatuses[key].label}</option>`).join("")}
                    </select>
                </div>
                ${clueCards(clues)}
            `;
            document.getElementById("my-clue-status").addEventListener("change", event => {
                const value = event.target.value;
                window.location.hash = value ? `#/my/clues?status=${value}` : "#/my/clues";
            });
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function clueCards(clues) {
        if (!clues || clues.length === 0) {
            return `<div class="mis-empty">暂无线索记录</div>`;
        }
        return `
            <div class="mis-clue-list">
                ${clues.map(clue => `
                    <article class="mis-clue-card">
                        <img ${imageAttrs(clue.photoUrl, "thumb-image clue-image", "线索照片")}>
                        <div>
                            <div class="mis-row-title"><strong>${escapeHtml(clue.clueNo)}</strong>${tag(clue.status, clueStatuses)}${tag(clue.urgencyLevel, urgencyLabels)}</div>
                            <p>${escapeHtml(clue.foundLocation)} · ${escapeHtml(clue.foundArea || "未填写区域")}</p>
                            <p>${escapeHtml(clue.description)}</p>
                            <small>核实意见：${escapeHtml(clue.verifyComment || "待核实")}</small>
                            <small>生成档案：${clue.createdCatId ? escapeHtml(clue.createdCatId) : "未建档"}</small>
                        </div>
                    </article>
                `).join("")}
            </div>
        `;
    }

    async function renderPublicCats(route, user) {
        userShell(route, user, `${pageHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载可认养猫咪...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            ["keyword", "gender", "healthLevel"].forEach(key => {
                if (params.get(key)) query.set(key, params.get(key));
            });
            const cats = await api(`/api/cats/public${query.toString() ? `?${query}` : ""}`);
            panel.innerHTML = `
                <div class="mis-filter-row">
                    <input id="public-cat-keyword" value="${escapeHtml(params.get("keyword") || "")}" placeholder="搜索昵称、地点、毛色">
                    <select id="public-cat-gender">
                        <option value="">全部性别</option>
                        <option value="M" ${params.get("gender") === "M" ? "selected" : ""}>公</option>
                        <option value="F" ${params.get("gender") === "F" ? "selected" : ""}>母</option>
                        <option value="U" ${params.get("gender") === "U" ? "selected" : ""}>未知</option>
                    </select>
                    <select id="public-cat-health">
                        <option value="">全部健康状态</option>
                        <option value="A" ${params.get("healthLevel") === "A" ? "selected" : ""}>健康</option>
                        <option value="B" ${params.get("healthLevel") === "B" ? "selected" : ""}>需观察</option>
                    </select>
                    <button class="ghost-btn" id="public-cat-search">筛选</button>
                </div>
                <div class="mis-cat-grid showcase">
                    ${cats.map(cat => homeCatCard(cat)).join("") || `<div class="mis-empty">暂无可认养猫咪</div>`}
                </div>
            `;
            document.getElementById("public-cat-search").addEventListener("click", () => {
                const next = new URLSearchParams();
                const keyword = document.getElementById("public-cat-keyword").value;
                const gender = document.getElementById("public-cat-gender").value;
                const health = document.getElementById("public-cat-health").value;
                if (keyword) next.set("keyword", keyword);
                if (gender) next.set("gender", gender);
                if (health) next.set("healthLevel", health);
                window.location.hash = `#/cats${next.toString() ? `?${next}` : ""}`;
            });
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function renderPublicCatDetail(route, user) {
        const catId = (window.location.hash.split("?")[0]).split("/").pop();
        userShell(route, user, `${pageHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载猫咪详情...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const [cat, timeline] = await Promise.all([
                api(`/api/cats/public/${catId}`),
                api(`/api/cats/${catId}/timeline`)
            ]);
            panel.innerHTML = `
                <div class="mis-detail-grid">
                    <a class="cat-detail-img-wrap" href="${escapeHtml(normalizeImageUrl(cat.coverUrl))}" target="_blank">
                        <img ${imageAttrs(cat.coverUrl, "cat-detail-image cat-detail-img mis-detail-photo image-contain", "猫咪照片")}>
                    </a>
                    <div class="mis-detail-content">
                        <h2>${escapeHtml(cat.catName || "待命名")}</h2>
                        <p>${escapeHtml(cat.description || "")}</p>
                        <div class="mis-row-title">${tag(cat.status, catStatusLabels)}${tag(cat.healthLevel, healthLabels)}<span class="mis-tag info">${boolLabel(cat.sterilized, "已绝育", "未绝育")}</span><span class="mis-tag info">${boolLabel(cat.vaccinated, "已疫苗", "未疫苗")}</span></div>
                        <p>性别：${escapeHtml(cat.gender || "-")} · 年龄：${escapeHtml(cat.ageEstimate || "-")} · 毛色：${escapeHtml(cat.color || "-")} · 发现地：${escapeHtml(cat.foundPlace || "-")}</p>
                        <div class="mis-detail-actions">${adoptionAction(cat, user)}</div>
                    </div>
                </div>
                <h3>生命周期时间线</h3>
                ${timelineList(timeline)}
            `;
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function renderAdoptionApply(route, user) {
        const catId = window.location.hash.split("?")[0].split("/").pop();
        userShell(route, user, `${pageHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载申请表...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            if (!canSubmitAdoptionApplication(user)) {
                panel.innerHTML = `<div class="mis-empty">当前账号为${escapeHtml(roleLabels[normalizeRole(user.role)] || normalizeRole(user.role))}，可查看猫咪详情，但不能提交认养申请。</div>`;
                return;
            }
            const cat = await api(`/api/cats/public/${catId}`);
            if (!isAdoptableCat(cat)) {
                panel.innerHTML = `
                    <div class="mis-detail-grid">
                        <a class="cat-detail-img-wrap" href="${escapeHtml(normalizeImageUrl(cat.coverUrl))}" target="_blank">
                            <img ${imageAttrs(cat.coverUrl, "cat-detail-image cat-detail-img mis-detail-photo image-contain", "猫咪照片")}>
                        </a>
                        <div>
                            <h2>${escapeHtml(cat.catName || "待命名")}</h2>
                            <p>${escapeHtml(cat.description || "")}</p>
                            <div class="mis-row-title">${tag(cat.status, catStatusLabels)}${tag(cat.healthLevel, healthLabels)}</div>
                            <button class="ghost-btn" disabled>暂不可认养</button>
                            <a class="ghost-btn" href="#/cats/${escapeHtml(cat.catId)}">返回详情</a>
                        </div>
                    </div>
                    <div class="mis-empty">当前猫咪不是可认养状态，不能提交认养申请。</div>
                `;
                return;
            }
            panel.innerHTML = `
                <div class="mis-detail-grid">
                    <a class="cat-detail-img-wrap" href="${escapeHtml(normalizeImageUrl(cat.coverUrl))}" target="_blank">
                        <img ${imageAttrs(cat.coverUrl, "cat-detail-image cat-detail-img mis-detail-photo image-contain", "猫咪照片")}>
                    </a>
                    <div><h2>${escapeHtml(cat.catName || "待命名")}</h2><p>${escapeHtml(cat.description || "")}</p><div class="mis-row-title">${tag(cat.status, catStatusLabels)}${tag(cat.healthLevel, healthLabels)}</div></div>
                </div>
                <form class="mis-form" id="adoption-apply-form">
                    <label class="wide">居住条件<textarea name="livingCondition" required placeholder="例如：校外稳定租住，已封窗，租期一年以上"></textarea></label>
                    <label class="wide">养宠经验<textarea name="petExperience" required placeholder="是否养过猫，是否了解疫苗、驱虫、绝育"></textarea></label>
                    <label class="wide">家庭/室友支持<textarea name="familySupport" required placeholder="家人或室友是否支持"></textarea></label>
                    <label class="wide">费用承担能力<textarea name="costAffordability" required placeholder="是否能承担猫粮、猫砂、疫苗、绝育、医疗等费用"></textarea></label>
                    <label class="check adoption-consent-check"><input type="checkbox" name="acceptFollowup" required disabled> <span>我已阅读并接受<a href="#" data-reading-doc="followup">定期回访事宜</a></span></label>
                    <label class="wide">承诺内容<textarea name="commitmentText" required>我承诺不弃养，接受定期回访，按要求完成免疫、绝育和医疗照护。</textarea></label>
                    <label class="wide">申请理由<textarea name="extraReason" placeholder="补充说明"></textarea></label>
                    <label class="check adoption-consent-check"><input type="checkbox" name="commitmentAccepted" required disabled> <span>我已阅读并同意<a href="#" data-reading-doc="commitment">认养承诺条款</a></span></label>
                    <div class="mis-form-actions"><button class="primary-btn">提交申请</button><span id="apply-message"></span></div>
                </form>
            `;
            shell.querySelectorAll("[data-reading-doc]").forEach(link => link.addEventListener("click", event => {
                event.preventDefault();
                event.stopPropagation();
                const kind = link.dataset.readingDoc;
                const inputName = kind === "followup" ? "acceptFollowup" : "commitmentAccepted";
                openAdoptionReadingDialog(kind, () => {
                    const checkbox = document.querySelector(`#adoption-apply-form input[name="${inputName}"]`);
                    if (checkbox) {
                        checkbox.disabled = false;
                        checkbox.focus();
                    }
                });
            }));
            document.getElementById("adoption-apply-form").addEventListener("submit", async event => {
                event.preventDefault();
                const form = new FormData(event.currentTarget);
                const body = Object.fromEntries(form.entries());
                body.catId = catId;
                body.acceptFollowup = form.get("acceptFollowup") === "on";
                body.commitmentAccepted = form.get("commitmentAccepted") === "on";
                const message = document.getElementById("apply-message");
                message.textContent = "";
                try {
                    setSubmitting(event.currentTarget, true);
                    requireValid(message, [
                        validateCleanText("居住条件", body.livingCondition, 5, 300),
                        validateCleanText("养宠经验", body.petExperience, 2, 300),
                        validateCleanText("家庭支持", body.familySupport, 2, 300),
                        validateCleanText("经济能力", body.costAffordability, 2, 300),
                        validateCleanText("认养承诺", body.commitmentText, 10, 500),
                        body.acceptFollowup ? "" : "请先阅读并勾选定期回访事宜。",
                        body.commitmentAccepted ? "" : "请先阅读并勾选认养承诺条款。",
                        body.extraReason ? validateCleanText("申请理由", body.extraReason, 2, 500) : ""
                    ]);
                    await api("/api/adoption/applications", { method: "POST", body: JSON.stringify(body) });
                    window.location.hash = "#/my/applications";
                } catch (error) {
                    message.textContent = error.message;
                } finally {
                    setSubmitting(event.currentTarget, false);
                }
            });
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function renderMyApplications(route, user) {
        userShell(route, user, `${pageHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载我的申请...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const status = params.get("status") || "";
            const apps = await api(`/api/my/adoption/applications${status ? `?status=${status}` : ""}`);
            panel.innerHTML = `
                ${summaryCards([
                    { label: "我的申请", value: apps.length },
                    { label: "审核中", value: countRows(apps, item => ["PENDING_INITIAL", "PENDING_FINAL"].includes(item.status)) },
                    { label: "待交接", value: countRows(apps, item => item.status === "PENDING_HANDOVER") },
                    { label: "已交接", value: countRows(apps, item => item.status === "HANDED_OVER") },
                    { label: "已拒绝/取消", value: countRows(apps, item => ["INITIAL_REJECTED", "FINAL_REJECTED", "CANCELLED"].includes(item.status)) }
                ], "business-summary")}
                <div class="mis-filter-row"><select id="my-app-status"><option value="">全部状态</option>${Object.keys(applicationStatusLabels).map(key => `<option value="${key}" ${status === key ? "selected" : ""}>${applicationStatusLabels[key].label}</option>`).join("")}</select><button class="ghost-btn" id="my-app-filter">筛选</button></div>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>申请编号</th><th>猫咪</th><th>时间</th><th>评分</th><th>风险</th><th>状态</th><th>操作</th></tr></thead><tbody>
                    ${apps.map(app => `<tr><td>${escapeHtml(app.applicationId)}</td><td>${escapeHtml(app.catName || app.catId)}</td><td>${escapeHtml((app.appliedAt || "").replace("T", " "))}</td><td>${scoreBar(app.score)}</td><td>${tag(app.riskLevel, riskLabels)}</td><td>${tag(app.status, applicationStatusLabels)}</td><td><button class="ghost-btn" data-my-app="${escapeHtml(app.applicationId)}">详情</button>${app.status === "PENDING_INITIAL" ? `<button class="ghost-btn" data-cancel-app="${escapeHtml(app.applicationId)}">取消</button>` : ""}</td></tr>`).join("")}
                </tbody></table></div>
            `;
            document.getElementById("my-app-filter").addEventListener("click", () => {
                const value = document.getElementById("my-app-status").value;
                window.location.hash = value ? `#/my/applications?status=${value}` : "#/my/applications";
            });
            shell.querySelectorAll("[data-my-app]").forEach(button => button.addEventListener("click", async () => {
                const detail = await api(`/api/my/adoption/applications/${button.dataset.myApp}`);
                alert(applicationDetailText(detail));
            }));
            shell.querySelectorAll("[data-cancel-app]").forEach(button => button.addEventListener("click", async () => {
                const cancelReason = prompt("请输入取消原因", "暂不认养");
                if (!cancelReason) return;
                await api(`/api/my/adoption/applications/${button.dataset.cancelApp}/cancel`, { method: "PUT", body: JSON.stringify({ cancelReason }) });
                renderMyApplications(route, user);
            }));
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function scoreBar(score) {
        const value = score == null ? 0 : score;
        const tone = value >= 80 ? "success" : value >= 60 ? "warning" : "danger";
        return `<span class="mis-score ${tone}"><i style="width:${value}%"></i><b>${value}</b></span>`;
    }

    function timelineList(events) {
        if (!events || events.length === 0) {
            return `<div class="mis-empty">暂无时间线记录</div>`;
        }
        return `<ol class="mis-timeline">${events.map(event => `<li><strong>${escapeHtml(event.title || event.eventType)}</strong><span>${escapeHtml(event.description || "")}</span><small>${escapeHtml((event.eventTime || "").replace("T", " "))}</small></li>`).join("")}</ol>`;
    }

    function medicalRecordRows(records, emptyText = "暂无医疗记录") {
        return (records || []).map(item => `<tr>
            <td>${escapeHtml(item.medicalId)}</td>
            <td><a href="#/admin/cats/${escapeHtml(item.catId)}">${escapeHtml(item.catId)}</a></td>
            <td>${escapeHtml(item.checkDate || "-")}</td>
            <td>${tag(item.healthLevel, healthLabels)}</td>
            <td>${escapeHtml(item.treatment || item.doctorNote || "-")}</td>
            <td>${escapeHtml(item.hospital || "-")}</td>
        </tr>`).join("") || `<tr><td colspan="6"><div class="mis-empty">${escapeHtml(emptyText)}</div></td></tr>`;
    }

    async function renderHospitalPortal(route, user) {
        userShell(route, user, `${pageHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载医疗协作数据...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const [summary, medicalCats, observingCats, records] = await Promise.all([
                api("/api/admin/hospital/summary"),
                api("/api/admin/cats?status=MEDICAL").catch(() => []),
                api("/api/admin/cats?status=OBSERVING").catch(() => []),
                api("/api/admin/hospital/records?limit=8").catch(() => [])
            ]);
            panel.innerHTML = `
                ${summaryCards([
                    { label: "医疗中猫咪", value: summary.medicalCatCount },
                    { label: "观察中猫咪", value: summary.observingCatCount },
                    { label: "健康异常记录", value: summary.abnormalRecordCount },
                    { label: "本院录入记录", value: summary.myRecordCount }
                ], "business-summary")}
                <div class="mis-section-head"><h2>医院协作入口</h2><span>${escapeHtml(user.college || "合作医院")} · ${escapeHtml(user.userName)}</span></div>
                <div class="role-actions">
                    <a class="primary-btn" href="#/admin/hospital">进入医院后台首页</a>
                    <a class="ghost-btn" href="#/admin/medical">维护医疗记录</a>
                    <a class="ghost-btn" href="#/admin/medical?status=MEDICAL">处理医疗中猫咪</a>
                    <a class="ghost-btn" href="#/admin/medical?status=OBSERVING">查看观察中猫咪</a>
                </div>
                <h3>待医疗猫咪</h3>
                <div class="mis-cat-grid showcase">
                    ${(medicalCats || []).slice(0, 4).map(cat => homeCatCard(cat).replaceAll("#/cats/", "#/admin/cats/")).join("") || `<div class="mis-empty">暂无医疗中猫咪</div>`}
                </div>
                <h3>观察中猫咪</h3>
                <div class="mis-cat-grid showcase">
                    ${(observingCats || []).slice(0, 4).map(cat => homeCatCard(cat).replaceAll("#/cats/", "#/admin/cats/")).join("") || `<div class="mis-empty">暂无观察中猫咪</div>`}
                </div>
                <h3>最近医疗记录</h3>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>编号</th><th>猫咪</th><th>日期</th><th>健康</th><th>说明</th><th>医院</th></tr></thead><tbody>${medicalRecordRows(records)}</tbody></table></div>
            `;
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function renderAdminDashboard(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-stat-grid"><div class="mis-loading">正在加载统计...</div></section>`);
        const grid = shell.querySelector(".mis-stat-grid");
        let distribution;
        grid.insertAdjacentHTML("afterend", `<section class="mis-table-panel" id="dashboard-distribution"><div class="mis-loading">Loading distribution...</div></section>`);
        distribution = document.getElementById("dashboard-distribution");
        try {
            const [summary, catStatus, applicationStatus, followupStatus, warningType] = await Promise.all([
                api("/api/admin/dashboard/summary"),
                api("/api/admin/dashboard/cat-status"),
                api("/api/admin/dashboard/application-status"),
                api("/api/admin/dashboard/followup-status"),
                api("/api/admin/dashboard/warning-type")
            ]);
            grid.innerHTML = `
                <article><span>猫咪总数</span><strong>${summary.catCount}</strong></article>
                <article><span>可认养数量</span><strong>${summary.adoptableCount}</strong></article>
                <article><span>观察中数量</span><strong>${summary.observingCount}</strong></article>
                <article><span>医疗中数量</span><strong>${summary.medicalCount}</strong></article>
                <article><span>暂停认养数量</span><strong>${summary.suspendedCount}</strong></article>
                <article><span>待核实线索</span><strong>${summary.pendingClueCount}</strong></article>
                <article><span>已建档线索</span><strong>${summary.createdCatClueCount}</strong></article>
                <article><span>待初审申请</span><strong>${summary.pendingInitialApplicationCount}</strong></article>
                <article><span>待终审申请</span><strong>${summary.pendingFinalApplicationCount}</strong></article>
                <article><span>待交接申请</span><strong>${summary.pendingHandoverApplicationCount}</strong></article>
                <article><span>已交接申请</span><strong>${summary.handedOverApplicationCount}</strong></article>
                <article><span>待回访任务</span><strong>${summary.pendingFollowupTaskCount}</strong></article>
                <article><span>已完成回访</span><strong>${summary.completedFollowupTaskCount}</strong></article>
                <article><span>逾期回访</span><strong>${summary.overdueFollowupTaskCount}</strong></article>
                <article><span>异常回访</span><strong>${summary.abnormalFollowupTaskCount}</strong></article>
                <article><span>待处理预警</span><strong>${summary.pendingWarningCount}</strong></article>
                <article><span>已处理预警</span><strong>${summary.handledWarningCount}</strong></article>
                <article><span>回访完成率</span><strong>${summary.followupCompletionRate}%</strong></article>
                <article><span>回访中猫咪</span><strong>${summary.followingCatCount}</strong></article>
                <article><span>高风险申请</span><strong>${summary.highRiskApplicationCount}</strong></article>
                <article><span>未读消息</span><strong>${summary.unreadMessageCount || 0}</strong></article>
            `;
            distribution.innerHTML = `
                <div class="mis-section-head"><h2>业务分布</h2><span>按状态聚合的中期验收指标</span></div>
                <div class="mis-placeholder-grid">
                    ${distributionCard("猫咪状态", catStatus)}
                    ${distributionCard("申请状态", applicationStatus)}
                    ${distributionCard("回访状态", followupStatus)}
                    ${distributionCard("预警类型", warningType)}
                </div>
            `;
        } catch (error) {
            grid.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
            if (distribution) {
                distribution.innerHTML = "";
            }
        }
    }

    function distributionCard(title, rows) {
        return `<article><strong>${escapeHtml(title)}</strong>${(rows || []).map(row => `<span>${escapeHtml(row.name || "-")}：${row.count}</span>`).join("") || "<span>暂无数据</span>"}</article>`;
    }

    function summaryCards(items, className = "") {
        return `
            <section class="mis-stat-grid ${className}">
                ${items.map(item => `<article><span>${escapeHtml(item.label)}</span><strong>${escapeHtml(item.value)}</strong></article>`).join("")}
            </section>
        `;
    }

    function countRows(rows, predicate) {
        return (Array.isArray(rows) ? rows : []).filter(predicate).length;
    }

    async function renderAdminClues(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载线索...</div></section>`);
        await loadAdminClues();
    }

    async function renderAdminCats(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载猫咪档案...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            ["status", "healthLevel", "keyword"].forEach(key => {
                if (params.get(key)) query.set(key, params.get(key));
            });
            const cats = await api(`/api/admin/cats${query.toString() ? `?${query}` : ""}`);
            panel.innerHTML = `
                <div class="mis-filter-row">
                    <select id="admin-cat-status"><option value="">全部状态</option>${Object.keys(catStatusLabels).map(key => `<option value="${key}" ${params.get("status") === key ? "selected" : ""}>${catStatusLabels[key].label}</option>`).join("")}</select>
                    <select id="admin-cat-health"><option value="">全部健康</option><option value="A" ${params.get("healthLevel") === "A" ? "selected" : ""}>健康</option><option value="B" ${params.get("healthLevel") === "B" ? "selected" : ""}>需观察</option><option value="C" ${params.get("healthLevel") === "C" ? "selected" : ""}>需治疗</option></select>
                    <input id="admin-cat-keyword" value="${escapeHtml(params.get("keyword") || "")}" placeholder="搜索编号、昵称、地点">
                    <button class="ghost-btn" id="admin-cat-search">筛选</button>
                    <button class="primary-btn" id="admin-cat-create">新增猫咪</button>
                    ${user.role === "ADMIN" ? `<button class="primary-btn" id="admin-cat-export">导出 CSV</button>` : ""}
                </div>
                <div class="mis-table-wrap">
                    <table class="mis-table">
                        <thead><tr><th>照片</th><th>编号</th><th>昵称</th><th>性别</th><th>健康</th><th>疫苗</th><th>绝育</th><th>状态</th><th>更新时间</th><th>操作</th></tr></thead>
                        <tbody>
                            ${cats.map(cat => `
                                <tr>
                                    <td><img ${imageAttrs(cat.coverUrl, "thumb-image mis-thumb", "猫咪照片")}></td>
                                    <td>${escapeHtml(cat.catId)}</td>
                                    <td>${escapeHtml(cat.catName || "待命名")}</td>
                                    <td>${escapeHtml(cat.gender || "-")}</td>
                                    <td>${tag(cat.healthLevel, healthLabels)}</td>
                                    <td>${boolLabel(cat.vaccinated, "已疫苗", "未疫苗")}</td>
                                    <td>${boolLabel(cat.sterilized, "已绝育", "未绝育")}</td>
                                    <td>${tag(cat.status, catStatusLabels)}</td>
                                    <td>${escapeHtml((cat.updatedAt || "").replace("T", " "))}</td>
                                    <td>
                                        <button class="ghost-btn" data-cat-detail="${escapeHtml(cat.catId)}">详情</button>
                                        <button class="ghost-btn" data-cat-edit="${escapeHtml(cat.catId)}">编辑</button>
                                        <button class="ghost-btn" data-cat-status="${escapeHtml(cat.catId)}">改状态</button>
                                        ${user.role === "ADMIN" ? `<button class="ghost-btn" data-cat-delete="${escapeHtml(cat.catId)}">归档</button>` : ""}
                                        <a class="ghost-btn" href="#/admin/medical?catId=${escapeHtml(cat.catId)}">医疗</a>
                                    </td>
                                </tr>
                            `).join("")}
                        </tbody>
                    </table>
                </div>
            `;
            bindAdminCatEvents(cats, user);
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function bindAdminCatEvents(cats, user) {
        document.getElementById("admin-cat-search").addEventListener("click", () => {
            const query = new URLSearchParams();
            const status = document.getElementById("admin-cat-status").value;
            const health = document.getElementById("admin-cat-health").value;
            const keyword = document.getElementById("admin-cat-keyword").value;
            if (status) query.set("status", status);
            if (health) query.set("healthLevel", health);
            if (keyword) query.set("keyword", keyword);
            window.location.hash = `#/admin/cats${query.toString() ? `?${query}` : ""}`;
        });
        document.getElementById("admin-cat-export")?.addEventListener("click", async () => {
            try {
                const params = new URLSearchParams(location.hash.split("?")[1] || "");
                await downloadCsv(`/api/admin/export/cats${params.toString() ? `?${params}` : ""}`, "cats.csv");
            } catch (error) {
                alert(error.message);
            }
        });
        document.getElementById("admin-cat-create")?.addEventListener("click", async () => saveAdminCat(null));
        shell.querySelectorAll("[data-cat-detail]").forEach(button => button.addEventListener("click", () => {
            window.location.hash = `#/admin/cats/${button.dataset.catDetail}`;
        }));
        shell.querySelectorAll("[data-cat-status]").forEach(button => button.addEventListener("click", async () => {
            const targetStatus = prompt("目标状态：OBSERVING / MEDICAL / ADOPTABLE / SUSPENDED", "ADOPTABLE");
            if (!targetStatus) return;
            const reason = prompt("请输入状态变更原因");
            if (!reason) return;
            try {
                await api(`/api/admin/cats/${button.dataset.catStatus}/status`, {
                    method: "PUT",
                    body: JSON.stringify({ targetStatus: targetStatus.toUpperCase(), reason })
                });
                render();
            } catch (error) {
                alert(error.message);
            }
        }));
        shell.querySelectorAll("[data-cat-edit]").forEach(button => button.addEventListener("click", async () => {
            const cat = cats.find(item => item.catId === button.dataset.catEdit);
            await saveAdminCat(cat);
        }));
        shell.querySelectorAll("[data-cat-delete]").forEach(button => button.addEventListener("click", async () => {
            if (!confirm("确认归档该猫咪档案？已有申请/协议/回访的猫咪会被后端拒绝。")) return;
            try {
                await api(`/api/admin/cats/${button.dataset.catDelete}`, { method: "DELETE" });
                render();
            } catch (error) {
                alert(error.message);
            }
        }));
    }

    async function saveAdminCat(cat) {
        const catName = prompt("猫咪昵称", cat?.catName || "待命名");
        if (catName === null) return;
        const foundPlace = prompt("发现地点", cat?.foundPlace || "校园待补充地点");
        if (foundPlace === null) return;
        const color = prompt("毛色", cat?.color || "待观察");
        if (color === null) return;
        const ageEstimate = prompt("年龄估计", cat?.ageEstimate || "待估计");
        if (ageEstimate === null) return;
        const personality = prompt("性格描述", cat?.personality || "待观察") || "待观察";
        const description = prompt("描述", cat?.description || "校园流浪猫档案，后续补充观察记录。") || "";
        const validationMessage = [
            validateCleanText("猫咪名称", catName, 1, 20),
            validateCleanText("发现地点", foundPlace, 2, 100),
            validateCleanText("毛色", color, 1, 30),
            validateCleanText("年龄估计", ageEstimate, 1, 30),
            validateCleanText("性格描述", personality, 2, 200),
            description ? validateCleanText("猫咪描述", description, 2, 500) : ""
        ].find(Boolean);
        if (validationMessage) {
            alert(validationMessage);
            return;
        }
        const body = {
            catName,
            foundPlace,
            foundDate: cat?.foundDate || new Date().toISOString().slice(0, 10),
            gender: cat?.gender || "U",
            color,
            ageEstimate,
            personality,
            healthLevel: cat?.healthLevel || "B",
            sterilized: Boolean(cat?.sterilized),
            vaccinated: Boolean(cat?.vaccinated),
            status: cat?.status || "OBSERVING",
            coverUrl: cat?.coverUrl || "/uploads/cats/no-photo.svg",
            tags: splitTags(cat?.tags),
            description
        };
        await api(cat ? `/api/admin/cats/${cat.catId}` : "/api/admin/cats", {
            method: cat ? "PUT" : "POST",
            body: JSON.stringify(body)
        });
        render();
    }

    async function renderAdminCatDetail(route, user) {
        const catId = window.location.hash.split("?")[0].split("/").pop();
        adminShell(route, user, `${adminHero({ ...route, title: "猫咪档案详情" })}<section class="mis-table-panel"><div class="mis-loading">正在加载猫咪档案...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const [cat, timeline, medical] = await Promise.all([
                api(`/api/admin/cats/${catId}`),
                api(`/api/admin/cats/${catId}/timeline`),
                api(`/api/admin/cats/${catId}/medical-records`)
            ]);
            panel.innerHTML = `
                <div class="mis-detail-grid">
                    <a class="cat-detail-img-wrap" href="${escapeHtml(normalizeImageUrl(cat.coverUrl))}" target="_blank">
                        <img ${imageAttrs(cat.coverUrl, "cat-detail-image cat-detail-img mis-detail-photo image-contain", "猫咪照片")}>
                    </a>
                    <div>
                        <h2>${escapeHtml(cat.catName || "待命名")} <small>${escapeHtml(cat.catId)}</small></h2>
                        <div class="mis-row-title">${tag(cat.status, catStatusLabels)}${tag(cat.healthLevel, healthLabels)}<span class="mis-tag info">${boolLabel(cat.vaccinated, "已疫苗", "未疫苗")}</span><span class="mis-tag info">${boolLabel(cat.sterilized, "已绝育", "未绝育")}</span></div>
                        <p>${escapeHtml(cat.foundPlace || "-")} · ${escapeHtml(cat.color || "-")} · ${escapeHtml(cat.ageEstimate || "-")}</p>
                        <p>${escapeHtml(cat.description || "")}</p>
                        <a class="ghost-btn" href="#/admin/medical?catId=${escapeHtml(cat.catId)}">维护医疗记录</a>
                    </div>
                </div>
                <h3>医疗记录时间线</h3>${timelineList(medical.map(item => ({ title: item.healthLevel, description: item.treatment || item.doctorNote, eventTime: item.createdAt })))}
                <h3>生命周期时间线</h3>${timelineList(timeline)}
            `;
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function renderAdminHospital(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载医院后台...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const [summary, medicalCats, observingCats, records] = await Promise.all([
                api("/api/admin/hospital/summary"),
                api("/api/admin/cats?status=MEDICAL").catch(() => []),
                api("/api/admin/cats?status=OBSERVING").catch(() => []),
                api("/api/admin/hospital/records?limit=12").catch(() => [])
            ]);
            panel.innerHTML = `
                ${summaryCards([
                    { label: "医疗中猫咪", value: summary.medicalCatCount },
                    { label: "观察中猫咪", value: summary.observingCatCount },
                    { label: "异常医疗记录", value: summary.abnormalRecordCount },
                    { label: "本院录入记录", value: summary.myRecordCount },
                    { label: "待疫苗猫咪", value: summary.pendingVaccineCount },
                    { label: "待绝育猫咪", value: summary.pendingSterilizationCount }
                ], "business-summary")}
                <div class="mis-filter-row">
                    <a class="primary-btn" href="#/admin/medical?status=MEDICAL">处理医疗中猫咪</a>
                    <a class="ghost-btn" href="#/admin/medical?status=OBSERVING">查看观察中猫咪</a>
                    <a class="ghost-btn" href="#/admin/medical">新增医疗记录</a>
                    <a class="ghost-btn" href="#/hospital">返回医疗协作前台</a>
                </div>
                <div class="mis-placeholder-grid">
                    <article><strong>待医疗</strong><span>${(medicalCats || []).slice(0, 5).map(cat => `${cat.catName || cat.catId}(${cat.catId})`).join("、") || "暂无医疗中猫咪"}</span></article>
                    <article><strong>需观察</strong><span>${(observingCats || []).slice(0, 5).map(cat => `${cat.catName || cat.catId}(${cat.catId})`).join("、") || "暂无观察中猫咪"}</span></article>
                    <article><strong>工作口径</strong><span>体检、疫苗、绝育、治疗记录由医院用户录入；异常记录会联动猫咪进入医疗中状态。</span></article>
                </div>
                <h3>最近医疗记录</h3>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>编号</th><th>猫咪</th><th>日期</th><th>健康</th><th>说明</th><th>医院</th></tr></thead><tbody>${medicalRecordRows(records)}</tbody></table></div>
            `;
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function renderAdminMedical(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载医疗工作台...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const selectedCatId = params.get("catId") || "";
            const statusFilter = params.get("status") || "MEDICAL";
            const summary = await api("/api/admin/hospital/summary").catch(() => ({}));
            let cats = await api(`/api/admin/cats?status=${encodeURIComponent(statusFilter)}`).catch(() => []);
            if (!cats.length && !isHospitalUser(user)) {
                cats = await api("/api/admin/cats");
            }
            const currentCat = selectedCatId || (cats[0] ? cats[0].catId : "");
            const records = currentCat ? await api(`/api/admin/cats/${currentCat}/medical-records`) : [];
            panel.innerHTML = `
                ${summaryCards([
                    { label: "医疗中猫咪", value: summary.medicalCatCount ?? 0 },
                    { label: "观察中猫咪", value: summary.observingCatCount ?? 0 },
                    { label: "异常记录", value: summary.abnormalRecordCount ?? 0 },
                    { label: "本院记录", value: summary.myRecordCount ?? 0 },
                    { label: "待疫苗", value: summary.pendingVaccineCount ?? 0 },
                    { label: "待绝育", value: summary.pendingSterilizationCount ?? 0 }
                ], "business-summary")}
                <div class="mis-filter-row">
                    <select id="medical-status-filter">
                        <option value="MEDICAL" ${statusFilter === "MEDICAL" ? "selected" : ""}>医疗中猫咪</option>
                        <option value="OBSERVING" ${statusFilter === "OBSERVING" ? "selected" : ""}>观察中猫咪</option>
                        <option value="ADOPTABLE" ${statusFilter === "ADOPTABLE" ? "selected" : ""}>可认养猫咪</option>
                    </select>
                    ${cats.length ? `<select id="medical-cat-select">${cats.map(cat => `<option value="${cat.catId}" ${currentCat === cat.catId ? "selected" : ""}>${cat.catId} · ${escapeHtml(cat.catName || "待命名")} · ${catStatusLabels[cat.status]?.label || cat.status}</option>`).join("")}</select>` : `<input id="medical-cat-select" value="${escapeHtml(currentCat)}" placeholder="输入猫咪编号，如 CAT260501001">`}
                    <button class="ghost-btn" id="medical-cat-jump">查看</button>
                    <a class="ghost-btn" href="#/admin/hospital">医院首页</a>
                </div>
                <form class="mis-form" id="medical-form">
                    <input type="hidden" name="medicalId" value="">
                    <label>记录类型<select name="recordType"><option value="CHECKUP">体检</option><option value="VACCINE">疫苗</option><option value="STERILIZATION">绝育</option><option value="TREATMENT">治疗</option><option value="OTHER">其他</option></select></label>
                    <label>记录日期<input name="recordDate" type="date"></label>
                    <label>健康结果<select name="healthResult"><option value="HEALTHY">健康</option><option value="OBSERVE">需观察</option><option value="SICK">患病</option><option value="SERIOUS">严重异常</option></select></label>
                    <label>疫苗状态<select name="vaccineStatus"><option value="UNKNOWN">未知</option><option value="NOT_VACCINATED">未疫苗</option><option value="PARTIAL">部分接种</option><option value="VACCINATED">已疫苗</option></select></label>
                    <label>绝育状态<select name="sterilizedStatus"><option value="UNKNOWN">未知</option><option value="NOT_STERILIZED">未绝育</option><option value="STERILIZED">已绝育</option><option value="NOT_SUITABLE">暂不适合</option></select></label>
                    <label>费用<input name="cost" type="number" step="0.01"></label>
                    <label>附件 URL<input name="attachmentUrl" placeholder="/uploads/cats/cat_01_01.jpg"></label>
                    <label class="wide">医疗说明<textarea name="description" required></textarea></label>
                    <label class="check"><input name="abnormalFlag" type="checkbox"> 标记异常</label>
                    <div class="mis-form-actions"><button class="primary-btn" id="medical-submit">新增医疗记录</button><button class="ghost-btn" id="medical-reset" type="button">清空</button><span id="medical-message"></span></div>
                </form>
                <h3>当前猫咪医疗记录</h3>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>编号</th><th>日期</th><th>健康</th><th>说明</th><th>医院</th><th>操作</th></tr></thead><tbody>
                    ${(records || []).map(item => `<tr>
                        <td>${escapeHtml(item.medicalId)}</td>
                        <td>${escapeHtml(item.checkDate || "-")}</td>
                        <td>${tag(item.healthLevel, healthLabels)}</td>
                        <td>${escapeHtml(item.treatment || item.doctorNote || "-")}</td>
                        <td>${escapeHtml(item.hospital || "-")}</td>
                        <td>
                            <button class="ghost-btn" data-medical-edit="${escapeHtml(item.medicalId)}">回填编辑</button>
                            <button class="ghost-btn" data-medical-void="${escapeHtml(item.medicalId)}">作废</button>
                        </td>
                    </tr>`).join("") || `<tr><td colspan="6"><div class="mis-empty">暂无医疗记录</div></td></tr>`}
                </tbody></table></div>
            `;
            document.getElementById("medical-cat-jump").addEventListener("click", () => {
                const status = document.getElementById("medical-status-filter").value;
                window.location.hash = `#/admin/medical?status=${encodeURIComponent(status)}&catId=${encodeURIComponent(document.getElementById("medical-cat-select").value)}`;
            });
            document.getElementById("medical-status-filter").addEventListener("change", event => {
                window.location.hash = `#/admin/medical?status=${encodeURIComponent(event.currentTarget.value)}`;
            });
            document.getElementById("medical-reset").addEventListener("click", () => {
                const form = document.getElementById("medical-form");
                form.reset();
                form.elements.medicalId.value = "";
                document.getElementById("medical-submit").textContent = "新增医疗记录";
                document.getElementById("medical-message").textContent = "";
            });
            document.getElementById("medical-form").addEventListener("submit", async event => {
                event.preventDefault();
                const form = new FormData(event.currentTarget);
                const body = Object.fromEntries(form.entries());
                const medicalId = body.medicalId;
                delete body.medicalId;
                body.abnormalFlag = form.get("abnormalFlag") === "on";
                body.cost = body.cost ? Number(body.cost) : null;
                const message = document.getElementById("medical-message");
                message.textContent = "";
                try {
                    setSubmitting(event.currentTarget, true);
                    requireValid(message, [
                        validateCleanText("医疗说明", body.description, 5, 500),
                        body.cost !== null && body.cost < 0 ? "费用不能为负数" : ""
                    ]);
                    if (medicalId) {
                        await api(`/api/admin/medical-records/${medicalId}`, { method: "PUT", body: JSON.stringify(body) });
                        message.textContent = "医疗记录已更新";
                    } else {
                        await api(`/api/admin/cats/${currentCat}/medical-records`, { method: "POST", body: JSON.stringify(body) });
                        message.textContent = "医疗记录已保存";
                    }
                    renderAdminMedical(route, user);
                } catch (error) {
                    message.textContent = error.message;
                } finally {
                    setSubmitting(event.currentTarget, false);
                }
            });
            shell.querySelectorAll("[data-medical-edit]").forEach(button => button.addEventListener("click", () => {
                const record = records.find(item => item.medicalId === button.dataset.medicalEdit);
                if (!record) {
                    return;
                }
                const form = document.getElementById("medical-form");
                form.elements.medicalId.value = record.medicalId;
                form.elements.recordType.value = "CHECKUP";
                form.elements.recordDate.value = record.checkDate || new Date().toISOString().slice(0, 10);
                form.elements.healthResult.value = record.healthLevel === "A" ? "HEALTHY" : record.healthLevel === "C" ? "SICK" : "OBSERVE";
                form.elements.vaccineStatus.value = record.vaccinated ? "VACCINATED" : "UNKNOWN";
                form.elements.sterilizedStatus.value = record.sterilized ? "STERILIZED" : "UNKNOWN";
                form.elements.cost.value = "";
                form.elements.attachmentUrl.value = "";
                form.elements.description.value = record.treatment || record.doctorNote || "";
                form.elements.abnormalFlag.checked = record.healthLevel === "C";
                document.getElementById("medical-submit").textContent = "更新医疗记录";
                document.getElementById("medical-message").textContent = `正在编辑 ${record.medicalId}`;
                form.scrollIntoView({ behavior: "smooth", block: "start" });
            }));
            shell.querySelectorAll("[data-medical-void]").forEach(button => button.addEventListener("click", async () => {
                if (!confirm("确认作废该医疗记录？作废后不会物理删除。")) return;
                const reason = prompt("请输入作废原因");
                if (!reason) return;
                try {
                    await api(`/api/admin/medical-records/${button.dataset.medicalVoid}/void`, {
                        method: "PUT",
                        body: JSON.stringify({ reason })
                    });
                    renderAdminMedical(route, user);
                } catch (error) {
                    alert(error.message);
                }
            }));
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function renderAdminAdoptionAudits(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载认养申请...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            ["status", "riskLevel", "keyword"].forEach(key => {
                if (params.get(key)) query.set(key, params.get(key));
            });
            const apps = await api(`/api/admin/adoption/applications${query.toString() ? `?${query}` : ""}`);
            panel.innerHTML = `
                ${summaryCards([
                    { label: "申请总数", value: apps.length },
                    { label: "待初审", value: countRows(apps, item => item.status === "PENDING_INITIAL") },
                    { label: "待终审", value: countRows(apps, item => item.status === "PENDING_FINAL") },
                    { label: "待交接", value: countRows(apps, item => item.status === "PENDING_HANDOVER") },
                    { label: "已交接", value: countRows(apps, item => item.status === "HANDED_OVER") }
                ], "business-summary")}
                <div class="mis-filter-row">
                    <select id="admin-app-status"><option value="">默认状态</option>${Object.keys(applicationStatusLabels).map(key => `<option value="${key}" ${params.get("status") === key ? "selected" : ""}>${applicationStatusLabels[key].label}</option>`).join("")}</select>
                    <select id="admin-app-risk"><option value="">全部风险</option>${Object.keys(riskLabels).map(key => `<option value="${key}" ${params.get("riskLevel") === key ? "selected" : ""}>${riskLabels[key].label}</option>`).join("")}</select>
                    <input id="admin-app-keyword" value="${escapeHtml(params.get("keyword") || "")}" placeholder="搜索申请编号、猫咪、申请人">
                    <button class="ghost-btn" id="admin-app-search">筛选</button>
                    ${user.role === "ADMIN" ? `<button class="primary-btn" id="admin-app-export">导出 CSV</button>` : ""}
                </div>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>申请编号</th><th>猫咪</th><th>申请人</th><th>时间</th><th>评分</th><th>风险</th><th>状态</th><th>操作</th></tr></thead><tbody>
                    ${apps.map(app => `<tr><td>${escapeHtml(app.applicationId)}</td><td>${escapeHtml(app.catName || app.catId)}</td><td>${escapeHtml(app.userName || "-")}</td><td>${escapeHtml((app.appliedAt || "").replace("T", " "))}</td><td>${scoreBar(app.score)}</td><td>${tag(app.riskLevel, riskLabels)}</td><td>${tag(app.status, applicationStatusLabels)}</td><td><button class="ghost-btn" data-admin-app="${escapeHtml(app.applicationId)}">详情</button>${user.role === "VOLUNTEER" && app.status === "PENDING_INITIAL" ? `<button class="primary-btn" data-initial="${escapeHtml(app.applicationId)}">初审</button>` : ""}${user.role === "ADMIN" && app.status === "PENDING_FINAL" ? `<button class="primary-btn" data-final="${escapeHtml(app.applicationId)}">终审</button>` : ""}${user.role === "ADMIN" && app.status !== "HANDED_OVER" ? `<button class="ghost-btn" data-void-app="${escapeHtml(app.applicationId)}">作废</button>` : ""}</td></tr>`).join("")}
                </tbody></table></div>
            `;
            document.getElementById("admin-app-search").addEventListener("click", () => {
                const next = new URLSearchParams();
                const status = document.getElementById("admin-app-status").value;
                const risk = document.getElementById("admin-app-risk").value;
                const keyword = document.getElementById("admin-app-keyword").value;
                if (status) next.set("status", status);
                if (risk) next.set("riskLevel", risk);
                if (keyword) next.set("keyword", keyword);
                window.location.hash = `#/admin/adoption/audits${next.toString() ? `?${next}` : ""}`;
            });
            document.getElementById("admin-app-export")?.addEventListener("click", async () => {
                try {
                    const params = new URLSearchParams(location.hash.split("?")[1] || "");
                    await downloadCsv(`/api/admin/export/applications${params.toString() ? `?${params}` : ""}`, "applications.csv");
                } catch (error) {
                    alert(error.message);
                }
            });
            shell.querySelectorAll("[data-admin-app]").forEach(button => button.addEventListener("click", async () => {
                const detail = await api(`/api/admin/adoption/applications/${button.dataset.adminApp}`);
                alert(applicationDetailText(detail));
            }));
            shell.querySelectorAll("[data-initial]").forEach(button => button.addEventListener("click", () => auditApplication(button.dataset.initial, "initial")));
            shell.querySelectorAll("[data-final]").forEach(button => button.addEventListener("click", () => auditApplication(button.dataset.final, "final")));
            shell.querySelectorAll("[data-void-app]").forEach(button => button.addEventListener("click", async () => {
                if (!confirm("确认作废该认养申请？已交接申请会被后端拒绝。")) return;
                const reason = prompt("请输入作废原因");
                if (!reason) return;
                try {
                    await api(`/api/admin/adoption/applications/${button.dataset.voidApp}/void`, {
                        method: "PUT",
                        body: JSON.stringify({ reason })
                    });
                    renderAdminAdoptionAudits(route, user);
                } catch (error) {
                    alert(error.message);
                }
            }));
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function auditApplication(applicationId, stage) {
        const auditResult = prompt("审核结果：APPROVED / REJECTED", "APPROVED");
        if (!auditResult) return;
        const auditComment = prompt("审核意见");
        if (!auditComment) return;
        try {
            await api(`/api/admin/adoption/applications/${applicationId}/${stage}-audit`, {
                method: "PUT",
                body: JSON.stringify({ auditResult: auditResult.toUpperCase(), auditComment })
            });
            render();
        } catch (error) {
            alert(error.message);
        }
    }

    function applicationDetailText(app) {
        const audits = (app.audits || []).map(audit => `${audit.auditStage} ${audit.auditResult}: ${audit.auditComment}`).join("\n");
        const agreement = app.agreementInfo
            ? `协议编号：${app.agreementInfo.agreementNo || "-"}\n协议状态：${agreementStatusLabels[app.agreementInfo.status]?.label || app.agreementInfo.status}\n交接时间：${(app.agreementInfo.handoverTime || "-").replace("T", " ")}\n交接地点：${app.agreementInfo.handoverLocation || "-"}`
            : "暂未生成";
        const followups = (app.followupTasks || []).map(task =>
            `${followupTaskLabels[task.taskType]?.label || task.taskType} / ${task.planDate} / ${followupTaskLabels[task.status]?.label || task.status}`
        ).join("\n");
        return `申请：${app.applicationId}
猫咪：${app.catName || app.catId}
申请人：${app.userName || app.userId}
状态：${applicationStatusLabels[app.status]?.label || app.status}
评分：${app.score} / ${riskLabels[app.riskLevel]?.label || app.riskLevel}
评分原因：${app.scoreReasons || "-"}
居住条件：${app.livingCondition || "-"}
养宠经验：${app.petExperience || "-"}
家庭支持：${app.familySupport || "-"}
费用能力：${app.costAffordability || "-"}
接受回访：${app.acceptFollowup ? "是" : "否"}
承诺内容：${app.commitmentText || "-"}
申请理由：${app.extraReason || "-"}
协议交接：
${agreement}
回访任务：
${followups || "暂无"}
审核记录：
${audits || "暂无"}`;
    }

    async function loadAdminClues() {
        const panel = shell.querySelector(".mis-table-panel");
        const params = new URLSearchParams(location.hash.split("?")[1] || "");
        try {
            const query = new URLSearchParams();
            ["status", "urgencyLevel", "keyword"].forEach(key => {
                if (params.get(key)) query.set(key, params.get(key));
            });
            const clues = await api(`/api/admin/clues${query.toString() ? `?${query}` : ""}`);
            panel.innerHTML = `
                ${summaryCards([
                    { label: "线索总数", value: clues.length },
                    { label: "待核实", value: countRows(clues, item => item.status === "PENDING_VERIFY") },
                    { label: "已核实有效", value: countRows(clues, item => item.status === "VERIFIED_VALID") },
                    { label: "已建档", value: countRows(clues, item => item.status === "CREATED_CAT") },
                    { label: "无效/重复", value: countRows(clues, item => ["INVALID", "DUPLICATE"].includes(item.status)) }
                ], "business-summary")}
                <div class="mis-filter-row">
                    <select id="admin-clue-status">
                        <option value="">全部状态</option>
                        ${Object.keys(clueStatuses).filter(key => !key.startsWith("待")).map(key => `<option value="${key}" ${params.get("status") === key ? "selected" : ""}>${clueStatuses[key].label}</option>`).join("")}
                    </select>
                    <select id="admin-clue-urgency">
                        <option value="">全部紧急程度</option>
                        ${Object.keys(urgencyLabels).map(key => `<option value="${key}" ${params.get("urgencyLevel") === key ? "selected" : ""}>${urgencyLabels[key].label}</option>`).join("")}
                    </select>
                    <input id="admin-clue-keyword" value="${escapeHtml(params.get("keyword") || "")}" placeholder="搜索编号、地点、上报人">
                    <button class="ghost-btn" id="admin-clue-search">筛选</button>
                </div>
                <div class="mis-table-wrap">
                    <table class="mis-table">
                        <thead><tr><th>线索编号</th><th>照片</th><th>地点</th><th>上报人</th><th>紧急程度</th><th>状态</th><th>上报时间</th><th>操作</th></tr></thead>
                        <tbody>
                            ${clues.map(clue => `
                                <tr>
                                    <td>${escapeHtml(clue.clueNo)}</td>
                                    <td><img ${imageAttrs(clue.photoUrl, "thumb-image mis-thumb", "线索照片")}></td>
                                    <td>${escapeHtml(clue.foundLocation)}<br><small>${escapeHtml(clue.foundArea || "")}</small></td>
                                    <td>${escapeHtml(clue.reporterName || "-")}</td>
                                    <td>${tag(clue.urgencyLevel, urgencyLabels)}</td>
                                    <td>${tag(clue.status, clueStatuses)}</td>
                                    <td>${escapeHtml((clue.createTime || "").replace("T", " "))}</td>
                                    <td>
                                        <button class="ghost-btn" data-detail="${escapeHtml(clue.id)}">详情</button>
                                        ${["PENDING_VERIFY", "VERIFIED_VALID"].includes(clue.status) ? `<button class="ghost-btn" data-verify="${escapeHtml(clue.id)}">核实</button>` : ""}
                                        ${["PENDING_VERIFY", "VERIFIED_VALID"].includes(clue.status) ? `<button class="ghost-btn" data-invalid="${escapeHtml(clue.id)}">标记无效</button>` : ""}
                                        ${currentUser()?.role === "ADMIN" && clue.status !== "CREATED_CAT" ? `<button class="ghost-btn" data-clue-delete="${escapeHtml(clue.id)}">逻辑删除</button>` : ""}
                                        ${canCreateCat(clue) ? `<button class="primary-btn" data-create="${escapeHtml(clue.id)}">建档</button>` : ""}
                                    </td>
                                </tr>
                            `).join("")}
                        </tbody>
                    </table>
                </div>
            `;
            bindAdminClueEvents(clues);
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function bindAdminClueEvents(clues) {
        document.getElementById("admin-clue-search").addEventListener("click", () => {
            const query = new URLSearchParams();
            const status = document.getElementById("admin-clue-status").value;
            const urgency = document.getElementById("admin-clue-urgency").value;
            const keyword = document.getElementById("admin-clue-keyword").value;
            if (status) query.set("status", status);
            if (urgency) query.set("urgencyLevel", urgency);
            if (keyword) query.set("keyword", keyword);
            window.location.hash = `#/admin/clues${query.toString() ? `?${query}` : ""}`;
        });
        shell.querySelectorAll("[data-detail]").forEach(button => button.addEventListener("click", () => {
            const clue = clues.find(item => item.id === button.dataset.detail);
            alert(`线索详情\n编号：${clue.clueNo}\n地点：${clue.foundLocation}\n描述：${clue.description}\n核实意见：${clue.verifyComment || "待填写"}`);
        }));
        shell.querySelectorAll("[data-verify]").forEach(button => button.addEventListener("click", async () => {
            const result = prompt("请输入核实结果：VALID / DUPLICATE / INVALID", "VALID");
            if (!result) return;
            const comment = prompt("请输入核实意见");
            if (!comment) return;
            try {
                await api(`/api/admin/clues/${button.dataset.verify}/verify`, {
                    method: "PUT",
                    body: JSON.stringify({ verifyResult: result.toUpperCase(), verifyComment: comment })
                });
                await loadAdminClues();
            } catch (error) {
                alert(error.message);
            }
        }));
        shell.querySelectorAll("[data-invalid]").forEach(button => button.addEventListener("click", async () => {
            const reason = prompt("请输入标记无效原因");
            if (!reason) return;
            try {
                await api(`/api/admin/clues/${button.dataset.invalid}/invalid`, {
                    method: "PUT",
                    body: JSON.stringify({ reason })
                });
                await loadAdminClues();
            } catch (error) {
                alert(error.message);
            }
        }));
        shell.querySelectorAll("[data-clue-delete]").forEach(button => button.addEventListener("click", async () => {
            if (!confirm("确认逻辑删除该线索？只适用于明显脏数据或无效数据。")) return;
            const reason = prompt("请输入逻辑删除原因");
            if (!reason) return;
            try {
                await api(`/api/admin/clues/${button.dataset.clueDelete}`, {
                    method: "DELETE",
                    body: JSON.stringify({ reason })
                });
                await loadAdminClues();
            } catch (error) {
                alert(error.message);
            }
        }));
        shell.querySelectorAll("[data-create]").forEach(button => button.addEventListener("click", async () => {
            const name = prompt("猫咪昵称，可留空", "待命名");
            if (name === null) return;
            try {
                await api(`/api/admin/clues/${button.dataset.create}/create-cat`, {
                    method: "POST",
                    body: JSON.stringify({ name, healthStatus: "B" })
                });
                await loadAdminClues();
            } catch (error) {
                alert(error.message);
            }
        }));
    }

    async function renderAdminAgreements(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载协议交接记录...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            ["status", "keyword"].forEach(key => {
                if (params.get(key)) query.set(key, params.get(key));
            });
            const rows = await api(`/api/admin/agreements/pending${query.toString() ? `?${query}` : ""}`);
            panel.innerHTML = `
                ${summaryCards([
                    { label: "交接记录", value: rows.length },
                    { label: "未生成协议", value: countRows(rows, item => !item.id) },
                    { label: "待交接", value: countRows(rows, item => ["GENERATED", "DRAFT"].includes(item.status)) },
                    { label: "已交接", value: countRows(rows, item => item.status === "HANDED_OVER") },
                    { label: "已取消", value: countRows(rows, item => item.status === "CANCELLED") }
                ], "business-summary")}
                <div class="mis-filter-row">
                    <select id="agreement-status"><option value="">全部协议状态</option>${Object.keys(agreementStatusLabels).map(key => `<option value="${key}" ${params.get("status") === key ? "selected" : ""}>${agreementStatusLabels[key].label}</option>`).join("")}</select>
                    <input id="agreement-keyword" value="${escapeHtml(params.get("keyword") || "")}" placeholder="搜索申请、协议、猫咪、认养人">
                    <button class="ghost-btn" id="agreement-search">筛选</button>
                    <a class="ghost-btn" href="#/admin/followups">回访任务</a>
                </div>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>申请编号</th><th>协议编号</th><th>猫咪</th><th>认养人</th><th>终审时间</th><th>协议状态</th><th>交接信息</th><th>操作</th></tr></thead><tbody>
                    ${rows.map(row => `<tr>
                        <td>${escapeHtml(row.applicationId)}</td>
                        <td>${escapeHtml(row.agreementNo || "未生成")}</td>
                        <td>${escapeHtml(row.catName || row.catId)}</td>
                        <td>${escapeHtml(row.adopterName || row.adopterId)}<br><small>${escapeHtml(row.adopterPhone || "")}</small></td>
                        <td>${escapeHtml((row.finalApprovedAt || "").replace("T", " "))}</td>
                        <td>${tag(row.status || "NOT_GENERATED", agreementStatusLabels)}</td>
                        <td>${row.handoverTime ? `${escapeHtml(row.handoverLocation || "-")}<br><small>${escapeHtml(row.handoverTime.replace("T", " "))}</small>` : "待交接"}</td>
                        <td>
                            ${row.id ? `<button class="ghost-btn" data-agreement-detail="${row.id}">查看</button>` : ""}
                            ${user.role === "ADMIN" && !row.id ? `<button class="primary-btn" data-agreement-generate="${escapeHtml(row.applicationId)}">生成协议</button>` : ""}
                            ${user.role === "ADMIN" && row.id && (row.status === "GENERATED" || row.status === "DRAFT") ? `<button class="ghost-btn" data-agreement-edit="${row.id}">编辑</button><button class="ghost-btn" data-agreement-cancel="${row.id}">取消</button><button class="primary-btn" data-agreement-handover="${row.id}">完成交接</button>` : ""}
                        </td>
                    </tr>`).join("") || `<tr><td colspan="8"><div class="mis-empty">暂无待交接记录</div></td></tr>`}
                </tbody></table></div>
            `;
            document.getElementById("agreement-search").addEventListener("click", () => {
                const next = new URLSearchParams();
                const status = document.getElementById("agreement-status").value;
                const keyword = document.getElementById("agreement-keyword").value;
                if (status) next.set("status", status);
                if (keyword) next.set("keyword", keyword);
                window.location.hash = `#/admin/agreements${next.toString() ? `?${next}` : ""}`;
            });
            shell.querySelectorAll("[data-agreement-detail]").forEach(button => button.addEventListener("click", async () => {
                const detail = await api(`/api/admin/agreements/${button.dataset.agreementDetail}`);
                alert(agreementDetailText(detail));
            }));
            shell.querySelectorAll("[data-agreement-generate]").forEach(button => button.addEventListener("click", async () => {
                await api(`/api/admin/agreements/${button.dataset.agreementGenerate}/generate`, { method: "POST" });
                renderAdminAgreements(route, user);
            }));
            shell.querySelectorAll("[data-agreement-edit]").forEach(button => button.addEventListener("click", async () => {
                const detail = await api(`/api/admin/agreements/${button.dataset.agreementEdit}`);
                const agreementContent = prompt("协议正文", detail.agreementContent || "");
                if (!agreementContent) return;
                const remark = prompt("备注", detail.remark || "") || "";
                await api(`/api/admin/agreements/${button.dataset.agreementEdit}`, { method: "PUT", body: JSON.stringify({ agreementContent, remark }) });
                renderAdminAgreements(route, user);
            }));
            shell.querySelectorAll("[data-agreement-cancel]").forEach(button => button.addEventListener("click", async () => {
                if (!confirm("确认取消该未交接协议？")) return;
                const reason = prompt("请输入取消原因");
                if (!reason) return;
                try {
                    await api(`/api/admin/agreements/${button.dataset.agreementCancel}/cancel`, {
                        method: "PUT",
                        body: JSON.stringify({ reason })
                    });
                    renderAdminAgreements(route, user);
                } catch (error) {
                    alert(error.message);
                }
            }));
            shell.querySelectorAll("[data-agreement-handover]").forEach(button => button.addEventListener("click", async () => {
                const handoverTime = prompt("交接时间，格式：2026-06-26T17:00:00", new Date().toISOString().slice(0, 19));
                if (!handoverTime) return;
                const handoverLocation = prompt("交接地点", "翡翠湖校区志愿者服务点");
                if (!handoverLocation) return;
                const handoverUserId = prompt("交接人用户ID", user.userId);
                if (!handoverUserId) return;
                const remark = prompt("备注", "现场确认完成交接") || "";
                await api(`/api/admin/agreements/${button.dataset.agreementHandover}/handover`, {
                    method: "PUT",
                    body: JSON.stringify({ handoverTime, handoverLocation, handoverUserId, remark, adopterConfirmed: true, volunteerConfirmed: true })
                });
                alert("交接完成，已自动生成 7/30/90 天回访任务。");
                window.location.hash = "#/admin/followups";
            }));
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function agreementDetailText(detail) {
        return `协议编号：${detail.agreementNo || "-"}
申请编号：${detail.applicationId}
猫咪：${detail.catName || detail.catId}
认养人：${detail.adopterName || detail.adopterId} / ${detail.adopterPhone || "-"}
状态：${agreementStatusLabels[detail.status]?.label || detail.status}
生成时间：${(detail.generatedTime || "-").replace("T", " ")}
交接时间：${(detail.handoverTime || "-").replace("T", " ")}
交接地点：${detail.handoverLocation || "-"}
交接人：${detail.handoverUserName || detail.handoverUserId || "-"}
备注：${detail.remark || "-"}

协议正文：
${detail.agreementContent || "-"}`;
    }

    async function renderMyFollowups(route, user) {
        userShell(route, user, `${pageHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载我的回访任务...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const status = new URLSearchParams(location.hash.split("?")[1] || "").get("status") || "";
            const tasks = await api(`/api/my/followup/tasks${status ? `?status=${status}` : ""}`);
            panel.innerHTML = `${followupSummaryCards(tasks)}${followupTaskTable(tasks, false, status)}`;
            bindFollowupFilter("#/my/followups");
            bindFollowupTaskActions(route, user, false);
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function renderAdminFollowups(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载回访任务...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            ["status", "planDate", "keyword"].forEach(key => {
                if (params.get(key)) query.set(key, params.get(key));
            });
            const tasks = await api(`/api/admin/followup/tasks${query.toString() ? `?${query}` : ""}`);
            panel.innerHTML = `${followupSummaryCards(tasks)}${followupTaskTable(tasks, true, params.get("status") || "", params.get("planDate") || "", params.get("keyword") || "", params.get("taskType") || "", user)}`;
            bindFollowupFilter("#/admin/followups");
            bindFollowupTaskActions(route, user, true);
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function followupSummaryCards(tasks) {
        const rows = Array.isArray(tasks) ? tasks : [];
        const count = key => rows.filter(item => item.status === key).length;
        const total = rows.length;
        const completed = count("COMPLETED");
        const pending = count("PENDING");
        const overdue = count("OVERDUE");
        const abnormal = count("ABNORMAL");
        const rate = total ? Math.round((completed / total) * 100) : 0;
        return `
            <section class="mis-stat-grid followup-summary">
                <article><span>回访任务</span><strong>${total}</strong></article>
                <article><span>待回访</span><strong>${pending}</strong></article>
                <article><span>已完成</span><strong>${completed}</strong></article>
                <article><span>逾期/异常</span><strong>${overdue + abnormal}</strong></article>
                <article><span>完成率</span><strong>${rate}%</strong></article>
            </section>
        `;
    }

    function followupTaskTable(tasks, admin, status, planDate, keyword, taskType, user) {
        return `
            <div class="mis-filter-row">
                <select id="followup-status"><option value="">全部任务状态</option>${["PENDING", "COMPLETED", "OVERDUE", "ABNORMAL"].map(key => `<option value="${key}" ${status === key ? "selected" : ""}>${followupTaskLabels[key].label}</option>`).join("")}</select>
                ${admin ? `<select id="followup-type"><option value="">全部任务类型</option>${["DAY_7", "DAY_30", "DAY_90"].map(key => `<option value="${key}" ${taskType === key ? "selected" : ""}>${followupTaskLabels[key].label}</option>`).join("")}</select><input id="followup-date" type="date" value="${escapeHtml(planDate || "")}"><input id="followup-keyword" value="${escapeHtml(keyword || "")}" placeholder="搜索猫咪、申请、认养人">` : ""}
                <button class="ghost-btn" id="followup-filter">筛选</button>
                ${admin && user?.role === "ADMIN" ? `<button class="primary-btn" id="followup-export">导出 CSV</button>` : ""}
                ${admin && user?.role === "ADMIN" ? `<button class="primary-btn" id="refresh-overdue">刷新逾期任务</button>` : ""}
            </div>
            <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>猫咪</th><th>申请编号</th><th>认养人</th><th>任务类型</th><th>计划日期</th><th>状态</th><th>操作</th></tr></thead><tbody>
                ${(tasks || []).map(task => `<tr>
                    <td><img ${imageAttrs(task.catCoverUrl, "thumb-image mis-thumb", "猫咪照片")}>${escapeHtml(task.catName || task.catId)}</td>
                    <td>${escapeHtml(task.applicationId)}</td>
                    <td>${escapeHtml(task.adopterName || task.adopterId)}</td>
                    <td>${tag(task.taskType, followupTaskLabels)}</td>
                    <td>${escapeHtml(task.planDate || "-")}</td>
                    <td>${tag(task.status, followupTaskLabels)}</td>
                    <td>
                        <button class="ghost-btn" data-followup-detail="${task.id}">详情</button>
                        ${!admin && task.feedbackEnabled ? `<button class="primary-btn" data-followup-submit="${task.id}">提交回访</button>` : ""}
                        ${admin && ["PENDING", "OVERDUE", "COMPLETED"].includes(task.status) ? `<button class="ghost-btn" data-followup-abnormal="${task.id}">标记异常</button>` : ""}
                    </td>
                </tr>`).join("") || `<tr><td colspan="7"><div class="mis-empty">暂无回访任务</div></td></tr>`}
            </tbody></table></div>
        `;
    }

    function bindFollowupFilter(baseHash) {
        document.getElementById("followup-filter").addEventListener("click", () => {
            const next = new URLSearchParams();
            const status = document.getElementById("followup-status").value;
            const taskType = document.getElementById("followup-type")?.value;
            const date = document.getElementById("followup-date")?.value;
            const keyword = document.getElementById("followup-keyword")?.value;
            if (status) next.set("status", status);
            if (taskType) next.set("taskType", taskType);
            if (date) next.set("planDate", date);
            if (keyword) next.set("keyword", keyword);
            window.location.hash = `${baseHash}${next.toString() ? `?${next}` : ""}`;
        });
    }

    function bindFollowupTaskActions(route, user, admin) {
        document.getElementById("refresh-overdue")?.addEventListener("click", async () => {
            const result = await api("/api/admin/followup/tasks/refresh-overdue", { method: "POST" });
            alert(`已刷新逾期任务：${result.updatedTaskCount} 条，生成预警：${result.generatedWarningCount} 条`);
            renderAdminFollowups(route, user);
        });
        document.getElementById("followup-export")?.addEventListener("click", async () => {
            try {
                const params = new URLSearchParams(location.hash.split("?")[1] || "");
                await downloadCsv(`/api/admin/export/followups${params.toString() ? `?${params}` : ""}`, "followups.csv");
            } catch (error) {
                alert(error.message);
            }
        });
        shell.querySelectorAll("[data-followup-detail]").forEach(button => button.addEventListener("click", async () => {
            const path = admin ? `/api/admin/followup/tasks/${button.dataset.followupDetail}` : `/api/my/followup/tasks/${button.dataset.followupDetail}`;
            const detail = await api(path);
            alert(followupTaskDetailText(detail));
        }));
        shell.querySelectorAll("[data-followup-submit]").forEach(button => button.addEventListener("click", async () => {
            const content = prompt("回访文字内容");
            if (!content) return;
            const catCondition = prompt("猫咪当前状态描述");
            if (!catCondition) return;
            const environmentDesc = prompt("生活环境描述");
            if (!environmentDesc) return;
            const photoUrl = prompt("回访照片 URL", "/uploads/cats/no-photo.svg") || "";
            const abnormalFlag = confirm("是否存在异常？");
            const abnormalDesc = abnormalFlag ? prompt("异常说明") : "";
            if (abnormalFlag && !abnormalDesc) return;
            const validationMessage = [
                validateCleanText("回访内容", content, 5, 500),
                validateCleanText("猫咪状态描述", catCondition, 2, 500),
                validateCleanText("环境描述", environmentDesc, 2, 500),
                abnormalFlag ? validateCleanText("异常说明", abnormalDesc, 5, 500) : ""
            ].find(Boolean);
            if (validationMessage) {
                alert(validationMessage);
                return;
            }
            await api(`/api/my/followup/tasks/${button.dataset.followupSubmit}/records`, {
                method: "POST",
                body: JSON.stringify({ content, catCondition, environmentDesc, photoUrl, abnormalFlag, abnormalDesc })
            });
            alert(abnormalFlag ? "已提交异常反馈，管理员将跟进处理。" : "提交成功，回访任务已完成。");
            renderMyFollowups(route, user);
        }));
        shell.querySelectorAll("[data-followup-abnormal]").forEach(button => button.addEventListener("click", async () => {
            const abnormalDesc = prompt("异常说明");
            if (!abnormalDesc) return;
            const volunteerComment = prompt("志愿者备注", "") || "";
            await api(`/api/admin/followup/tasks/${button.dataset.followupAbnormal}/mark-abnormal`, {
                method: "PUT",
                body: JSON.stringify({ abnormalDesc, volunteerComment })
            });
            renderAdminFollowups(route, user);
        }));
    }

    function followupTaskDetailText(task) {
        return `任务：${task.id}
猫咪：${task.catName || task.catId}
认养人：${task.adopterName || task.adopterId}
类型：${followupTaskLabels[task.taskType]?.label || task.taskType}
计划日期：${task.planDate || "-"}
状态：${followupTaskLabels[task.status]?.label || task.status}
提交时间：${(task.submitTime || "-").replace("T", " ")}
回访内容：${task.recordContent || "-"}
猫咪状态：${task.catCondition || "-"}
生活环境：${task.environmentDesc || "-"}
异常说明：${task.abnormalDesc || "-"}
志愿者备注：${task.volunteerComment || "-"}
关联预警：${task.warningCount || 0} 条`;
    }

    async function renderAdminWarnings(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载异常预警...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            ["warningType", "warningLevel", "status", "keyword"].forEach(key => {
                if (params.get(key)) query.set(key, params.get(key));
            });
            const warnings = await api(`/api/admin/warnings${query.toString() ? `?${query}` : ""}`);
            panel.innerHTML = `
                ${summaryCards([
                    { label: "预警总数", value: warnings.length },
                    { label: "待处理", value: countRows(warnings, item => item.status === "PENDING") },
                    { label: "处理中", value: countRows(warnings, item => item.status === "PROCESSING") },
                    { label: "已处理", value: countRows(warnings, item => item.status === "HANDLED") },
                    { label: "已忽略", value: countRows(warnings, item => item.status === "IGNORED") }
                ], "business-summary")}
                <div class="mis-filter-row">
                    <select id="warning-type"><option value="">全部类型</option>${Object.keys(warningTypeLabels).map(key => `<option value="${key}" ${params.get("warningType") === key ? "selected" : ""}>${warningTypeLabels[key].label}</option>`).join("")}</select>
                    <select id="warning-level"><option value="">全部等级</option>${Object.keys(riskLabels).map(key => `<option value="${key}" ${params.get("warningLevel") === key ? "selected" : ""}>${riskLabels[key].label}</option>`).join("")}</select>
                    <select id="warning-status"><option value="">全部状态</option>${Object.keys(warningStatusLabels).map(key => `<option value="${key}" ${params.get("status") === key ? "selected" : ""}>${warningStatusLabels[key].label}</option>`).join("")}</select>
                    <input id="warning-keyword" value="${escapeHtml(params.get("keyword") || "")}" placeholder="搜索标题、猫咪、认养人">
                    <button class="ghost-btn" id="warning-filter">筛选</button>
                    ${user.role === "ADMIN" ? `<button class="primary-btn" id="warning-export">导出 CSV</button>` : ""}
                </div>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>标题</th><th>类型</th><th>等级</th><th>猫咪</th><th>认养人</th><th>状态</th><th>创建时间</th><th>操作</th></tr></thead><tbody>
                    ${(warnings || []).map(item => `<tr>
                        <td>${escapeHtml(item.title)}</td>
                        <td>${tag(item.warningType, warningTypeLabels)}</td>
                        <td>${tag(item.warningLevel, riskLabels)}</td>
                        <td>${escapeHtml(item.catName || item.catId || "-")}</td>
                        <td>${escapeHtml(item.userName || item.userId || "-")}</td>
                        <td>${tag(item.status, warningStatusLabels)}</td>
                        <td>${escapeHtml((item.createTime || "").replace("T", " "))}</td>
                        <td><button class="ghost-btn" data-warning-detail="${item.id}">详情</button>${user.role === "ADMIN" && !["HANDLED", "IGNORED"].includes(item.status) ? `<button class="primary-btn" data-warning-handle="${item.id}">处理</button>` : ""}</td>
                    </tr>`).join("") || `<tr><td colspan="8"><div class="mis-empty">暂无异常预警</div></td></tr>`}
                </tbody></table></div>
            `;
            document.getElementById("warning-filter").addEventListener("click", () => {
                const next = new URLSearchParams();
                const type = document.getElementById("warning-type").value;
                const level = document.getElementById("warning-level").value;
                const status = document.getElementById("warning-status").value;
                const keyword = document.getElementById("warning-keyword").value;
                if (type) next.set("warningType", type);
                if (level) next.set("warningLevel", level);
                if (status) next.set("status", status);
                if (keyword) next.set("keyword", keyword);
                window.location.hash = `#/admin/warnings${next.toString() ? `?${next}` : ""}`;
            });
            document.getElementById("warning-export")?.addEventListener("click", async () => {
                try {
                    const params = new URLSearchParams(location.hash.split("?")[1] || "");
                    await downloadCsv(`/api/admin/export/warnings${params.toString() ? `?${params}` : ""}`, "warnings.csv");
                } catch (error) {
                    alert(error.message);
                }
            });
            shell.querySelectorAll("[data-warning-detail]").forEach(button => button.addEventListener("click", async () => {
                const detail = await api(`/api/admin/warnings/${button.dataset.warningDetail}`);
                alert(warningDetailText(detail));
            }));
            shell.querySelectorAll("[data-warning-handle]").forEach(button => button.addEventListener("click", async () => {
                const targetStatus = prompt("目标状态：PROCESSING / HANDLED / IGNORED", "HANDLED");
                if (!targetStatus) return;
                const handleComment = prompt("处理意见");
                if (!handleComment) return;
                await api(`/api/admin/warnings/${button.dataset.warningHandle}/handle`, {
                    method: "PUT",
                    body: JSON.stringify({ targetStatus: targetStatus.toUpperCase(), handleComment })
                });
                renderAdminWarnings(route, user);
            }));
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function warningDetailText(item) {
        return `预警：${item.title}
类型：${warningTypeLabels[item.warningType]?.label || item.warningType}
等级：${riskLabels[item.warningLevel]?.label || item.warningLevel}
状态：${warningStatusLabels[item.status]?.label || item.status}
猫咪：${item.catName || item.catId || "-"}
认养人：${item.userName || item.userId || "-"}
内容：${item.content || "-"}
处理人：${item.handlerName || item.handlerId || "-"}
处理意见：${item.handleComment || "-"}
处理时间：${(item.handleTime || "-").replace("T", " ")}`;
    }

    async function renderAdminUsers(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载用户...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            ["role", "enabled", "keyword"].forEach(key => {
                if (params.get(key)) query.set(key, params.get(key));
            });
            const users = await api(`/api/admin/users${query.toString() ? `?${query}` : ""}`);
            panel.innerHTML = `
                <div class="mis-filter-row">
                    <select id="admin-user-role"><option value="">全部角色</option>${["STUDENT", "VOLUNTEER", "HOSPITAL", "ADMIN"].map(role => `<option value="${role}" ${params.get("role") === role ? "selected" : ""}>${roleLabels[role] || role}</option>`).join("")}</select>
                    <select id="admin-user-enabled"><option value="">全部状态</option><option value="true" ${params.get("enabled") === "true" ? "selected" : ""}>启用</option><option value="false" ${params.get("enabled") === "false" ? "selected" : ""}>禁用</option></select>
                    <input id="admin-user-keyword" value="${escapeHtml(params.get("keyword") || "")}" placeholder="搜索账号、姓名、手机号">
                    <button class="ghost-btn" id="admin-user-search">筛选</button>
                    <button class="primary-btn" id="admin-user-create">新增用户</button>
                </div>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>ID</th><th>姓名</th><th>账号</th><th>手机号</th><th>单位</th><th>角色</th><th>状态</th><th>操作</th></tr></thead><tbody>
                    ${(users || []).map(item => `<tr>
                        <td>${escapeHtml(item.userId)}</td>
                        <td>${escapeHtml(item.userName)}</td>
                        <td>${escapeHtml(item.schoolNo || "-")}</td>
                        <td>${escapeHtml(item.phone || "-")}</td>
                        <td>${escapeHtml(item.college || "-")}</td>
                        <td>${escapeHtml(roleLabels[normalizeRole(item.role)] || normalizeRole(item.role))}</td>
                        <td>${item.enabled ? "启用" : "禁用"}</td>
                        <td>
                            <button class="ghost-btn" data-user-edit="${escapeHtml(item.userId)}">编辑</button>
                            <button class="ghost-btn" data-user-role="${escapeHtml(item.userId)}">改角色</button>
                            <button class="ghost-btn" data-user-status="${escapeHtml(item.userId)}" data-enabled="${!item.enabled}">${item.enabled ? "禁用" : "启用"}</button>
                        </td>
                    </tr>`).join("") || `<tr><td colspan="8"><div class="mis-empty">暂无用户</div></td></tr>`}
                </tbody></table></div>
            `;
            document.getElementById("admin-user-search").addEventListener("click", () => {
                const next = new URLSearchParams();
                const role = document.getElementById("admin-user-role").value;
                const enabled = document.getElementById("admin-user-enabled").value;
                const keyword = document.getElementById("admin-user-keyword").value;
                if (role) next.set("role", role);
                if (enabled) next.set("enabled", enabled);
                if (keyword) next.set("keyword", keyword);
                window.location.hash = `#/admin/users${next.toString() ? `?${next}` : ""}`;
            });
            document.getElementById("admin-user-create").addEventListener("click", async () => saveAdminUser(route, user, null));
            shell.querySelectorAll("[data-user-edit]").forEach(button => button.addEventListener("click", async () => {
                const item = users.find(row => row.userId === button.dataset.userEdit);
                await saveAdminUser(route, user, item);
            }));
            shell.querySelectorAll("[data-user-role]").forEach(button => button.addEventListener("click", async () => {
                const role = prompt("目标角色：STUDENT / VOLUNTEER / HOSPITAL / ADMIN", "STUDENT");
                if (!role) return;
                await api(`/api/admin/users/${button.dataset.userRole}/role?role=${encodeURIComponent(role.toUpperCase())}`, { method: "PUT" });
                renderAdminUsers(route, user);
            }));
            shell.querySelectorAll("[data-user-status]").forEach(button => button.addEventListener("click", async () => {
                const enabled = button.dataset.enabled === "true";
                if (!confirm(`确认${enabled ? "启用" : "禁用"}该用户？`)) return;
                await api(`/api/admin/users/${button.dataset.userStatus}/status?enabled=${enabled}`, { method: "PUT" });
                renderAdminUsers(route, user);
            }));
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function saveAdminUser(route, user, item) {
        const userName = prompt("姓名", item?.userName || "");
        if (!userName) return;
        const schoolNo = prompt("登录账号/学工号", item?.schoolNo || "");
        if (!schoolNo) return;
        const phone = prompt("手机号", item?.phone || "");
        if (!phone) return;
        const college = prompt("学院或单位", item?.college || "");
        if (!college) return;
        const role = item ? normalizeRole(item.role) : (prompt("角色：STUDENT / VOLUNTEER / HOSPITAL / ADMIN", "STUDENT") || "STUDENT").toUpperCase();
        const password = item ? "" : prompt("初始密码，至少 6 位", "123456");
        if (!item && !password) return;
        const petExperience = prompt("说明/经验", item?.petExperience || "后台维护用户") || "";
        const validationMessage = [
            validateCleanText("姓名", userName, 2, 30),
            validateCleanText("登录账号", schoolNo, 2, 30),
            validateCleanText("学院或单位", college, 2, 80),
            petExperience ? validateCleanText("说明", petExperience, 2, 300) : "",
            !/^1[3-9]\d{9}$/.test(phone) ? "手机号格式不正确" : ""
        ].find(Boolean);
        if (validationMessage) {
            alert(validationMessage);
            return;
        }
        await api(item ? `/api/admin/users/${item.userId}` : "/api/admin/users", {
            method: item ? "PUT" : "POST",
            body: JSON.stringify({
                userName,
                schoolNo,
                password,
                phone,
                idCard: item?.idCard || "",
                college,
                petExperience,
                role,
                enabled: item ? item.enabled : true
            })
        });
        renderAdminUsers(route, user);
    }

    function redirectTarget(defaultHash) {
        const params = new URLSearchParams((location.hash.split("?")[1] || "").replace(/^redirect=/, "redirect="));
        const redirect = params.get("redirect");
        if (!redirect || redirect === "#/login" || redirect === "#/register" || redirect === "#/auth" || redirect === "#auth") {
            return defaultHash;
        }
        if (redirect.startsWith("#/admin")) {
            const user = currentUser();
            const route = findRoute(redirect.split("?")[0]);
            return user && route && normalizeRole(user.role) !== "STUDENT" && canVisit(route, user.role) ? redirect : "#/403";
        }
        return redirect.startsWith("#/") ? redirect : defaultHash;
    }

    function roleLanding(user) {
        const role = normalizeRole(user?.role);
        if (!user) return "#/";
        if (role === "HOSPITAL") return "#/admin/hospital";
        if (role === "VOLUNTEER" || role === "ADMIN") return "#/admin/dashboard";
        return "#/profile";
    }

    async function renderLogin(route, user) {
        if (user) {
            window.location.hash = redirectTarget("#/");
            return;
        }
        userShell(route, user, `
            <section class="auth-page">
                <div class="auth-copy">
                    <p class="eyebrow">实名认养 · 审核交接 · 长期回访</p>
                    <h1>合肥工业大学校园流浪猫在线认养系统</h1>
                    <p>登录后默认进入前台门户。志愿者、医院用户和管理员可从前台入口进入对应后台。</p>
                    <div class="flow-line"><span>发现上报</span><span>志愿者核实</span><span>在线认养</span><span>审核交接</span><span>回访预警</span></div>
                    <div class="auth-cat-showcase">
                        <img src="/uploads/cats/cat_01_01.jpg" alt="校园猫咪玩耍照片">
                        <div class="auth-cat-note">
                            <strong>今天也要被温柔接住</strong>
                            <span>从发现线索到安心到家，每一步都有记录。</span>
                        </div>
                    </div>
                    <a class="ghost-btn" href="#/">返回首页</a>
                </div>
                <form class="auth-card" id="login-form">
                    <h2>账号登录</h2>
                    <label>用户名 / 学号 / 手机号<input name="account" autocomplete="username" required></label>
                    <label>密码<input name="password" type="password" autocomplete="current-password" required></label>
                    <label class="check"><input name="remember" type="checkbox"> 记住登录</label>
                    <button class="primary-btn">登录</button>
                    <p id="login-message" class="form-message"></p>
                    <p class="hint">演示账号：2024210008、2022210006、H2026002、A2026002，默认密码 123456。</p>
                    <div class="demo-account-row" aria-label="演示账号快捷填充">
                        <button class="ghost-btn" type="button" data-demo-account="2024210008">普通用户</button>
                        <button class="ghost-btn" type="button" data-demo-account="2022210006">志愿者</button>
                        <button class="ghost-btn" type="button" data-demo-account="H2026002">医院</button>
                        <button class="ghost-btn" type="button" data-demo-account="A2026002">管理员</button>
                    </div>
                    <a href="#/register">还没有账号？注册普通用户</a>
                </form>
            </section>
        `);
        document.querySelectorAll("[data-demo-account]").forEach(button => {
            button.addEventListener("click", () => {
                const form = document.getElementById("login-form");
                form.elements.account.value = button.dataset.demoAccount;
                form.elements.password.value = "123456";
            });
        });
        document.getElementById("login-form").addEventListener("submit", async event => {
            event.preventDefault();
            const form = new FormData(event.currentTarget);
            const message = document.getElementById("login-message");
            message.textContent = "正在登录...";
            try {
                const result = await api("/api/users/login", {
                    method: "POST",
                    body: JSON.stringify({ account: form.get("account"), password: form.get("password") })
                });
                saveAuth(result);
                const verified = await api("/api/users/me");
                saveAuth({ token: result.token, user: verified });
                window.location.hash = redirectTarget("#/");
            } catch (error) {
                message.textContent = error.message;
            }
        });
    }

    async function renderRegister(route, user) {
        if (user) {
            window.location.hash = "#/profile";
            return;
        }
        userShell(route, user, `
            <section class="auth-page">
                <div class="auth-copy">
                    <p class="eyebrow">普通用户注册</p>
                    <h1>创建认养门户账号</h1>
                    <p>前台注册只创建普通用户账号。志愿者、医院用户和管理员由后台用户管理或演示数据维护。</p>
                    <div class="flow-line"><span>实名信息</span><span>提交线索</span><span>申请认养</span><span>查看消息</span></div>
                    <a class="ghost-btn" href="#/login">返回登录</a>
                </div>
                <form class="auth-card" id="register-form">
                    <h2>注册普通用户</h2>
                    <label>用户名<input name="userName" required></label>
                    <label>手机号<input name="phone" pattern="^1[3-9]\\d{9}$" required></label>
                    <label>学号或工号<input name="schoolNo" required></label>
                    <label>身份证号<input name="idCard" pattern="^\\d{17}[\\dXx]$" required></label>
                    <label>学院/单位<input name="college" required></label>
                    <label>密码<input name="password" type="password" minlength="6" required></label>
                    <label>确认密码<input name="confirmPassword" type="password" minlength="6" required></label>
                    <label class="wide">养宠经验<textarea name="petExperience" placeholder="可选：简单说明过往照护经验"></textarea></label>
                    <button class="primary-btn">注册</button>
                    <p id="register-message" class="form-message"></p>
                </form>
            </section>
        `);
        document.getElementById("register-form").addEventListener("submit", async event => {
            event.preventDefault();
            const form = new FormData(event.currentTarget);
            const message = document.getElementById("register-message");
            if (form.get("password") !== form.get("confirmPassword")) {
                message.textContent = "两次输入的密码不一致";
                return;
            }
            try {
                setSubmitting(event.currentTarget, true);
                requireValid(message, [
                    validateCleanText("用户姓名", form.get("userName"), 3, 30),
                    validateCleanText("学号或工号", form.get("schoolNo"), 2, 30),
                    validateCleanText("学院或单位", form.get("college"), 2, 80),
                    form.get("petExperience") ? validateCleanText("养宠经验", form.get("petExperience"), 2, 300) : ""
                ]);
                await api("/api/users", {
                    method: "POST",
                    body: JSON.stringify({
                        userName: form.get("userName"),
                        schoolNo: form.get("schoolNo"),
                        password: form.get("password"),
                        phone: form.get("phone"),
                        idCard: form.get("idCard"),
                        college: form.get("college"),
                        petExperience: form.get("petExperience")
                    })
                });
                window.location.hash = "#/login";
            } catch (error) {
                message.textContent = error.message;
            } finally {
                setSubmitting(event.currentTarget, false);
            }
        });
    }

    async function renderHome(route, user) {
        userShell(route, user, `<section class="front-home"><div class="mis-loading">正在加载门户数据...</div></section>`);
        const panel = shell.querySelector(".front-home");
        try {
            const [stats, cats, notices, roleData] = await Promise.all([
                api("/api/dashboard/stats").catch(() => ({})),
                api("/api/cats/public").catch(() => []),
                api("/api/notices").catch(() => []),
                loadRolePortalData(user)
            ]);
            panel.innerHTML = `
                <section class="hero">
                    <div class="hero-copy">
                        <p class="eyebrow">Campus Cat Adoption</p>
                        <h1>合肥工业大学校园流浪猫在线认养系统</h1>
                        <p>校园公益认养与流浪猫全生命周期管理平台，把发现上报、核实建档、医疗记录、在线认养、审核交接和回访预警放进同一条可追溯链路。</p>
                        <div class="actions">
                            <a class="primary-btn" href="#/cats">查看可认养猫咪</a>
                            <a class="ghost-btn" href="${user ? "#/clues/submit" : "#/login?redirect=%23%2Fclues%2Fsubmit"}">上报猫咪线索</a>
                            ${user && normalizeRole(user.role) !== "STUDENT" ? `<a class="ghost-btn" href="${escapeHtml(roleLanding(user))}">进入后台</a>` : ""}
                        </div>
                    </div>
                    <div class="hero-media">
                        <img ${imageAttrs((cats[0] && cats[0].coverUrl) || "/uploads/cats/cat_03_01.jpg", "hero-image", "校园猫咪照片")}>
                        <div class="floating-note"><strong>${stats.adoptableCount || cats.length || 0}</strong><span>只正在等待认养</span></div>
                    </div>
                </section>
                ${rolePortal(user, { ...roleData, stats })}
                <section class="portal-guides">
                    ${[
                        { icon: "🏠", title: "认养须知", text: "先了解照护责任、居住环境和长期陪伴要求。", link: "#/cats", action: "浏览可认养猫咪" },
                        { icon: "📷", title: "回访提醒", text: "交接后按 7/30/90 天提交照片和适应反馈。", link: user ? "#/my/followups" : "#/login?redirect=%23%2Fmy%2Ffollowups", action: "查看回访" },
                        { icon: "📝", title: "发现线索", text: "发现校园猫咪后，可提交地点、照片和简要描述。", link: user ? "#/clues/submit" : "#/login?redirect=%23%2Fclues%2Fsubmit", action: "提交线索" },
                        { icon: "💚", title: "公益协作", text: "志愿者、医院和管理员共同完成救助与认养闭环。", link: "#/notices", action: "查看公告" }
                    ].map(item => `
                        <a class="portal-guide-card" href="${item.link}">
                            <span>${item.icon}</span>
                            <strong>${escapeHtml(item.title)}</strong>
                            <p>${escapeHtml(item.text)}</p>
                            <small>${escapeHtml(item.action)}</small>
                        </a>
                    `).join("")}
                </section>
                <section class="section">
                    <div class="section-head process-head">
                        <div>
                            <p class="eyebrow">Process</p>
                            <h2>认养协同流程</h2>
                        </div>
                        <span>从发现线索到长期回访，每一步都有清晰责任人和状态记录。</span>
                    </div>
                    <div class="process-flow">
                        ${[
                            { title: "发现上报", role: "普通用户", desc: "提交地点、时间、照片和联系方式，形成待核实线索。", action: "线索入口" },
                            { title: "线索核实", role: "志愿者", desc: "现场确认猫咪状态，判断有效、重复或无效并补充说明。", action: "后台处理" },
                            { title: "医疗建档", role: "医院协作", desc: "记录体检、疫苗、绝育和治疗信息，完善健康档案。", action: "健康记录" },
                            { title: "在线申请", role: "认养人", desc: "选择可认养猫咪，提交居住环境和照护经验。", action: "认养申请" },
                            { title: "审核交接", role: "志愿者 / 管理员", desc: "完成初审、终审、协议生成与线下交接登记。", action: "协议闭环" },
                            { title: "回访预警", role: "系统协同", desc: "按 7/30/90 天节点回访，异常或逾期自动进入预警。", action: "持续跟进" }
                        ].map((item, index) => `
                            <article class="process-step">
                                <div class="process-step-top">
                                    <span class="process-number">${String(index + 1).padStart(2, "0")}</span>
                                    <span class="process-action">${escapeHtml(item.action)}</span>
                                </div>
                                <h3>${escapeHtml(item.title)}</h3>
                                <p>${escapeHtml(item.desc)}</p>
                                <strong>${escapeHtml(item.role)}</strong>
                            </article>
                        `).join("")}
                    </div>
                </section>
                <section class="section">
                    <div class="section-head"><div><p class="eyebrow">Adoption</p><h2>可认养猫咪推荐</h2></div><a class="ghost-btn" href="#/cats">全部猫咪</a></div>
                    <div class="mis-cat-grid showcase">
                        ${(cats || []).slice(0, 6).map(cat => homeCatCard(cat)).join("") || `<div class="mis-empty">暂无可认养猫咪</div>`}
                    </div>
                </section>
                <section class="section split">
                    <div class="panel home-notice-panel">
                        <div class="home-panel-head">
                            <div>
                                <p class="eyebrow">Notice</p>
                                <h2>最新公告</h2>
                            </div>
                            <a class="ghost-btn compact" href="#/notices">全部公告</a>
                        </div>
                        <div class="home-notice-list">
                            ${(notices || []).slice(0, 3).map(item => {
                                const meta = noticeMeta(item);
                                return `
                                    <article class="home-notice-card">
                                        <a class="home-notice-thumb" href="#/notices/${escapeHtml(item.noticeId)}">
                                            <img ${imageAttrs(meta.image, "home-notice-image", noticeTitle(item))}>
                                        </a>
                                        <div>
                                            <div class="home-notice-meta"><span>${escapeHtml(meta.label)}</span><time>${escapeHtml(noticeTime(item))}</time></div>
                                            <h3><a href="#/notices/${escapeHtml(item.noticeId)}">${escapeHtml(noticeTitle(item))}</a></h3>
                                            <p>${escapeHtml(noticeSummary(item))}</p>
                                            <a class="home-notice-link" href="#/notices/${escapeHtml(item.noticeId)}">查看详情</a>
                                        </div>
                                    </article>
                                `;
                            }).join("") || `<article class="home-notice-empty"><strong>暂无公告</strong><span>管理员发布后会显示在这里。</span></article>`}
                        </div>
                    </div>
                    <div class="panel campus-collab-panel">
                        <div class="campus-collab-media">
                            <img src="/uploads/campus/hfut-campus-banner.png" alt="合肥工业大学校园景色">
                            <span>合工大校园公益协同</span>
                        </div>
                        <div class="home-panel-head">
                            <div>
                                <p class="eyebrow">Volunteer</p>
                                <h2>校园公益协同</h2>
                            </div>
                        </div>
                        <p class="muted">围绕校园真实场景，志愿者、医院用户和管理员协作完成发现上报、线索核实、医疗记录、认养审核、协议交接和回访预警。</p>
                        <div class="campus-collab-steps">
                            <span><strong>线索核实</strong><small>现场确认与建档</small></span>
                            <span><strong>医疗协作</strong><small>体检、疫苗、绝育记录</small></span>
                            <span><strong>回访跟进</strong><small>7/30/90 天反馈</small></span>
                        </div>
                        <div class="actions"><a class="primary-btn" href="${user ? "#/clues/submit" : "#/login?redirect=%23%2Fclues%2Fsubmit"}">提交发现线索</a><a class="ghost-btn" href="${user ? "#/my/messages" : "#/login?redirect=%23%2Fmy%2Fmessages"}">我的消息</a></div>
                    </div>
                </section>
            `;
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function loadRolePortalData(user) {
        if (!user) return {};
        const role = normalizeRole(user.role);
        const [unread, tasks] = await Promise.all([
            api("/api/user/messages/unread-count").catch(() => 0),
            api("/api/my/followup/tasks?status=PENDING").catch(() => [])
        ]);
        const personalData = { unread, pendingTasks: Array.isArray(tasks) ? tasks.length : 0 };
        if (role === "STUDENT") {
            return personalData;
        }
        if (role === "HOSPITAL") {
            const summary = await api("/api/admin/hospital/summary").catch(() => ({}));
            return { ...personalData, summary };
        }
        const summary = await api("/api/admin/dashboard/summary").catch(() => ({}));
        return { ...personalData, summary };
    }

    function catCard(cat) {
        return `
            <article class="mis-cat-card">
                <a class="cat-card-img-wrap" href="#/cats/${escapeHtml(cat.catId)}">
                    <img ${imageAttrs(cat.coverUrl, "cat-card-image cat-card-img image-cover", "猫咪照片")}>
                    <span>查看详情</span>
                </a>
                <div>
                    <h3>${escapeHtml(cat.catName || "待命名")}</h3>
                    <p>${escapeHtml(cat.foundPlace || "-")} · ${escapeHtml(cat.color || "-")} · ${escapeHtml(cat.ageEstimate || "-")}</p>
                    <div class="mis-row-title">${tag(cat.status, catStatusLabels)}${tag(cat.healthLevel, healthLabels)}<span class="mis-tag info">${boolLabel(cat.sterilized, "已绝育", "未绝育")}</span><span class="mis-tag info">${boolLabel(cat.vaccinated, "已疫苗", "未疫苗")}</span></div>
                    <p>${escapeHtml(cat.personality || cat.description || "")}</p>
                    <a class="primary-btn" href="#/cats/${escapeHtml(cat.catId)}">查看详情</a>
                </div>
            </article>
        `;
    }

    function homeCatCard(cat) {
        return `
            <article class="mis-cat-card home-cat-card">
                <a class="home-cat-image-wrap" href="#/cats/${escapeHtml(cat.catId)}">
                    <img ${imageAttrs(cat.coverUrl, "home-cat-image", "猫咪照片")}>
                    <span>查看详情</span>
                </a>
                <div class="home-cat-body">
                    <h3 class="home-cat-title">${escapeHtml(cat.catName || "待命名")}</h3>
                    <p class="home-cat-meta">${escapeHtml(cat.foundPlace || "-")} · ${escapeHtml(cat.color || "-")} · ${escapeHtml(cat.ageEstimate || "-")}</p>
                    <div class="mis-row-title home-cat-tags">${tag(cat.status, catStatusLabels)}${tag(cat.healthLevel, healthLabels)}<span class="mis-tag info">${boolLabel(cat.sterilized, "已绝育", "未绝育")}</span><span class="mis-tag info">${boolLabel(cat.vaccinated, "已疫苗", "未疫苗")}</span></div>
                    <p class="home-cat-desc">${escapeHtml(cat.personality || cat.description || "")}</p>
                    <a class="primary-btn" href="#/cats/${escapeHtml(cat.catId)}">查看详情</a>
                </div>
            </article>
        `;
    }

    async function renderNotices(route, user) {
        userShell(route, user, `${pageHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载公告...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const notices = await api("/api/notices");
            panel.innerHTML = `
                <div class="notice-list enhanced">
                    ${(notices || []).map(item => {
                        const meta = noticeMeta(item);
                        return `
                            <article class="notice-card">
                                <a class="notice-card-image" href="#/notices/${escapeHtml(item.noticeId)}">
                                    <img ${imageAttrs(meta.image, "notice-cover", noticeTitle(item))}>
                                    <span>${escapeHtml(meta.label)}</span>
                                </a>
                                <div class="notice-card-body">
                                    <div class="notice-card-meta">
                                        <span>${escapeHtml(meta.label)}</span>
                                        <time>${escapeHtml(noticeTime(item))}</time>
                                    </div>
                                    <h3><a href="#/notices/${escapeHtml(item.noticeId)}">${escapeHtml(noticeTitle(item))}</a></h3>
                                    <p>${escapeHtml(noticeSummary(item))}</p>
                                    <a class="ghost-btn compact" href="#/notices/${escapeHtml(item.noticeId)}">查看详情</a>
                                </div>
                            </article>
                        `;
                    }).join("") || `<div class="mis-empty">暂无公告</div>`}
                </div>
            `;
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function renderNoticeDetail(route, user) {
        const noticeId = window.location.hash.split("?")[0].split("/").pop();
        userShell(route, user, `${pageHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载公告详情...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const notice = await api(`/api/notices/${noticeId}`);
            const meta = noticeMeta(notice);
            const summary = noticeSummary(notice);
            panel.innerHTML = `
                <article class="notice-detail enhanced">
                    <div class="notice-detail-hero">
                        <img ${imageAttrs(meta.image, "notice-detail-cover", noticeTitle(notice))}>
                        <div>
                            <p class="eyebrow">${escapeHtml(meta.label)}</p>
                            <h2>${escapeHtml(noticeTitle(notice))}</h2>
                            <p>${escapeHtml(summary)}</p>
                        </div>
                    </div>
                    <div class="notice-detail-meta">
                        <span>发布人：${escapeHtml(notice.publisher || "系统公告")}</span>
                        <span>发布时间：${escapeHtml(noticeTime(notice) || "待发布")}</span>
                    </div>
                    <div class="notice-detail-content">
                        ${meta.sections.map(section => `
                            <section>
                                <h3>${escapeHtml(section.title)}</h3>
                                <p>${escapeHtml(section.text)}</p>
                            </section>
                        `).join("")}
                    </div>
                    <div class="notice-detail-actions">
                        <a class="ghost-btn" href="#/notices">返回公告列表</a>
                        <a class="primary-btn" href="#/cats">查看可认养猫咪</a>
                    </div>
                </article>
            `;
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function renderMyMessages(route, user) {
        userShell(route, user, `${pageHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载站内消息...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const readStatus = params.get("readStatus") || "";
            const messages = await api(`/api/user/messages${readStatus ? `?readStatus=${readStatus}` : ""}`);
            panel.innerHTML = `
                <div class="mis-filter-row">
                    <select id="message-read-status">
                        <option value="">全部消息</option>
                        <option value="UNREAD" ${readStatus === "UNREAD" ? "selected" : ""}>未读</option>
                        <option value="READ" ${readStatus === "READ" ? "selected" : ""}>已读</option>
                    </select>
                    <button class="ghost-btn" id="message-filter">筛选</button>
                    <button class="primary-btn" id="message-read-all">全部标记已读</button>
                </div>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>标题</th><th>业务</th><th>状态</th><th>时间</th><th>操作</th></tr></thead><tbody>
                    ${(messages || []).map(item => `<tr><td><strong>${escapeHtml(item.title)}</strong><br><small>${escapeHtml(item.content)}</small></td><td>${escapeHtml(item.bizType || item.messageType || "-")}</td><td>${tag(item.readStatus, { UNREAD: { label: "未读", tone: "warning" }, READ: { label: "已读", tone: "success" } })}</td><td>${escapeHtml((item.createTime || "").replace("T", " "))}</td><td>${item.readStatus !== "READ" ? `<button class="ghost-btn" data-message-read="${item.id}">标记已读</button>` : "-"}</td></tr>`).join("") || `<tr><td colspan="5"><div class="mis-empty">暂无消息</div></td></tr>`}
                </tbody></table></div>
            `;
            document.getElementById("message-filter").addEventListener("click", () => {
                const next = document.getElementById("message-read-status").value;
                window.location.hash = next ? `#/my/messages?readStatus=${next}` : "#/my/messages";
            });
            document.getElementById("message-read-all").addEventListener("click", async () => {
                await api("/api/user/messages/read-all", { method: "PUT" });
                renderMyMessages(route, user);
            });
            shell.querySelectorAll("[data-message-read]").forEach(button => button.addEventListener("click", async () => {
                await api(`/api/user/messages/${button.dataset.messageRead}/read`, { method: "PUT" });
                renderMyMessages(route, user);
            }));
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function renderProfile(route, user) {
        userShell(route, user, `${pageHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载个人中心...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const [freshUser, clues, apps, tasks, unread] = await Promise.all([
                api("/api/users/me"),
                api("/api/my/clues").catch(() => []),
                api("/api/my/adoption/applications").catch(() => []),
                api("/api/my/followup/tasks").catch(() => []),
                api("/api/user/messages/unread-count").catch(() => 0)
            ]);
            saveAuth({ token: token(), user: freshUser });
            panel.innerHTML = `
                <section class="profile-grid">
                    <article class="panel profile-card">
                        <p class="eyebrow">Account</p>
                        <h2>${escapeHtml(freshUser.userName)}</h2>
                        <p>${escapeHtml(freshUser.schoolNo || "-")} · ${escapeHtml(freshUser.phone || "-")} · ${escapeHtml(freshUser.college || "-")}</p>
                        <span class="mis-tag primary">${escapeHtml(roleLabels[normalizeRole(freshUser.role)] || normalizeRole(freshUser.role))}</span>
                        ${normalizeRole(freshUser.role) !== "STUDENT" ? `<a class="primary-btn" href="${escapeHtml(roleLanding(freshUser))}">进入后台</a>` : ""}
                    </article>
                    <article class="panel"><strong>${clues.length}</strong><span>我的线索</span><a class="ghost-btn" href="#/my/clues">查看</a></article>
                    <article class="panel"><strong>${apps.length}</strong><span>我的申请</span><a class="ghost-btn" href="#/my/applications">查看</a></article>
                    <article class="panel"><strong>${tasks.length}</strong><span>我的回访</span><a class="ghost-btn" href="#/my/followups">查看</a></article>
                    <article class="panel"><strong>${unread}</strong><span>未读消息</span><a class="ghost-btn" href="#/my/messages">查看</a></article>
                </section>
                <form class="mis-form front-form" id="profile-form">
                    <label>姓名<input name="userName" value="${escapeHtml(freshUser.userName || "")}" required></label>
                    <label>手机号<input name="phone" value="${escapeHtml(freshUser.phone || "")}" required></label>
                    <label>学院/单位<input name="college" value="${escapeHtml(freshUser.college || "")}" required></label>
                    <label class="wide">养宠经验<textarea name="petExperience">${escapeHtml(freshUser.petExperience || "")}</textarea></label>
                    <div class="mis-form-actions"><button class="primary-btn">保存资料</button><span id="profile-message"></span></div>
                </form>
            `;
            document.getElementById("profile-form").addEventListener("submit", async event => {
                event.preventDefault();
                const form = new FormData(event.currentTarget);
                const message = document.getElementById("profile-message");
                try {
                    setSubmitting(event.currentTarget, true);
                    requireValid(message, [
                        validateCleanText("用户姓名", form.get("userName"), 3, 30),
                        validateCleanText("学院或单位", form.get("college"), 2, 80),
                        form.get("petExperience") ? validateCleanText("养宠经验", form.get("petExperience"), 2, 300) : ""
                    ]);
                    const updated = await api("/api/users/me", {
                        method: "PATCH",
                        body: JSON.stringify({
                            userName: form.get("userName"),
                            phone: form.get("phone"),
                            college: form.get("college"),
                            petExperience: form.get("petExperience")
                        })
                    });
                    saveAuth({ token: token(), user: updated });
                    message.textContent = "资料已保存";
                } catch (error) {
                    message.textContent = error.message;
                } finally {
                    setSubmitting(event.currentTarget, false);
                }
            });
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function startScreenClock() {
        const node = document.getElementById("screen-live-time");
        if (!node) return;
        const update = () => {
            node.textContent = new Date().toLocaleString("zh-CN", { hour12: false });
        };
        update();
        screenClockTimer = window.setInterval(update, 1000);
    }

    async function renderDataScreen(route, user) {
        userShell(route, user, `<section class="data-screen"><div class="mis-loading">正在加载实时数据大屏...</div></section>`);
        const panel = shell.querySelector(".data-screen");
        try {
            const [stats, catStatusRows, applicationStatusRows] = await Promise.all([
                api("/api/dashboard/stats").catch(() => ({})),
                api("/api/dashboard/cat-status").catch(() => []),
                api("/api/dashboard/application-status").catch(() => [])
            ]);
            const statusGroups = Object.fromEntries((catStatusRows || []).map(item => [item.name, Number(item.count || 0)]));
            const applicationGroups = Object.fromEntries((applicationStatusRows || []).map(item => [item.name, Number(item.count || 0)]));
            const applicationMonitorRows = Object.entries(applicationGroups).slice(0, 6);
            const maxApplicationStatus = Math.max(1, ...Object.values(applicationGroups));
            const totalCats = Number(stats.catCount || Object.values(statusGroups).reduce((sum, value) => sum + value, 0));
            const adoptable = Number(stats.adoptableCount || statusGroups.ADOPTABLE || 0);
            const adopted = Number(stats.adoptedCount || 0);
            const pending = Number(stats.pendingApplicationCount || 0);
            const sterilization = Number(stats.sterilizationRate || 0);
            const following = Number(statusGroups.FOLLOWING || 0);
            const handedOver = Number(applicationGroups.HANDED_OVER || adopted || 0);
            const maxStatus = Math.max(1, ...Object.values(statusGroups));
            const funnel = [
                { label: "建档猫咪", value: totalCats, icon: "🐱" },
                { label: "可认养", value: adoptable, icon: "🏠" },
                { label: "待审核", value: pending, icon: "📝" },
                { label: "回访中", value: following, icon: "📷" },
                { label: "已交接", value: handedOver, icon: "💚" }
            ];
            const maxFunnel = Math.max(1, ...funnel.map(item => item.value));
            const agentTips = [
                pending > 0 ? `当前有 ${pending} 条申请等待处理，建议优先查看待初审和待交接记录。` : "当前没有待审核申请，认养审核队列较稳定。",
                following > 0 ? `${following} 只猫咪处于回访中，注意 7/30/90 天节点反馈。` : "暂无回访中猫咪，可以关注新交接申请。",
                adoptable > 0 ? `${adoptable} 只猫咪可认养，建议在门户和公告中保持曝光。` : "暂无可认养猫咪，建议先完善观察或医疗档案。"
            ];
            panel.innerHTML = `
                <div class="screen-shell">
                    <div class="screen-glow"></div>
                    <header class="screen-topbar">
                        <div>
                            <p class="screen-kicker">HFUT CAT ADOPTION COMMAND CENTER</p>
                            <h1>校园流浪猫实时数据驾驶舱</h1>
                        </div>
                        <div class="screen-live">
                            <span></span>
                            <strong>LIVE</strong>
                            <small id="screen-live-time"></small>
                        </div>
                        <a class="screen-back" href="#/">返回门户</a>
                    </header>

                    <section class="screen-kpi-row">
                        <article><span>🐾 猫咪档案</span><strong>${totalCats}</strong><small>有效建档总量</small></article>
                        <article><span>🏠 可认养</span><strong>${adoptable}</strong><small>当前可申请</small></article>
                        <article><span>📝 待审核</span><strong>${pending}</strong><small>待志愿者/管理员处理</small></article>
                        <article><span>💚 已交接</span><strong>${handedOver}</strong><small>完成认养交接</small></article>
                    </section>

                    <section class="screen-command-grid">
                        <article class="screen-card screen-radar-card">
                            <div class="screen-card-head"><span>Health Radar</span><strong>健康管理雷达</strong></div>
                            <div class="screen-radar">
                                <div class="screen-radar-ring" style="--rate:${Math.min(100, sterilization)}">
                                    <strong>${sterilization}%</strong>
                                    <span>绝育率</span>
                                </div>
                                <div class="screen-orbit-dot one"></div>
                                <div class="screen-orbit-dot two"></div>
                                <div class="screen-orbit-dot three"></div>
                            </div>
                            <div class="screen-radar-meta">
                                <span>疫苗率 ${stats.vaccinationRate || 0}%</span>
                                <span>医疗中 ${stats.medicalCount || statusGroups.MEDICAL || 0}</span>
                                <span>回访任务 ${stats.followupCount || 0}</span>
                            </div>
                        </article>

                        <article class="screen-card screen-flow-card">
                            <div class="screen-card-head"><span>Adoption Pipeline</span><strong>认养流程能量流</strong></div>
                            <div class="screen-flow">
                                ${funnel.map(item => `<div class="screen-flow-step"><span>${item.icon}</span><div><strong>${escapeHtml(item.label)}</strong><i><b style="width:${Math.max(8, item.value / maxFunnel * 100)}%"></b></i></div><em>${item.value}</em></div>`).join("")}
                            </div>
                        </article>

                        <article class="screen-card screen-status-card">
                            <div class="screen-card-head"><span>Status Matrix</span><strong>猫咪状态矩阵</strong></div>
                            <div class="screen-status-grid">
                                ${Object.entries(statusGroups).slice(0, 8).map(([status, value]) => `
                                    <div class="screen-status-cell">
                                        ${tag(status, catStatusLabels)}
                                        <strong>${value}</strong>
                                        <i><b style="height:${Math.max(14, value / maxStatus * 100)}%"></b></i>
                                    </div>
                                `).join("") || `<div class="mis-empty compact">暂无猫咪状态数据</div>`}
                            </div>
                        </article>

                        <article class="screen-card screen-application-card">
                            <div class="screen-card-head"><span>Application Monitor</span><strong>申请状态监控</strong></div>
                            <div class="screen-mini-bars">
                                ${applicationMonitorRows.map(([status, value]) => `<div><span>${escapeHtml(applicationStatusLabels[status]?.label || status)}</span><strong>${value}</strong><i><b style="width:${Math.max(8, value / maxApplicationStatus * 100)}%"></b></i></div>`).join("") || `<div class="mis-empty compact">暂无申请状态数据</div>`}
                            </div>
                        </article>

                        <article class="screen-card screen-agent-card">
                            <div class="screen-card-head"><span>AI Agent</span><strong>认养智能体</strong></div>
                            <div class="agent-dialog" id="agent-dialog">
                                <p data-role="assistant">你好，我是认养智能体。可以问我认养前准备、申请怎么写、猫咪到家适应期、回访照片要求等问题。</p>
                            </div>
                            <div class="agent-quick" hidden>
                                <button type="button" data-agent-question="认养前需要准备什么？">认养准备</button>
                                <button type="button" data-agent-question="申请认养时怎么写更合适？">申请建议</button>
                                <button type="button" data-agent-question="猫咪到家后躲起来怎么办？">适应期</button>
                            </div>
                            <form class="agent-chat-form" id="agent-chat-form">
                                <input id="agent-chat-input" maxlength="800" placeholder="输入你的认养问题..." autocomplete="off">
                                <button type="submit">发送</button>
                            </form>
                            <div class="agent-actions" hidden>
                                <a href="#/cats">查看可认养</a>
                                <a href="#/notices">同步公告</a>
                            </div>
                        </article>
                    </section>
                </div>
            `;
            startScreenClock();
            bindAgentChat();
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function bindAgentChat() {
        const form = document.getElementById("agent-chat-form");
        const input = document.getElementById("agent-chat-input");
        const dialog = document.getElementById("agent-dialog");
        if (!form || !input || !dialog) return;
        document.querySelector(".agent-actions")?.remove();
        input.placeholder = "想问什么都可以，直接输入你的问题...";
        const quickQuestions = [
            ["认养前需要准备什么？", "认养前需要准备什么？"],
            ["申请表怎么写更好？", "申请表怎么写更好？"],
            ["猫咪到家应激怎么办？", "猫咪到家应激怎么办？"],
            ["回访照片要怎么拍？", "回访照片要怎么拍？"],
            ["新手适合养什么猫？", "新手适合养什么猫？"],
            ["预算大概要准备多少？", "预算大概要准备多少？"]
        ];
        const quickPanel = document.querySelector(".agent-quick");
        quickPanel?.remove();
        if (!document.querySelector(".agent-faq-section")) {
            form.insertAdjacentHTML("afterend", `
                <section class="agent-faq-section">
                    <div class="agent-section-title">
                        <strong>常见问题</strong>
                        <span>点击卡片，快速了解认养常见问题</span>
                    </div>
                    <div class="agent-faq-grid">
                        <button type="button" class="agent-faq-card" data-agent-question="认养前需要准备什么？">
                            <strong>认养准备</strong>
                            <span>用品清单、预算评估、居家环境准备</span>
                        </button>
                        <button type="button" class="agent-faq-card" data-agent-question="申请表怎么写更好？">
                            <strong>申请建议</strong>
                            <span>申请填写、自我介绍、提升通过率</span>
                        </button>
                        <button type="button" class="agent-faq-card" data-agent-question="猫咪到家应激怎么办？">
                            <strong>适应期</strong>
                            <span>到家应激、隔离观察、作息适应</span>
                        </button>
                        <button type="button" class="agent-faq-card" data-agent-question="回访照片要怎么拍？">
                            <strong>回访要求</strong>
                            <span>回访频率、拍照要求、注意事项</span>
                        </button>
                        <button type="button" class="agent-faq-card" data-agent-question="新手认养有哪些常见误区？">
                            <strong>新手避坑</strong>
                            <span>常见误区、认养禁忌、错误做法提醒</span>
                        </button>
                        <button type="button" class="agent-faq-card" data-agent-question="猫咪日常喂养护理要注意什么？">
                            <strong>喂养护理</strong>
                            <span>饮食、猫砂、清洁、健康管理</span>
                        </button>
                    </div>
                </section>
            `);
        }
        const faqCards = [
            ["认养准备", "用品清单、预算评估、居家环境准备", "提前准备猫粮、猫砂盆、猫砂、食碗水碗、猫包和安全封窗，并确认室友或家人同意。"],
            ["申请建议", "申请填写、自我介绍、提升通过率", "写清住所稳定性、经济能力、养宠经验、假期照护安排，以及愿意配合回访的承诺。"],
            ["适应期", "到家应激、隔离观察、作息适应", "先给猫咪安静小空间，不强抱不追赶，保持食水和猫砂固定，通常观察 3 到 7 天。"],
            ["回访要求", "回访频率、拍照要求、注意事项", "照片建议包含猫咪近照、生活环境、食水区和猫砂区，画面清晰无遮挡，按节点提交。"],
            ["新手避坑", "常见误区、认养禁忌、错误做法提醒", "不要冲动认养、不要频繁换粮、不要放养，也不要忽视封窗、驱虫和绝育计划。"],
            ["喂养护理", "饮食、猫砂、清洁、健康管理", "保持饮食稳定、每日清理猫砂、定期驱虫免疫，发现拒食呕吐腹泻要及时咨询医生。"]
        ];
        document.querySelectorAll(".agent-faq-card").forEach((card, index) => {
            const [title, summary, answer] = faqCards[index] || faqCards[0];
            card.removeAttribute("data-agent-question");
            card.setAttribute("data-agent-flip", "");
            card.setAttribute("aria-pressed", "false");
            card.innerHTML = `
                <span class="agent-faq-face agent-faq-front">
                    <strong>${title}</strong>
                    <span>${summary}</span>
                    <em>点击查看答案</em>
                </span>
                <span class="agent-faq-face agent-faq-back">
                    <strong>${title}</strong>
                    <span>${answer}</span>
                    <em>点击翻回</em>
                </span>
            `;
        });
        dialog.querySelectorAll("p[data-role]").forEach(node => {
            const role = node.dataset.role || "assistant";
            const item = document.createElement("div");
            item.className = "agent-message";
            item.dataset.role = role;
            const avatar = document.createElement("span");
            avatar.className = "agent-avatar";
            avatar.setAttribute("aria-hidden", "true");
            avatar.textContent = role === "user" ? "喵" : "AI";
            const bubble = document.createElement("p");
            bubble.className = "agent-bubble";
            bubble.textContent = node.textContent;
            item.append(avatar, bubble);
            node.replaceWith(item);
        });
        const quick = document.querySelector(".agent-quick");
        if (false && quick && !quick.querySelector('[data-agent-intro="who"]')) {
            quick.insertAdjacentHTML("afterbegin", `
                <button type="button" data-agent-intro="who" data-agent-question="你是谁？">你是谁</button>
                <button type="button" data-agent-intro="ability" data-agent-question="你会做什么？">你会做什么</button>
            `);
        }
        const history = [];
        const localAnswers = [
            {
                test: text => /你是谁|是谁|介绍.*自己|自我介绍|who are you/i.test(text),
                answer: "我是认养智能体，一个专门帮你理解校园猫咪认养流程的小助手。当前对话只在这个页面临时进行，刷新后不会保存记录。"
            },
            {
                test: text => /你会做什么|能做什么|可以做什么|功能|what can you do/i.test(text),
                answer: "我可以帮你梳理认养前准备、申请材料怎么写、到家适应期怎么照护、回访照片要拍什么，也能帮你快速跳转查看可认养猫咪和公告。"
            }
        ].filter(() => false);
        const append = (role, content) => {
            const item = document.createElement("div");
            item.className = "agent-message";
            item.dataset.role = role;
            const avatar = document.createElement("span");
            avatar.className = "agent-avatar";
            avatar.setAttribute("aria-hidden", "true");
            avatar.textContent = role === "user" ? "喵" : "AI";
            const bubble = document.createElement("p");
            bubble.className = "agent-bubble";
            bubble.textContent = content;
            item.append(avatar, bubble);
            dialog.appendChild(item);
            dialog.scrollTop = dialog.scrollHeight;
            if (role === "user" || role === "assistant") {
                history.push({ role, content });
                if (history.length > 8) history.shift();
            }
            return bubble;
        };
        const escapeHtml = value => String(value || "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
        const buildThinkingSteps = question => {
            const value = String(question || "").trim();
            const shortTopic = value.length > 22 ? value.slice(0, 22) + "..." : value;
            if (/\u4f5c\u6587|\u6587\u7ae0|\u6587\u6848|\u5199|essay|article|copy/i.test(value)) {
                return [
                    `\u56f4\u7ed5\u201c${shortTopic}\u201d\u786e\u5b9a\u5199\u4f5c\u4e3b\u9898`,
                    "\u5b89\u6392\u5f00\u5934\u3001\u5c55\u5f00\u548c\u7ed3\u5c3e\u7684\u7ed3\u6784",
                    "\u8865\u5145\u9002\u5408\u7684\u7ec6\u8282\u548c\u8868\u8fbe",
                    "\u8f93\u51fa\u8fde\u8d2f\u7684\u6210\u6587"
                ];
            }
            if (/\u8ba4\u517b|\u9886\u517b|\u732b|\u7533\u8bf7|\u9002\u5e94\u671f|adopt|cat/i.test(value)) {
                return [
                    `\u8bc6\u522b\u95ee\u9898\u4e2d\u7684\u8ba4\u517b\u573a\u666f\uff1a${shortTopic}`,
                    "\u533a\u5206\u5fc5\u5907\u6761\u4ef6\u548c\u5efa\u8bae\u51c6\u5907",
                    "\u6309\u6d41\u7a0b\u3001\u7528\u54c1\u3001\u7167\u62a4\u8981\u70b9\u6574\u7406",
                    "\u7ed9\u51fa\u5bb9\u6613\u6267\u884c\u7684\u56de\u7b54"
                ];
            }
            if (/\u4ee3\u7801|\u7a0b\u5e8f|bug|\u62a5\u9519|code|error|api/i.test(value)) {
                return [
                    `\u5224\u65ad\u6280\u672f\u95ee\u9898\u7684\u76ee\u6807\uff1a${shortTopic}`,
                    "\u68b3\u7406\u53ef\u80fd\u539f\u56e0\u548c\u5b9e\u73b0\u8def\u5f84",
                    "\u4f18\u5148\u7ed9\u51fa\u53ef\u64cd\u4f5c\u7684\u4fee\u6539\u6216\u6392\u67e5\u6b65\u9aa4",
                    "\u7ec4\u7ec7\u6210\u6e05\u6670\u7684\u6280\u672f\u56de\u7b54"
                ];
            }
            if (/\u6e05\u5355|\u8ba1\u5212|\u6b65\u9aa4|\u600e\u4e48|how|plan|list/i.test(value)) {
                return [
                    `\u62c6\u89e3\u4f60\u8981\u89e3\u51b3\u7684\u4e8b\uff1a${shortTopic}`,
                    "\u5148\u5217\u51fa\u6838\u5fc3\u8981\u70b9",
                    "\u518d\u6309\u4f18\u5148\u7ea7\u548c\u6267\u884c\u987a\u5e8f\u6392\u5217",
                    "\u751f\u6210\u4fbf\u4e8e\u76f4\u63a5\u4f7f\u7528\u7684\u7ed3\u679c"
                ];
            }
            return [
                `\u7406\u89e3\u95ee\u9898\u7684\u6838\u5fc3\u610f\u56fe\uff1a${shortTopic}`,
                "\u63d0\u53d6\u5df2\u77e5\u4fe1\u606f\u548c\u9690\u542b\u9700\u6c42",
                "\u9009\u62e9\u5408\u9002\u7684\u56de\u7b54\u89d2\u5ea6",
                "\u7ec4\u7ec7\u6210\u81ea\u7136\u3001\u6e05\u695a\u7684\u56de\u7b54"
            ];
        };
        const buildThoughtSummary = (question, answer) => {
            const value = String(question || "").trim();
            const reply = String(answer || "").trim();
            const shortTopic = value.length > 28 ? value.slice(0, 28) + "..." : value;
            const replyHint = reply.length > 42 ? reply.slice(0, 42) + "..." : reply;
            if (/\u4f5c\u6587|\u6587\u7ae0|\u6587\u6848|\u5199|essay|article|copy/i.test(value)) {
                return `\u601d\u8003\u6458\u8981\uff1a\u8fd9\u4e2a\u95ee\u9898\u7684\u6838\u5fc3\u662f\u5b8c\u6210\u5199\u4f5c\u4efb\u52a1\u201c${shortTopic}\u201d\uff0c\u56e0\u6b64\u56de\u7b54\u4f18\u5148\u56f4\u7ed5\u4e3b\u9898\u3001\u6587\u7ae0\u7ed3\u6784\u548c\u8868\u8fbe\u8fde\u8d2f\u6027\u5c55\u5f00\uff0c\u5e76\u5c3d\u91cf\u8ba9\u6210\u6587\u53ef\u76f4\u63a5\u4f7f\u7528\u3002`;
            }
            if (/\u8ba4\u517b|\u9886\u517b|\u732b|\u7533\u8bf7|\u9002\u5e94\u671f|adopt|cat/i.test(value)) {
                return `\u601d\u8003\u6458\u8981\uff1a\u8fd9\u4e2a\u95ee\u9898\u4e0e\u8ba4\u517b\u573a\u666f\u76f8\u5173\uff0c\u6240\u4ee5\u56de\u7b54\u4f18\u5148\u533a\u5206\u5fc5\u5907\u4e8b\u9879\u548c\u5efa\u8bae\u4e8b\u9879\uff0c\u518d\u6309\u6d41\u7a0b\u3001\u7528\u54c1\u3001\u7167\u62a4\u548c\u98ce\u9669\u63d0\u9192\u7ec4\u7ec7\u6210\u5bb9\u6613\u6267\u884c\u7684\u5185\u5bb9\u3002`;
            }
            if (/\u4ee3\u7801|\u7a0b\u5e8f|bug|\u62a5\u9519|code|error|api/i.test(value)) {
                return `\u601d\u8003\u6458\u8981\uff1a\u8fd9\u4e2a\u95ee\u9898\u504f\u6280\u672f\u6392\u67e5\u6216\u5b9e\u73b0\uff0c\u56e0\u6b64\u56de\u7b54\u4f1a\u5148\u9501\u5b9a\u76ee\u6807\uff0c\u518d\u628a\u53ef\u80fd\u539f\u56e0\u3001\u4fee\u6539\u65b9\u5411\u548c\u9a8c\u8bc1\u6b65\u9aa4\u653e\u5230\u66f4\u53ef\u64cd\u4f5c\u7684\u987a\u5e8f\u91cc\u3002`;
            }
            if (/\u6e05\u5355|\u8ba1\u5212|\u6b65\u9aa4|\u600e\u4e48|how|plan|list/i.test(value)) {
                return `\u601d\u8003\u6458\u8981\uff1a\u8fd9\u4e2a\u95ee\u9898\u9700\u8981\u53ef\u6267\u884c\u7684\u7ed3\u679c\uff0c\u6240\u4ee5\u56de\u7b54\u5148\u62c6\u89e3\u76ee\u6807\uff0c\u518d\u6309\u4f18\u5148\u7ea7\u6216\u884c\u52a8\u987a\u5e8f\u7ec4\u7ec7\uff0c\u907f\u514d\u53ea\u7ed9\u62bd\u8c61\u5efa\u8bae\u3002`;
            }
            return `\u601d\u8003\u6458\u8981\uff1a\u6211\u5148\u5224\u65ad\u4f60\u7684\u6838\u5fc3\u95ee\u9898\u662f\u201c${shortTopic}\u201d\uff0c\u518d\u6839\u636e\u95ee\u9898\u9700\u8981\u9009\u62e9\u76f4\u63a5\u56de\u7b54\u3001\u5206\u6b65\u8bf4\u660e\u6216\u8865\u5145\u80cc\u666f\u3002\u6700\u7ec8\u56de\u7b54\u56f4\u7ed5\u201c${replyHint}\u201d\u8fd9\u4e2a\u65b9\u5411\u7ec4\u7ec7\u3002`;
        };
        const ask = async question => {
            const text = String(question || "").trim();
            if (!text) return;
            input.value = "";
            const previousHistory = history.slice();
            append("user", text);
            const thinking = append("assistant", "正在思考...");
            form.querySelector("button").disabled = true;
            thinking.classList.add("is-thinking", "has-running-cat");
            thinking.innerHTML = `
                <img class="agent-running-cat" src="/assets/running-cat.gif" alt="" aria-hidden="true">
                <span class="agent-thinking-text">\u6b63\u5728\u52aa\u529b\u601d\u8003\u4e2d</span>
            `;
            try {
                const clean = value => String(value || "")
                    .replace(/\*\*/g, "")
                    .replace(/^\s*[-*]\s+/gm, "")
                    .replace(/`{1,3}/g, "");
                const response = await fetch("/api/agent/adoption-chat-stream", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ message: text, history: previousHistory })
                });
                if (!response.ok || !response.body) {
                    throw new Error("Agent is temporarily unavailable. Please try again later.");
                }
                const reader = response.body.getReader();
                const decoder = new TextDecoder("utf-8");
                let answer = "";
                const finishThinking = () => {
                    thinking.classList.remove("is-thinking", "has-running-cat");
                };
                thinking.textContent = "";
                while (true) {
                    const { value, done } = await reader.read();
                    if (done) break;
                    answer += decoder.decode(value, { stream: true });
                    finishThinking();
                    thinking.textContent = clean(answer);
                    dialog.scrollTop = dialog.scrollHeight;
                }
                finishThinking();
                thinking.textContent = clean(answer) || "No valid answer was generated.";
                const thoughtSummary = buildThoughtSummary(text, thinking.textContent);
                if (thoughtSummary && !thinking.nextElementSibling?.classList?.contains("agent-thought-detail")) {
                    const detail = document.createElement("details");
                    detail.className = "agent-thought-detail";
                    detail.innerHTML = `<summary>&#26597;&#30475;&#24605;&#32771;&#25688;&#35201;</summary><p>${escapeHtml(thoughtSummary)}</p>`;
                    thinking.after(detail);
                }
                history[history.length - 1] = { role: "assistant", content: thinking.textContent };
                return;
                const local = localAnswers.find(item => item.test(text));
                if (local) {
                    thinking.textContent = local.answer;
                    history[history.length - 1] = { role: "assistant", content: local.answer };
                    return;
                }
                const result = await api("/api/agent/adoption-chat", {
                    method: "POST",
                    body: JSON.stringify({ message: text, history: previousHistory })
                });
                thinking.textContent = result.answer || "我暂时没有生成有效回答。";
            } catch (error) {
                thinking.classList.remove("is-thinking", "has-running-cat");
                thinking.textContent = error.message || "Agent is temporarily unavailable. Please try again later.";
            } finally {
                form.querySelector("button").disabled = false;
                dialog.scrollTop = dialog.scrollHeight;
            }
        };
        form.addEventListener("submit", event => {
            event.preventDefault();
            ask(input.value);
        });
        document.querySelectorAll(".agent-quick [data-agent-question]").forEach(button => {
            button.addEventListener("click", () => ask(button.dataset.agentQuestion));
        });
        document.querySelectorAll("[data-agent-flip]").forEach(card => {
            card.addEventListener("click", () => {
                const active = card.classList.toggle("is-flipped");
                card.setAttribute("aria-pressed", String(active));
            });
        });
    }

    async function renderAdminNotices(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载公告...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            if (params.get("publishStatus")) query.set("publishStatus", params.get("publishStatus"));
            if (params.get("noticeType")) query.set("noticeType", params.get("noticeType"));
            const notices = await api(`/api/admin/notices${query.toString() ? `?${query}` : ""}`);
            panel.innerHTML = `
                <div class="mis-filter-row">
                    <select id="notice-status"><option value="">全部状态</option>${["DRAFT", "PUBLISHED", "OFFLINE"].map(v => `<option value="${v}" ${params.get("publishStatus") === v ? "selected" : ""}>${v}</option>`).join("")}</select>
                    <select id="notice-type"><option value="">全部类型</option>${["SYSTEM", "ADOPTION", "FOLLOWUP"].map(v => `<option value="${v}" ${params.get("noticeType") === v ? "selected" : ""}>${v}</option>`).join("")}</select>
                    <button class="ghost-btn" id="notice-filter">筛选</button>
                    <button class="primary-btn" id="notice-create">新增公告</button>
                </div>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>标题</th><th>类型</th><th>状态</th><th>排序</th><th>发布时间</th><th>操作</th></tr></thead><tbody>
                    ${(notices || []).map(item => `<tr><td>${escapeHtml(item.title)}<br><small>${escapeHtml(item.content)}</small></td><td>${escapeHtml(item.noticeType)}</td><td>${escapeHtml(item.publishStatus)}</td><td>${item.sortOrder || 0}</td><td>${escapeHtml((item.publishTime || "").replace("T", " "))}</td><td><button class="ghost-btn" data-notice-edit="${item.id}">编辑</button><button class="ghost-btn" data-notice-publish="${item.id}">发布</button><button class="ghost-btn" data-notice-offline="${item.id}">下架</button><button class="ghost-btn" data-notice-delete="${item.id}">删除</button></td></tr>`).join("") || `<tr><td colspan="6"><div class="mis-empty">暂无公告</div></td></tr>`}
                </tbody></table></div>
            `;
            document.getElementById("notice-filter").addEventListener("click", () => {
                const next = new URLSearchParams();
                if (document.getElementById("notice-status").value) next.set("publishStatus", document.getElementById("notice-status").value);
                if (document.getElementById("notice-type").value) next.set("noticeType", document.getElementById("notice-type").value);
                window.location.hash = `#/admin/notices${next.toString() ? `?${next}` : ""}`;
            });
            document.getElementById("notice-create").addEventListener("click", async () => saveNotice(route, user));
            shell.querySelectorAll("[data-notice-edit]").forEach(button => button.addEventListener("click", async () => saveNotice(route, user, button.dataset.noticeEdit)));
            shell.querySelectorAll("[data-notice-publish]").forEach(button => button.addEventListener("click", async () => { await api(`/api/admin/notices/${button.dataset.noticePublish}/publish`, { method: "PUT" }); renderAdminNotices(route, user); }));
            shell.querySelectorAll("[data-notice-offline]").forEach(button => button.addEventListener("click", async () => { await api(`/api/admin/notices/${button.dataset.noticeOffline}/offline`, { method: "PUT" }); renderAdminNotices(route, user); }));
            shell.querySelectorAll("[data-notice-delete]").forEach(button => button.addEventListener("click", async () => { if (confirm("确认删除该公告？")) { await api(`/api/admin/notices/${button.dataset.noticeDelete}`, { method: "DELETE" }); renderAdminNotices(route, user); } }));
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function saveNotice(route, user, id) {
        const title = prompt("公告标题");
        if (!title) return;
        const content = prompt("公告内容");
        if (!content) return;
        const noticeType = prompt("公告类型：SYSTEM / ADOPTION / FOLLOWUP", "SYSTEM") || "SYSTEM";
        const publishStatus = prompt("发布状态：DRAFT / PUBLISHED / OFFLINE", "DRAFT") || "DRAFT";
        const sortOrder = Number(prompt("排序值", "0") || "0");
        const sendMessage = confirm("是否同步发送站内消息？");
        const body = { title, content, noticeType, publishStatus, sortOrder, sendMessage };
        await api(id ? `/api/admin/notices/${id}` : "/api/admin/notices", { method: id ? "PUT" : "POST", body: JSON.stringify(body) });
        renderAdminNotices(route, user);
    }

    async function renderAdminDicts(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载字典...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const dictType = params.get("dictType") || "";
            const items = await api(`/api/admin/dicts${dictType ? `?dictType=${dictType}` : ""}`);
            panel.innerHTML = `
                <div class="mis-filter-row"><input id="dict-type-filter" value="${escapeHtml(dictType)}" placeholder="字典类型"><button class="ghost-btn" id="dict-filter">筛选</button><button class="primary-btn" id="dict-create">新增字典项</button></div>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>类型</th><th>标签</th><th>值</th><th>排序</th><th>启用</th><th>备注</th><th>操作</th></tr></thead><tbody>
                    ${(items || []).map(item => `<tr><td>${escapeHtml(item.dictType)}</td><td>${escapeHtml(item.dictLabel)}</td><td>${escapeHtml(item.dictValue)}</td><td>${item.sortOrder || 0}</td><td>${item.enabled ? "是" : "否"}</td><td>${escapeHtml(item.remark || "")}</td><td><button class="ghost-btn" data-dict-edit="${item.id}">编辑</button><button class="ghost-btn" data-dict-toggle="${item.id}" data-enabled="${!item.enabled}">${item.enabled ? "停用" : "启用"}</button><button class="ghost-btn" data-dict-delete="${item.id}">删除</button></td></tr>`).join("") || `<tr><td colspan="7"><div class="mis-empty">暂无字典项</div></td></tr>`}
                </tbody></table></div>
            `;
            document.getElementById("dict-filter").addEventListener("click", () => {
                const value = document.getElementById("dict-type-filter").value;
                window.location.hash = value ? `#/admin/dicts?dictType=${encodeURIComponent(value)}` : "#/admin/dicts";
            });
            document.getElementById("dict-create").addEventListener("click", async () => saveDict(route, user, null, dictType));
            shell.querySelectorAll("[data-dict-edit]").forEach(button => button.addEventListener("click", async () => saveDict(route, user, button.dataset.dictEdit, dictType)));
            shell.querySelectorAll("[data-dict-toggle]").forEach(button => button.addEventListener("click", async () => { await api(`/api/admin/dicts/${button.dataset.dictToggle}/enabled?enabled=${button.dataset.enabled}`, { method: "PUT" }); renderAdminDicts(route, user); }));
            shell.querySelectorAll("[data-dict-delete]").forEach(button => button.addEventListener("click", async () => { if (confirm("确认删除该字典项？")) { await api(`/api/admin/dicts/${button.dataset.dictDelete}`, { method: "DELETE" }); renderAdminDicts(route, user); } }));
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    async function saveDict(route, user, id, defaultType) {
        const dictType = prompt("字典类型", defaultType || "CAT_STATUS");
        if (!dictType) return;
        const dictLabel = prompt("显示标签");
        if (!dictLabel) return;
        const dictValue = prompt("字典值");
        if (!dictValue) return;
        const sortOrder = Number(prompt("排序值", "0") || "0");
        const enabled = confirm("是否启用？");
        const remark = prompt("备注", "") || "";
        await api(id ? `/api/admin/dicts/${id}` : "/api/admin/dicts", { method: id ? "PUT" : "POST", body: JSON.stringify({ dictType, dictLabel, dictValue, sortOrder, enabled, remark }) });
        renderAdminDicts(route, user);
    }

    async function renderAdminLogs(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载操作日志...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            ["operatorKeyword", "operationType", "bizType", "keyword"].forEach(key => { if (params.get(key)) query.set(key, params.get(key)); });
            const logs = await api(`/api/admin/logs${query.toString() ? `?${query}` : ""}`);
            panel.innerHTML = `
                <div class="mis-filter-row"><input id="log-operator" value="${escapeHtml(params.get("operatorKeyword") || "")}" placeholder="操作人"><input id="log-type" value="${escapeHtml(params.get("operationType") || "")}" placeholder="操作类型"><input id="log-biz" value="${escapeHtml(params.get("bizType") || "")}" placeholder="业务类型"><input id="log-keyword" value="${escapeHtml(params.get("keyword") || "")}" placeholder="关键词"><button class="ghost-btn" id="log-filter">筛选</button></div>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>时间</th><th>操作人</th><th>操作</th><th>业务</th><th>对象</th><th>状态</th><th>操作</th></tr></thead><tbody>
                    ${(logs || []).map(item => `<tr><td>${escapeHtml((item.createTime || "").replace("T", " "))}</td><td>${escapeHtml(item.operatorName || item.operatorId || "-")}</td><td>${escapeHtml(item.operationType)}</td><td>${escapeHtml(item.bizType)}</td><td>${escapeHtml(item.bizId)}</td><td>${escapeHtml(item.status)}</td><td><button class="ghost-btn" data-log-detail="${item.id}">详情</button></td></tr>`).join("") || `<tr><td colspan="7"><div class="mis-empty">暂无日志</div></td></tr>`}
                </tbody></table></div>
            `;
            document.getElementById("log-filter").addEventListener("click", () => {
                const next = new URLSearchParams();
                [["operatorKeyword", "log-operator"], ["operationType", "log-type"], ["bizType", "log-biz"], ["keyword", "log-keyword"]].forEach(([key, id]) => {
                    const value = document.getElementById(id).value;
                    if (value) next.set(key, value);
                });
                window.location.hash = `#/admin/logs${next.toString() ? `?${next}` : ""}`;
            });
            shell.querySelectorAll("[data-log-detail]").forEach(button => button.addEventListener("click", async () => {
                const item = await api(`/api/admin/logs/${button.dataset.logDetail}`);
                alert(`操作人：${item.operatorName || item.operatorId || "-"}\n操作类型：${item.operationType}\n业务：${item.bizType}/${item.bizId}\n前值：${item.beforeData || "-"}\n后值：${item.afterData || "-"}\n备注：${item.remark || "-"}`);
            }));
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function renderPlaceholder(route, user, isAdmin) {
        if (isAdmin) {
            adminShell(route, user, `${adminHero(route)}${placeholderGrid()}`);
        } else {
            userShell(route, user, `${pageHero(route)}${placeholderGrid()}`);
        }
    }

    function loginHashFor(hash) {
        return `#/login?redirect=${encodeURIComponent(hash)}`;
    }

    function render() {
        const hash = normalizeHash();
        if (!hash) {
            window.location.hash = "#/";
            return;
        }

        if (!authChecked) {
            document.body.classList.add("mis-active");
            shell.innerHTML = `<div class="mis-user-layout"><main class="mis-user-main"><div class="mis-loading">正在校验登录状态...</div></main></div>`;
            verifyAuth().then(render);
            return;
        }

        const user = currentUser();
        const cleanHash = hash.split("?")[0];
        const route = cleanHash === "#/403" ? null : findRoute(cleanHash);
        document.body.classList.add("mis-active");

        if (cleanHash === "#/403") {
            renderForbidden(user);
            return;
        }

        if (!route) {
            window.location.hash = "#/";
            return;
        }

        const isAdmin = route.path.startsWith("#/admin");
        if (isAdmin && !user) {
            window.location.hash = loginHashFor(hash);
            return;
        }
        if (isAdmin && (normalizeRole(user.role) === "STUDENT" || !canVisit(route, user.role))) {
            renderForbidden(user);
            return;
        }

        if (!isAdmin && route.roles && user && !canVisit(route, user.role)) {
            renderForbidden(user);
            return;
        }

        if (isPrivateFrontRoute(route) && !user) {
            window.location.hash = loginHashFor(hash);
            return;
        }

        if (route.path === "#/") {
            renderHome(route, user);
        } else if (route.path === "#/login") {
            renderLogin(route, user);
        } else if (route.path === "#/register") {
            renderRegister(route, user);
        } else if (route.path === "#/data-screen") {
            renderDataScreen(route, user);
        } else if (route.path === "#/notices") {
            renderNotices(route, user);
        } else if (route.path === "#/notices/:id") {
            renderNoticeDetail(route, user);
        } else if (route.path === "#/clues/submit") {
            renderClueSubmit(route, user);
        } else if (route.path === "#/cats") {
            renderPublicCats(route, user);
        } else if (route.path === "#/cats/:id") {
            renderPublicCatDetail(route, user);
        } else if (route.path === "#/adoption/apply/:catId") {
            renderAdoptionApply(route, user);
        } else if (route.path === "#/my/clues") {
            renderMyClues(route, user);
        } else if (route.path === "#/my/applications") {
            renderMyApplications(route, user);
        } else if (route.path === "#/my/followups") {
            renderMyFollowups(route, user);
        } else if (route.path === "#/my/messages") {
            renderMyMessages(route, user);
        } else if (route.path === "#/hospital") {
            renderHospitalPortal(route, user);
        } else if (route.path === "#/profile") {
            renderProfile(route, user);
        } else if (route.path === "#/admin/dashboard") {
            renderAdminDashboard(route, user);
        } else if (route.path === "#/admin/hospital") {
            renderAdminHospital(route, user);
        } else if (route.path === "#/admin/clues") {
            renderAdminClues(route, user);
        } else if (route.path === "#/admin/adoption/audits") {
            renderAdminAdoptionAudits(route, user);
        } else if (route.path === "#/admin/cats") {
            renderAdminCats(route, user);
        } else if (route.path === "#/admin/cats/:id") {
            renderAdminCatDetail(route, user);
        } else if (route.path === "#/admin/medical") {
            renderAdminMedical(route, user);
        } else if (route.path === "#/admin/agreements") {
            renderAdminAgreements(route, user);
        } else if (route.path === "#/admin/followups") {
            renderAdminFollowups(route, user);
        } else if (route.path === "#/admin/warnings") {
            renderAdminWarnings(route, user);
        } else if (route.path === "#/admin/users") {
            renderAdminUsers(route, user);
        } else if (route.path === "#/admin/notices") {
            renderAdminNotices(route, user);
        } else if (route.path === "#/admin/dicts") {
            renderAdminDicts(route, user);
        } else if (route.path === "#/admin/logs") {
            renderAdminLogs(route, user);
        } else {
            renderPlaceholder(route, user, isAdmin);
        }
    }

    window.addEventListener("hashchange", render);
    window.addEventListener("storage", render);
    render();
})();
