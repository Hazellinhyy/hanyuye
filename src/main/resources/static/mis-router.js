(function () {
    // 前端主入口：所有页面都渲染到 mis-shell，形成不依赖构建工具的单页应用。
    const shell = document.getElementById("mis-shell");
    if (!shell) {
        return;
    }

    let screenClockTimer = null;
    let localeObserver = null;
    const localeStorageKey = "hfut-cat-locale";
    const supportedLocales = {
        zh: "中文",
        en: "English",
        ja: "日本語",
        ko: "한국어"
    };
    let currentLocale = localStorage.getItem(localeStorageKey) || "zh";
    const nativeAlert = window.alert.bind(window);
    const nativeConfirm = window.confirm.bind(window);
    const nativePrompt = window.prompt.bind(window);

    // 多语言词典只负责演示界面文案翻译，业务数据中的编号和用户输入不会被翻译。
    const i18nPacks = {
        en: {
            "后台管理系统": "Admin System",
            "校园流浪猫认养门户": "Campus Stray Cat Adoption Portal",
            "返回前台": "Front Portal",
            "退出": "Log out",
            "登录": "Log in",
            "注册": "Register",
            "后台": "Admin",
            "门户首页": "Portal",
            "数据大屏": "Data Screen",
            "可认养猫咪": "Adoptable Cats",
            "公告": "Notices",
            "上报线索": "Submit Clue",
            "我的线索": "My Clues",
            "我的申请": "My Applications",
            "我的回访": "My Follow-ups",
            "我的消息": "Messages",
            "医疗协作": "Medical Partner",
            "个人中心": "Profile",
            "后台首页": "Dashboard",
            "线索核实": "Clue Review",
            "猫咪档案": "Cat Records",
            "医疗工作台": "Medical Desk",
            "申请审核": "Application Review",
            "协议交接": "Agreement Handover",
            "回访任务": "Follow-up Tasks",
            "异常预警": "Warnings",
            "用户角色": "Users & Roles",
            "公告管理": "Notice Management",
            "操作日志": "Operation Logs",
            "筛选": "Filter",
            "详情": "Details",
            "删除": "Delete",
            "处理": "Handle",
            "查看": "View",
            "编辑": "Edit",
            "新增": "Create",
            "导出 CSV": "Export CSV",
            "操作人": "Operator",
            "操作类型": "Operation Type",
            "业务类型": "Business Type",
            "关键词": "Keyword",
            "时间": "Time",
            "操作": "Operation",
            "业务": "Business",
            "对象": "Target",
            "状态": "Status",
            "变更前": "Before",
            "变更后": "After",
            "备注": "Remark",
            "提交回访记录": "Submit Follow-up Record",
            "工作人员填写回访记录": "Staff Follow-up Record",
            "认养人提交回访记录": "Adopter Follow-up Record",
            "自动生成认养协议": "Auto-generate Adoption Agreement",
            "删除异常预警": "Delete Warning",
            "异常预警": "Warning",
            "回访任务": "Follow-up Task",
            "认养协议": "Adoption Agreement",
            "公告": "Notice",
            "用户": "User",
            "猫咪": "Cat",
            "线索": "Clue",
            "医疗记录": "Medical Record",
            "认养申请": "Adoption Application",
            "已完成": "Completed",
            "待处理": "Pending",
            "处理中": "Processing",
            "已处理": "Handled",
            "已忽略": "Ignored",
            "已删除": "Deleted",
            "已生成": "Generated",
            "已发布": "Published",
            "已下架": "Offline",
            "草稿": "Draft",
            "暂无日志": "No logs",
            "正在加载操作日志...": "Loading operation logs...",
            "全局搜索猫咪、线索、申请、协议、预警": "Search cats, clues, applications, agreements, warnings"
        },
        ja: {
            "后台管理系统": "管理システム",
            "校园流浪猫认养门户": "キャンパス猫譲渡ポータル",
            "返回前台": "ポータルへ",
            "退出": "ログアウト",
            "登录": "ログイン",
            "注册": "登録",
            "后台": "管理",
            "操作日志": "操作ログ",
            "异常预警": "異常アラート",
            "回访任务": "フォローアップ",
            "协议交接": "契約引き渡し",
            "公告管理": "お知らせ管理",
            "用户角色": "ユーザー権限",
            "筛选": "絞り込み",
            "详情": "詳細",
            "删除": "削除",
            "处理": "処理",
            "操作人": "操作者",
            "操作类型": "操作種別",
            "业务类型": "業務種別",
            "关键词": "キーワード",
            "时间": "時間",
            "操作": "操作",
            "业务": "業務",
            "对象": "対象",
            "状态": "状態",
            "变更前": "変更前",
            "变更后": "変更後",
            "备注": "備考",
            "提交回访记录": "フォロー記録を提出",
            "已完成": "完了",
            "已删除": "削除済み",
            "已生成": "生成済み",
            "暂无日志": "ログなし",
            "正在加载操作日志...": "操作ログを読み込み中..."
        },
        ko: {
            "后台管理系统": "관리 시스템",
            "校园流浪猫认养门户": "캠퍼스 길고양이 입양 포털",
            "返回前台": "포털로",
            "退出": "로그아웃",
            "登录": "로그인",
            "注册": "가입",
            "后台": "관리",
            "操作日志": "작업 로그",
            "异常预警": "이상 경고",
            "回访任务": "후속 방문",
            "协议交接": "계약 인계",
            "公告管理": "공지 관리",
            "用户角色": "사용자 역할",
            "筛选": "필터",
            "详情": "상세",
            "删除": "삭제",
            "处理": "처리",
            "操作人": "작업자",
            "操作类型": "작업 유형",
            "业务类型": "업무 유형",
            "关键词": "키워드",
            "时间": "시간",
            "操作": "작업",
            "业务": "업무",
            "对象": "대상",
            "状态": "상태",
            "变更前": "변경 전",
            "变更后": "변경 후",
            "备注": "비고",
            "提交回访记录": "후속 기록 제출",
            "已完成": "완료",
            "已删除": "삭제됨",
            "已生成": "생성됨",
            "暂无日志": "로그 없음",
            "正在加载操作日志...": "작업 로그 로딩 중..."
        }
    };

    const i18nCommonPacks = {
        en: {
            "后台管理系统": "Admin System",
            "校园流浪猫认养门户": "Campus Stray Cat Adoption Portal",
            "返回前台": "Front Portal",
            "退出": "Log out",
            "登录": "Log in",
            "注册": "Register",
            "后台": "Admin",
            "门户首页": "Portal",
            "数据大屏": "Data Screen",
            "可认养猫咪": "Adoptable Cats",
            "公告": "Notices",
            "上报线索": "Submit Clue",
            "我的线索": "My Clues",
            "我的申请": "My Applications",
            "我的回访": "My Follow-ups",
            "我的消息": "Messages",
            "医疗协作": "Medical Partner",
            "个人中心": "Profile",
            "后台首页": "Dashboard",
            "线索核实": "Clue Review",
            "猫咪档案": "Cat Records",
            "医疗工作台": "Medical Desk",
            "申请审核": "Application Review",
            "协议交接": "Agreement Handover",
            "回访任务": "Follow-up Tasks",
            "异常预警": "Warnings",
            "用户角色": "Users & Roles",
            "公告管理": "Notice Management",
            "操作日志": "Operation Logs",
            "筛选": "Filter",
            "详情": "Details",
            "删除": "Delete",
            "处理": "Handle",
            "查看": "View",
            "编辑": "Edit",
            "新增": "Create",
            "保存": "Save",
            "取消": "Cancel",
            "提交": "Submit",
            "导出 CSV": "Export CSV",
            "操作人": "Operator",
            "操作类型": "Operation Type",
            "业务类型": "Business Type",
            "全部状态": "All Statuses",
            "全部类型": "All Types",
            "全部等级": "All Levels",
            "关键词": "Keyword",
            "时间": "Time",
            "操作": "Operation",
            "业务": "Business",
            "对象": "Target",
            "状态": "Status",
            "变更前": "Before",
            "变更后": "After",
            "备注": "Remark",
            "搜索": "Search",
            "搜索标题、猫咪、认养人": "Search title, cat, adopter",
            "搜索申请、协议、猫咪、认养人": "Search application, agreement, cat, adopter",
            "搜索编号、昵称、地点": "Search ID, name, location",
            "搜索申请人、协议、猫咪、认养人": "Search applicant, agreement, cat, adopter",
            "正在加载操作日志...": "Loading operation logs...",
            "暂无日志": "No logs",
            "暂无数据": "No data",
            "已完成": "Completed",
            "待处理": "Pending",
            "处理中": "Processing",
            "已处理": "Handled",
            "已忽略": "Ignored",
            "已删除": "Deleted",
            "已生成": "Generated",
            "已发布": "Published",
            "已下架": "Offline",
            "已取消": "Cancelled",
            "已作废": "Voided",
            "待交接": "Pending Handover",
            "已交接": "Handed Over",
            "草稿": "Draft",
            "自动生成认养协议": "Auto-generate Adoption Agreement",
            "生成认养协议": "Generate Adoption Agreement",
            "编辑认养协议": "Edit Adoption Agreement",
            "删除异常预警": "Delete Warning",
            "处理异常预警": "Handle Warning",
            "更新公告": "Update Notice",
            "删除公告": "Delete Notice",
            "新增公告": "Create Notice",
            "发布公告": "Publish Notice",
            "下架公告": "Offline Notice",
            "认养协议": "Adoption Agreement",
            "回访任务": "Follow-up Task",
            "异常预警": "Warning",
            "用户": "User",
            "猫咪": "Cat",
            "线索": "Clue",
            "医疗记录": "Medical Record",
            "认养申请": "Adoption Application",
            "回访异常": "Abnormal Follow-up",
            "回访逾期": "Overdue Follow-up",
            "健康异常": "Health Abnormality",
            "高风险申请预警": "High-risk Application Warning",
            "普通用户": "User",
            "志愿者": "Volunteer",
            "管理员": "Admin",
            "合作医院": "Partner Hospital",
            "医院工作台": "Hospital Desk",
            "管理后台": "Admin Panel"
        },
        ja: {
            "后台管理系统": "管理システム",
            "校园流浪猫认养门户": "キャンパス猫譲渡ポータル",
            "返回前台": "ポータルへ戻る",
            "退出": "ログアウト",
            "登录": "ログイン",
            "注册": "登録",
            "后台": "管理",
            "门户首页": "ポータル",
            "数据大屏": "データ画面",
            "可认养猫咪": "譲渡可能な猫",
            "公告": "お知らせ",
            "上报线索": "情報を送信",
            "我的线索": "自分の情報",
            "我的申请": "自分の申請",
            "我的回访": "自分のフォロー",
            "我的消息": "メッセージ",
            "医疗协作": "医療連携",
            "个人中心": "プロフィール",
            "后台首页": "ダッシュボード",
            "线索核实": "情報確認",
            "猫咪档案": "猫台帳",
            "医疗工作台": "医療デスク",
            "申请审核": "申請審査",
            "协议交接": "契約引渡し",
            "回访任务": "フォロータスク",
            "异常预警": "異常アラート",
            "用户角色": "ユーザーと権限",
            "公告管理": "お知らせ管理",
            "操作日志": "操作ログ",
            "筛选": "絞り込み",
            "详情": "詳細",
            "删除": "削除",
            "处理": "処理",
            "查看": "表示",
            "编辑": "編集",
            "新增": "作成",
            "保存": "保存",
            "取消": "取消",
            "提交": "送信",
            "导出 CSV": "CSV出力",
            "操作人": "操作ユーザー",
            "操作类型": "操作種別",
            "业务类型": "業務種別",
            "全部状态": "すべての状態",
            "关键词": "キーワード",
            "时间": "時間",
            "操作": "操作",
            "业务": "業務",
            "对象": "対象",
            "状态": "状態",
            "变更前": "変更前",
            "变更后": "変更後",
            "备注": "備考",
            "已完成": "完了",
            "待处理": "未処理",
            "处理中": "処理中",
            "已处理": "処理済み",
            "已忽略": "無視済み",
            "已删除": "削除済み",
            "已生成": "生成済み",
            "已发布": "公開済み",
            "已下架": "非公開",
            "已取消": "取消済み",
            "已作废": "無効",
            "待交接": "引渡し待ち",
            "已交接": "引渡し済み",
            "草稿": "下書き",
            "认养协议": "譲渡契約",
            "回访任务": "フォロータスク",
            "异常预警": "異常アラート",
            "用户": "ユーザー",
            "猫咪": "猫",
            "线索": "情報",
            "医疗记录": "医療記録",
            "认养申请": "譲渡申請"
        },
        ko: {
            "后台管理系统": "관리 시스템",
            "校园流浪猫认养门户": "캠퍼스 고양이 입양 포털",
            "返回前台": "포털로 돌아가기",
            "退出": "로그아웃",
            "登录": "로그인",
            "注册": "가입",
            "后台": "관리",
            "门户首页": "포털",
            "数据大屏": "데이터 화면",
            "可认养猫咪": "입양 가능한 고양이",
            "公告": "공지",
            "上报线索": "제보 등록",
            "我的线索": "내 제보",
            "我的申请": "내 신청",
            "我的回访": "내 사후관리",
            "我的消息": "메시지",
            "医疗协作": "의료 협업",
            "个人中心": "프로필",
            "后台首页": "대시보드",
            "线索核实": "제보 확인",
            "猫咪档案": "고양이 기록",
            "医疗工作台": "의료 데스크",
            "申请审核": "신청 심사",
            "协议交接": "계약 인계",
            "回访任务": "사후관리 작업",
            "异常预警": "이상 경고",
            "用户角色": "사용자 및 권한",
            "公告管理": "공지 관리",
            "操作日志": "작업 로그",
            "筛选": "필터",
            "详情": "상세",
            "删除": "삭제",
            "处理": "처리",
            "查看": "보기",
            "编辑": "편집",
            "新增": "생성",
            "保存": "저장",
            "取消": "취소",
            "提交": "제출",
            "导出 CSV": "CSV 내보내기",
            "操作人": "작업자",
            "操作类型": "작업 유형",
            "业务类型": "업무 유형",
            "全部状态": "전체 상태",
            "关键词": "키워드",
            "时间": "시간",
            "操作": "작업",
            "业务": "업무",
            "对象": "대상",
            "状态": "상태",
            "变更前": "변경 전",
            "变更后": "변경 후",
            "备注": "비고",
            "已完成": "완료",
            "待处理": "대기",
            "处理中": "처리 중",
            "已处理": "처리됨",
            "已忽略": "무시됨",
            "已删除": "삭제됨",
            "已生成": "생성됨",
            "已发布": "게시됨",
            "已下架": "비공개",
            "已取消": "취소됨",
            "已作废": "무효",
            "待交接": "인계 대기",
            "已交接": "인계 완료",
            "草稿": "초안",
            "认养协议": "입양 계약",
            "回访任务": "사후관리 작업",
            "异常预警": "이상 경고",
            "用户": "사용자",
            "猫咪": "고양이",
            "线索": "제보",
            "医疗记录": "의료 기록",
            "认养申请": "입양 신청"
        }
    };

    const i18nFallbackPacks = {
        en: {
            "合肥工业大学校园流浪猫在线认养系统": "HFUT Campus Stray Cat Online Adoption System",
            "校园公益认养与流浪猫全生命周期管理平台": "Campus public-welfare adoption and stray cat lifecycle management platform",
            "从发现线索到安心到家": "From discovery clue to safe arrival home",
            "从发现线索到长期回访": "From discovery clue to long-term follow-up",
            "登录后默认进入前台门户": "After login, you enter the front portal by default",
            "医院用户和管理员可从前台入口进入对应后台": "Hospital users and admins can enter their workbench from the front portal",
            "请在提交申请或处理业务前先查看最新公告": "Please read the latest notices before submitting applications or handling tasks",
            "查看本人提交线索的核实和建档进度": "View verification and filing progress for your submitted clues",
            "查看待回访任务并提交回访反馈": "View pending follow-up tasks and submit feedback",
            "提交居住环境和照护经验": "Submit housing environment and care experience",
            "协议生成与线下交接登记": "Agreement generation and offline handover registration",
            "异常或逾期自动进入预警": "Abnormal or overdue items automatically become warnings",
            "正在加载门户数据...": "Loading portal data...",
            "正在加载可认养猫咪...": "Loading adoptable cats...",
            "正在加载猫咪详情...": "Loading cat details...",
            "正在加载申请表...": "Loading application form...",
            "正在加载我的申请...": "Loading my applications...",
            "正在加载我的线索...": "Loading my clues...",
            "正在加载我的回访任务...": "Loading my follow-up tasks...",
            "正在加载回访任务详情...": "Loading follow-up task details...",
            "正在加载站内消息...": "Loading messages...",
            "正在加载个人中心...": "Loading profile...",
            "正在加载后台首页...": "Loading dashboard...",
            "正在加载猫咪档案...": "Loading cat records...",
            "正在加载医疗工作台...": "Loading medical desk...",
            "正在加载认养申请...": "Loading adoption applications...",
            "正在加载协议交接记录...": "Loading agreement handover records...",
            "正在加载回访任务...": "Loading follow-up tasks...",
            "正在加载异常预警...": "Loading warnings...",
            "正在加载用户...": "Loading users...",
            "正在加载公告...": "Loading notices...",
            "正在加载公告详情...": "Loading notice details...",
            "正在加载公告发布台...": "Loading notice editor...",
            "正在加载实时数据大屏...": "Loading real-time data screen...",
            "正在校验登录状态...": "Checking login status...",
            "当前账号无权执行此操作": "The current account is not allowed to perform this operation",
            "当前不能进入该页面": "You cannot enter this page now",
            "请先登录后再访问需要权限的后台页面": "Please log in before visiting protected admin pages",
            "当前猫咪不是可认养状态": "This cat is not currently adoptable",
            "确认删除该异常预警": "Confirm deleting this warning?",
            "删除后列表不再显示": "It will no longer appear in the list after deletion",
            "确认删除该公告？": "Confirm deleting this notice?",
            "确认作废该未交接协议": "Confirm voiding this unhanded agreement?",
            "确认删除该未交接协议": "Confirm deleting this unhanded agreement?",
            "确认作废该医疗记录": "Confirm voiding this medical record?",
            "确认作废该认养申请": "Confirm voiding this adoption application?",
            "确认逻辑删除该线索": "Confirm logically deleting this clue?",
            "确认归档该猫咪档案": "Confirm archiving this cat record?",
            "请先选择一张图片": "Please select an image first",
            "图片不能超过 5MB": "Image must not exceed 5 MB",
            "照片不能超过 5MB": "Photo must not exceed 5 MB",
            "请先点击上传附件图片": "Please upload the attachment image first",
            "请先点击上传回访照片": "Please upload the follow-up photo first",
            "上传完成后再保存记录": "Save the record after upload is complete",
            "上传完成后再保存医疗记录": "Save the medical record after upload is complete",
            "图片还没有上传成功，请先上传完成后再保存公告": "The image has not uploaded successfully; finish upload before saving the notice",
            "正在上传": "Uploading",
            "图片已上传，保存公告后生效": "Image uploaded; it takes effect after saving the notice",
            "已移除图片，保存公告后生效": "Image removed; it takes effect after saving the notice",
            "公告已保存": "Notice saved",
            "公告已发布": "Notice published",
            "公告已下架": "Notice offlined",
            "公告已删除": "Notice deleted",
            "正在保存...": "Saving...",
            "正在发布...": "Publishing...",
            "正在下架...": "Offlining...",
            "正在删除...": "Deleting...",
            "发布范围至少选择一个角色": "Select at least one target role",
            "请至少选择一个发布范围": "Please select at least one publishing scope",
            "公告会同步显示到前台公告页": "The notice will also appear on the front notice page",
            "发布新公告": "Publish New Notice",
            "编辑公告": "Edit Notice",
            "保存公告": "Save Notice",
            "新增公告": "Create Notice",
            "取消编辑": "Cancel Editing",
            "公告列表": "Notice List",
            "公告图片": "Notice Image",
            "公告图片预览": "Notice image preview",
            "暂无公告图片": "No notice image",
            "上传/更换图片": "Upload/Replace Image",
            "移除图片": "Remove Image",
            "公告内容": "Notice Content",
            "填写公告正文，建议包含时间、地点、对象和注意事项": "Write notice content, preferably including time, place, audience, and notes",
            "例如：本周认养开放日安排": "Example: This week's adoption open day schedule",
            "系统公告": "System Notice",
            "认养公告": "Adoption Notice",
            "回访提醒": "Follow-up Reminder",
            "全部角色": "All Roles",
            "普通用户": "Regular User",
            "志愿者": "Volunteer",
            "合作医院": "Partner Hospital",
            "管理员": "Admin",
            "校园流浪猫实时数据驾驶舱": "Campus Stray Cat Real-time Data Cockpit",
            "猫咪状态矩阵": "Cat Status Matrix",
            "申请状态监控": "Application Status Monitor",
            "认养智能体": "Adoption Assistant",
            "你好，我是认养智能体。可以问我认养前准备、申请怎么写、猫咪到家适应期、回访照片要求等问题。": "Hello, I am the adoption assistant. You can ask about preparation, applications, adaptation after bringing a cat home, and follow-up photo requirements.",
            "输入你的认养问题...": "Enter your adoption question...",
            "想问什么都可以，直接输入你的问题...": "Ask anything; type your question directly...",
            "发送": "Send",
            "查看可认养": "View Adoptable Cats",
            "同步公告": "Synced Notices",
            "常见问题": "FAQ",
            "点击卡片，快速了解认养常见问题": "Click a card to quickly learn common adoption questions",
            "认养准备": "Adoption Preparation",
            "申请建议": "Application Advice",
            "适应期": "Adaptation Period",
            "回访要求": "Follow-up Requirements",
            "新手避坑": "Beginner Tips",
            "喂养护理": "Feeding and Care",
            "用品清单、预算评估、居家环境准备": "Supply list, budget estimate, and home preparation",
            "申请填写、自我介绍、提升通过率": "Application writing, self-introduction, and approval tips",
            "到家应激、隔离观察、作息适应": "Stress after arrival, isolation observation, and routine adaptation",
            "回访频率、拍照要求、注意事项": "Follow-up frequency, photo requirements, and notes",
            "常见误区、认养禁忌、错误做法提醒": "Common mistakes, adoption taboos, and incorrect practices",
            "饮食、猫砂、清洁、健康管理": "Diet, litter, cleaning, and health management",
            "点击查看答案": "Click to view answer",
            "点击翻回": "Click to flip back",
            "你是谁": "Who are you?",
            "你会做什么": "What can you do?",
            "正在思考...": "Thinking...",
            "智能体暂时不可用，请稍后再试。": "The assistant is temporarily unavailable. Please try again later.",
            "暂时没有生成有效回答。": "No valid answer was generated for now.",
            "我暂时没有生成有效回答。": "I could not generate a valid answer for now.",
            "认养前需要准备什么？": "What should I prepare before adoption?",
            "申请认养时怎么写更合适？": "How should I write an adoption application?",
            "猫咪到家后躲起来怎么办？": "What if the cat hides after arriving home?",
            "回访照片要怎么拍？": "How should I take follow-up photos?",
            "新手认养有哪些常见误区？": "What common mistakes do beginners make?",
            "猫咪日常喂养护理要注意什么？": "What should I know about daily feeding and care?",
            "预算大概要准备多少？": "How much budget should I prepare?",
            "提前准备猫粮、猫砂盆、猫砂、食碗水碗、猫包和安全封窗，并确认室友或家人同意。": "Prepare cat food, litter box, litter, food and water bowls, carrier, and secure windows; confirm roommates or family agree.",
            "写清住所稳定性、经济能力、养宠经验、假期照护安排，以及愿意配合回访的承诺。": "Clearly describe housing stability, financial ability, pet experience, holiday care plans, and commitment to follow-ups.",
            "先给猫咪安静小空间，不强抱不追赶，保持食水和猫砂固定，通常观察 3 到 7 天。": "Give the cat a quiet small space first, do not force holding or chasing, keep food/water and litter fixed, and observe for 3 to 7 days.",
            "照片建议包含猫咪近照、生活环境、食水区和猫砂区，画面清晰无遮挡，按节点提交。": "Photos should include recent cat photos, living environment, food/water area, and litter area; submit clear, unobstructed photos on schedule.",
            "不要冲动认养、不要频繁换粮、不要放养，也不要忽视封窗、驱虫和绝育计划。": "Do not adopt impulsively, switch food frequently, let cats roam outside, or ignore window safety, deworming, and sterilization plans.",
            "保持饮食稳定、每日清理猫砂、定期驱虫免疫，发现拒食呕吐腹泻要及时咨询医生。": "Keep diet stable, clean litter daily, deworm and vaccinate regularly, and consult a doctor if refusal to eat, vomiting, or diarrhea occurs.",
            "认养前": "Before Adoption",
            "申请表": "Application Form",
            "猫咪到家": "Cat Arrives Home",
            "回访照片": "Follow-up Photos",
            "新手": "Beginner",
            "预算": "Budget",
            "首页": "Home",
            "开放日": "Open Day",
            "咨询": "Consultation",
            "资料维护": "Profile Maintenance",
            "消息通知": "Message Notifications",
            "身份核验": "Identity Verification",
            "状态查询": "Status Query",
            "长期回访": "Long-term Follow-up",
            "医疗异常": "Medical Abnormality",
            "高风险申请": "High-risk Application",
            "待核实": "Pending Verification",
            "已核实有效": "Verified Valid",
            "已建档": "Filed",
            "无效": "Invalid",
            "观察中": "Under Observation",
            "医疗中": "Under Treatment",
            "可认养": "Adoptable",
            "申请中": "Applying",
            "已认养": "Adopted",
            "回访中": "In Follow-up",
            "待初审": "Pending Initial Review",
            "待终审": "Pending Final Review",
            "初审拒绝": "Initial Review Rejected",
            "终审拒绝": "Final Review Rejected",
            "已通过": "Approved",
            "已拒绝": "Rejected",
            "已撤回": "Withdrawn",
            "低风险": "Low Risk",
            "中风险": "Medium Risk",
            "高风险": "High Risk",
            "健康": "Healthy",
            "需观察": "Needs Observation",
            "需治疗": "Needs Treatment",
            "逾期": "Overdue",
            "异常": "Abnormal",
            "今天": "Today",
            "待办": "Tasks",
            "优先待办": "Priority Tasks",
            "业务分布": "Business Distribution",
            "按风险和流程阻塞排序": "Sorted by risk and process blocking",
            "按状态聚合的后台指标": "Admin metrics grouped by status",
            "申请": "Application",
            "协议": "Agreement",
            "预警": "Warning",
            "线索": "Clue",
            "医疗": "Medical",
            "回访": "Follow-up",
            "认养": "Adoption",
            "详情": "Details",
            "记录": "Record",
            "列表": "List",
            "搜索": "Search",
            "筛选": "Filter",
            "全部": "All",
            "暂无": "None",
            "正在加载": "Loading",
            "确认": "Confirm",
            "请输入": "Please enter",
            "请选择": "Please select",
            "不能": "cannot",
            "没有": "no",
            "成功": "success",
            "失败": "failed",
            "保存": "Save",
            "上传": "Upload",
            "删除": "Delete",
            "作废": "Void",
            "发布": "Publish",
            "下架": "Offline",
            "编辑": "Edit",
            "新增": "Create",
            "提交": "Submit",
            "返回": "Back",
            "进入": "Enter",
            "查看": "View",
            "处理": "Handle",
            "关闭": "Close",
            "图片": "Image",
            "照片": "Photo",
            "附件": "Attachment",
            "内容": "Content",
            "原因": "Reason",
            "意见": "Comment",
            "说明": "Description",
            "时间": "Time",
            "地点": "Location",
            "状态": "Status",
            "类型": "Type",
            "等级": "Level",
            "标题": "Title",
            "编号": "ID",
            "姓名": "Name",
            "电话": "Phone",
            "账号": "Account",
            "密码": "Password",
            "角色": "Role",
            "范围": "Scope",
            "排序": "Sort",
            "数量": "Count",
            "条": " items",
            "个": " ",
            "天": " days"
        },
        ja: {
            "正在加载": "読み込み中",
            "确认": "確認",
            "请输入": "入力してください",
            "请选择": "選択してください",
            "暂无": "なし",
            "全部": "すべて",
            "保存": "保存",
            "上传": "アップロード",
            "删除": "削除",
            "作废": "無効化",
            "发布": "公開",
            "下架": "非公開",
            "编辑": "編集",
            "新增": "作成",
            "提交": "送信",
            "返回": "戻る",
            "进入": "入る",
            "查看": "表示",
            "处理": "処理",
            "关闭": "閉じる",
            "图片": "画像",
            "照片": "写真",
            "附件": "添付",
            "内容": "内容",
            "原因": "理由",
            "意见": "コメント",
            "说明": "説明",
            "时间": "時間",
            "地点": "場所",
            "状态": "状態",
            "类型": "種類",
            "等级": "レベル",
            "标题": "タイトル",
            "编号": "ID",
            "姓名": "氏名",
            "电话": "電話",
            "账号": "アカウント",
            "密码": "パスワード",
            "角色": "権限",
            "申请": "申請",
            "协议": "契約",
            "预警": "アラート",
            "线索": "情報",
            "医疗": "医療",
            "回访": "フォロー",
            "认养": "譲渡",
            "详情": "詳細",
            "记录": "記録",
            "列表": "一覧",
            "搜索": "検索",
            "筛选": "絞り込み",
            "猫咪": "猫",
            "公告": "お知らせ",
            "用户": "ユーザー",
            "志愿者": "ボランティア",
            "管理员": "管理者",
            "合作医院": "協力病院",
            "普通用户": "一般ユーザー",
            "已完成": "完了",
            "待处理": "未処理",
            "处理中": "処理中",
            "已处理": "処理済み",
            "已删除": "削除済み",
            "已生成": "生成済み",
            "已发布": "公開済み",
            "已下架": "非公開",
            "草稿": "下書き",
            "条": "件",
            "个": "件",
            "天": "日"
        },
        ko: {
            "正在加载": "로딩 중",
            "确认": "확인",
            "请输入": "입력하세요",
            "请选择": "선택하세요",
            "暂无": "없음",
            "全部": "전체",
            "保存": "저장",
            "上传": "업로드",
            "删除": "삭제",
            "作废": "무효",
            "发布": "게시",
            "下架": "비공개",
            "编辑": "편집",
            "新增": "생성",
            "提交": "제출",
            "返回": "돌아가기",
            "进入": "들어가기",
            "查看": "보기",
            "处理": "처리",
            "关闭": "닫기",
            "图片": "이미지",
            "照片": "사진",
            "附件": "첨부",
            "内容": "내용",
            "原因": "사유",
            "意见": "의견",
            "说明": "설명",
            "时间": "시간",
            "地点": "장소",
            "状态": "상태",
            "类型": "유형",
            "等级": "등급",
            "标题": "제목",
            "编号": "번호",
            "姓名": "이름",
            "电话": "전화",
            "账号": "계정",
            "密码": "비밀번호",
            "角色": "역할",
            "申请": "신청",
            "协议": "계약",
            "预警": "경고",
            "线索": "제보",
            "医疗": "의료",
            "回访": "사후관리",
            "认养": "입양",
            "详情": "상세",
            "记录": "기록",
            "列表": "목록",
            "搜索": "검색",
            "筛选": "필터",
            "猫咪": "고양이",
            "公告": "공지",
            "用户": "사용자",
            "志愿者": "봉사자",
            "管理员": "관리자",
            "合作医院": "협력 병원",
            "普通用户": "일반 사용자",
            "已完成": "완료",
            "待处理": "대기",
            "处理中": "처리 중",
            "已处理": "처리됨",
            "已删除": "삭제됨",
            "已生成": "생성됨",
            "已发布": "게시됨",
            "已下架": "비공개",
            "草稿": "초안",
            "条": "건",
            "个": "개",
            "天": "일"
        }
    };

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

    // 前台路由配置：定义导航名称、页面标题和是否需要特定角色。
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
        { path: "#/hospital", label: "医疗协作", roles: ["HOSPITAL"], title: "医疗协作门户", text: "合作医院查看待医疗猫咪、最近医疗记录和工作台入口。" },
        { path: "#/profile", label: "个人中心", title: "个人中心", text: "维护个人资料，查看申请、回访、消息和对应后台入口。" },
        { path: "#/login", label: "登录", title: "登录", text: "使用统一账号进入前台或后台。" },
        { path: "#/register", label: "注册", title: "注册", text: "创建普通用户账号，提交线索和认养申请。" }
    ];

    // 后台路由配置：roles 字段用于和登录用户角色做权限匹配。
    const adminRoutes = [
        { path: "#/admin/dashboard", label: "Dashboard", roles: ["VOLUNTEER", "ADMIN"], title: "后台首页", text: "展示猫咪、线索、申请、回访、预警等 MIS 指标。" },
        { path: "#/admin/clues", label: "线索核实", roles: ["VOLUNTEER", "ADMIN"], title: "线索核实管理", text: "志愿者核实线索，管理员查看和追踪处理结果。" },
        { path: "#/admin/cats", label: "猫咪档案", roles: ["VOLUNTEER", "ADMIN"], title: "猫咪档案管理", text: "维护猫咪档案、照片、标签和生命周期状态。" },
        { path: "#/admin/medical", label: "医疗工作台", roles: ["HOSPITAL", "ADMIN"], title: "医疗记录管理", text: "医院用户录入体检、疫苗、绝育、治疗和异常记录。" },
        { path: "#/admin/adoption/audits", label: "申请审核", roles: ["VOLUNTEER", "ADMIN"], title: "认养申请审核", text: "志愿者初审，管理员终审，沉淀审核记录。" },
        { path: "#/admin/agreements", label: "协议交接", roles: ["VOLUNTEER", "ADMIN"], title: "协议交接管理", text: "系统自动生成协议，志愿者和管理员登记交接信息，管理员可编辑或取消未交接协议。" },
        { path: "#/admin/followups", label: "回访任务", roles: ["VOLUNTEER", "ADMIN"], title: "回访任务管理", text: "查看待回访、已完成、逾期和异常回访任务。" },
        { path: "#/admin/warnings", label: "异常预警", roles: ["VOLUNTEER", "ADMIN"], title: "异常预警中心", text: "处理回访逾期、回访异常、医疗异常和高风险申请。" },
        { path: "#/admin/users", label: "用户角色", roles: ["ADMIN"], title: "用户与角色管理", text: "管理员启停用户、分配角色并记录操作日志。" },
        { path: "#/admin/notices", label: "公告管理", roles: ["ADMIN"], title: "公告管理", text: "维护草稿、已发布、已下架公告。" },
        { path: "#/admin/logs", label: "操作日志", roles: ["ADMIN"], title: "操作日志", text: "查看审核、状态变更、交接、预警处理等关键日志。" }
       
    ];

    // 这些前台页面涉及个人数据或业务提交，未登录用户会被引导到登录页。
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
        // 后端历史数据中医院角色可能有不同写法，前端统一归一为 HOSPITAL 后再做权限判断。
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
        // 从本地登录态中恢复当前用户，并统一角色编码，后续路由权限判断都依赖这个对象。
        if (!authState || !authState.token || !authState.user) return null;
        return { ...authState.user, role: normalizeRole(authState.user.role) };
    }

    function translateText(value) {
        // 翻译时先保护业务编号，避免 APP、CAT、NT 等编号被误替换。
        if (!value) return value;
        if (currentLocale === "zh") return String(value);
        const pack = { ...(i18nPacks[currentLocale] || {}), ...(i18nCommonPacks[currentLocale] || {}) };
        let result = String(value);
        const keep = [];
        result = result.replace(/\b(?:APP|AGR|CAT|NT|CL|LG|MR|U|A)\d{4,}\b/g, match => {
            keep.push(match);
            return `__I18N_KEEP_${keep.length - 1}__`;
        });
        Object.keys(pack).sort((a, b) => b.length - a.length).forEach(key => {
            result = result.replaceAll(key, pack[key]);
        });
        keep.forEach((value, index) => {
            result = result.replaceAll(`__I18N_KEEP_${index}__`, value);
        });
        return result;
    }

    window.alert = message => nativeAlert(translateText(message));
    window.confirm = message => nativeConfirm(translateText(message));
    window.prompt = (message, defaultValue) => nativePrompt(translateText(message), defaultValue);

    function languageSwitcher() {
        return `<select class="mis-language-switch" id="mis-language-switch" aria-label="语言">
            ${Object.entries(supportedLocales).map(([key, label]) => `<option value="${key}" ${currentLocale === key ? "selected" : ""}>${label}</option>`).join("")}
        </select>`;
    }

    function bindLanguageSwitcher() {
        document.querySelectorAll("#mis-language-switch").forEach(select => {
            select.addEventListener("change", event => {
                currentLocale = event.target.value || "zh";
                localStorage.setItem(localeStorageKey, currentLocale);
                render();
            });
        });
    }

    function applyLocale(root = shell) {
        // 对当前渲染出来的 DOM 做轻量翻译，跳过输入框正文和声明了 data-no-i18n 的区域。
        if (!root) return;
        const walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT, {
            acceptNode(node) {
                const parent = node.parentElement;
                if (!parent || ["SCRIPT", "STYLE", "TEXTAREA"].includes(parent.tagName) || parent.closest?.("[data-no-i18n]")) {
                    return NodeFilter.FILTER_REJECT;
                }
                return node.nodeValue.trim() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP;
            }
        });
        const nodes = [];
        while (walker.nextNode()) nodes.push(walker.currentNode);
        nodes.forEach(node => {
            if (node._i18nSource == null) node._i18nSource = node.nodeValue;
            node.nodeValue = currentLocale === "zh" ? node._i18nSource : translateText(node._i18nSource);
        });
        root.querySelectorAll?.("[placeholder], [title], [aria-label], [value]").forEach(el => {
            ["placeholder", "title", "aria-label"].forEach(attr => {
                if (!el.hasAttribute(attr)) return;
                const sourceAttr = `data-i18n-${attr}`;
                if (!el.hasAttribute(sourceAttr)) el.setAttribute(sourceAttr, el.getAttribute(attr));
                el.setAttribute(attr, currentLocale === "zh" ? el.getAttribute(sourceAttr) : translateText(el.getAttribute(sourceAttr)));
            });
            if (el.tagName === "INPUT" && ["button", "submit", "reset"].includes((el.getAttribute("type") || "").toLowerCase())) {
                if (!el.hasAttribute("data-i18n-value")) el.setAttribute("data-i18n-value", el.getAttribute("value") || "");
                el.setAttribute("value", currentLocale === "zh" ? el.getAttribute("data-i18n-value") : translateText(el.getAttribute("data-i18n-value")));
            }
        });
    }

    function startLocaleObserver() {
        if (localeObserver) localeObserver.disconnect();
        localeObserver = new MutationObserver(mutations => {
            mutations.forEach(mutation => {
                mutation.addedNodes.forEach(node => {
                    if (node.nodeType === Node.ELEMENT_NODE) applyLocale(node);
                    if (node.nodeType === Node.TEXT_NODE) {
                        if (node._i18nSource == null) node._i18nSource = node.nodeValue;
                        node.nodeValue = currentLocale === "zh" ? node._i18nSource : translateText(node._i18nSource);
                    }
                });
            });
        });
        localeObserver.observe(shell, { childList: true, subtree: true });
    }

    function token() {
        return authState?.token || readAuth()?.token || "";
    }

    function saveAuth(result) {
        // 登录成功后把 token 和用户信息保存到 localStorage，刷新页面后仍可恢复会话。
        authState = result?.user ? { ...result, user: { ...result.user, role: normalizeRole(result.user.role) } } : result;
        localStorage.setItem("hfut-cat-auth", JSON.stringify(authState));
    }

    function clearAuth() {
        authState = null;
        ["hfut-cat-auth", "token", "userInfo", "role", "currentUser", "auth", "jwt"].forEach(key => localStorage.removeItem(key));
        sessionStorage.clear();
    }

    async function verifyAuth() {
        // 页面刷新后用本地 token 请求 /api/users/me，确认登录态仍然有效。
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
        return { ...meta, image: notice?.imageUrl || meta.image };
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
            return `<a class="primary-btn compact" href="#/admin/medical">医院工作台</a>`;
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
                        <p>待核实线索 ${summary.pendingClueCount ?? "-"}，待初审申请 ${summary.pendingInitialApplicationCount ?? "-"}，后台待回访任务 ${summary.pendingFollowupTaskCount ?? "-"}。${unread ? ` 未读消息 ${unread} 条。` : ""}</p>
                    </div>
                    <div class="role-actions">
                        <a class="primary-btn" href="#/admin/dashboard">进入志愿者工作台</a>
                        <a class="ghost-btn" href="#/admin/clues">线索审核</a>
                        <a class="ghost-btn" href="#/admin/followups">回访任务</a>
                        <a class="ghost-btn" href="#/admin/adoption/audits">认养初审</a>
                        <a class="ghost-btn" href="#/admin/cats">猫咪档案管理</a>
                        <a class="ghost-btn" href="#/my/messages">我的消息${unread ? `(${unread})` : ""}</a>
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
                        <a class="ghost-btn" href="#/admin/medical">待医疗猫咪</a>
                        <a class="ghost-btn" href="#/admin/medical">医疗记录管理</a>
                        <a class="ghost-btn" href="#/admin/medical">健康异常记录</a>
                        <a class="ghost-btn" href="#/profile">个人中心</a>
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
                    <a class="ghost-btn" href="#/admin/notices">公告发布</a>
                    <a class="ghost-btn" href="#/admin/logs">操作日志</a>
                </div>
            </section>
        `;
    }

    async function api(path, options) {
        // 统一封装后端接口请求：自动带上登录 Token，并把后端 ApiResponse 转成前端可直接使用的数据。
        const headers = { "Content-Type": "application/json", ...(options && options.headers ? options.headers : {}) };
        const authToken = token();
        if (authToken) {
            headers.Authorization = `Bearer ${authToken}`;
        }
        const response = await fetch(path, { ...options, headers });
        const payload = await response.json().catch(() => ({ success: false, message: "接口返回异常" }));
        if (response.status === 401) {
            clearAuth();
            throw new Error(payload.message || "登录已过期，请重新登录。");
        }
        if (response.status === 403) {
            throw new Error(payload.message || "当前账号无权执行此操作。");
        }
        if (!response.ok || !payload.success) {
            throw new Error(payload.message || "操作失败，请稍后重试。");
        }
        return payload.data;
    }

    function validateCleanText(label, value, min, max) {
        // 前端表单基础校验：过滤空值、过短过长文本和明显测试占位内容，减少无效演示数据进入数据库。
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
            throw new Error(message);
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
        // CSV 导出需要读取响应头中的文件流，所以不用统一 api()，单独处理 Blob 下载。
        const headers = { Accept: "text/csv,application/octet-stream,*/*" };
        const authToken = token();
        if (authToken) {
            headers.Authorization = `Bearer ${authToken}`;
        }
        const response = await fetch(path, { headers });
        if (!response.ok) {
            let message = "导出失败，请稍后重试";
            try {
                const contentType = response.headers.get("content-type") || "";
                if (contentType.includes("application/json")) {
                    const payload = await response.json();
                    message = payload.message || message;
                } else {
                    const text = await response.text();
                    message = text || message;
                }
            } catch (error) {
                // Keep the fallback message.
            }
            if (response.status === 401) {
                clearAuth();
                window.location.hash = loginHashFor(window.location.hash || "#/");
                throw new Error("登录已过期，请重新登录后再导出。");
            }
            if (response.status === 403) {
                throw new Error("当前账号没有导出权限，请使用管理员账号。");
            }
            throw new Error(message);
        }
        const blob = await response.blob();
        if (!blob.size) {
            throw new Error("导出文件为空，请稍后重试。");
        }
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
        // 图片上传使用 XMLHttpRequest，是为了展示上传进度；fetch 无法稳定获得上传百分比。
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
                    reject(new Error(request.responseText || `上传接口返回异常（HTTP ${request.status}）`));
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
                    reject(new Error(payload.message || `上传失败（HTTP ${request.status}）`));
                    return;
                }
                resolve(payload.data);
            });
            request.addEventListener("error", () => reject(new Error("网络异常，上传失败")));
            request.addEventListener("abort", () => reject(new Error("上传已取消")));
            request.send(formData);
        });
    }

    async function uploadNoticeImageFile(file, onProgress) {
        // 公告图片优先走公告上传接口；旧环境没有该接口时回退到线索上传接口，保证演示可用。
        try {
            return await uploadFileWithProgress("/api/uploads/notices", file, onProgress);
        } catch (error) {
            if (!String(error.message || "").includes("404")) {
                throw error;
            }
            return uploadFileWithProgress("/api/uploads/clues", file, onProgress);
        }
    }

    function bindAdminGlobalSearch() {
        // 后台顶部全局搜索采用防抖请求，减少输入过程中对后端的频繁访问。
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
        // 兼容旧版 hash 写法，统一转成当前正式路由格式。
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
        // 动态详情页没有固定配置，需要按路径模式临时生成路由元信息。
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
        // 角色访问控制集中在这里处理，避免各页面重复判断权限。
        const normalizedRole = normalizeRole(role);
        if (normalizedRole !== "STUDENT" && [
            "#/clues/submit",
            "#/my/clues",
            "#/my/applications",
            "#/my/followups",
            "#/adoption/apply/:catId"
        ].includes(route?.path)) {
            return false;
        }
        return !route.roles || route.roles.includes(normalizedRole);
    }

    function canUseFrontSelfService(user) {
        return !user || normalizeRole(user.role) === "STUDENT";
    }

    function isPrivateFrontRoute(route) {
        return Boolean(route && !route.path.startsWith("#/admin") && privateFrontRoutePaths.has(route.path));
    }

    function userNav(activePath, user) {
        return userRoutes.filter(route => !["#/login", "#/register"].includes(route.path))
            .filter(route => user || !privateFrontRoutePaths.has(route.path))
            .filter(route => route.roles ? (user && canVisit(route, user.role)) : (!user || canVisit(route, user.role))).map(route => `
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
        return role === "STUDENT";
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
        // 提交认养前要求用户阅读须知，用倒计时按钮强化“知情同意”的业务含义。
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
        const role = normalizeRole(user?.role);
        const homeHref = role === "HOSPITAL" ? "#/hospital" : "#/";
        const backendHref = roleLanding(user);
        const currentHash = (window.location.hash || "#/").split("?")[0];
        const showBackendLink = user && role !== "STUDENT" && backendHref !== currentHash;
        document.body.classList.add("mis-active");
        shell.innerHTML = `
            <main class="mis-forbidden">
                <section>
                    <strong>403</strong>
                    <h1>无权限访问</h1>
                    <p>${user ? `${escapeHtml(roleLabels[normalizeRole(user.role)] || normalizeRole(user.role))} 当前不能进入该页面。` : "请先登录后再访问需要权限的后台页面。"}</p>
                    <div class="mis-actions">
                        <a class="primary-btn" href="${escapeHtml(homeHref)}">回到前台</a>
                        ${showBackendLink ? `<a class="ghost-btn" href="${escapeHtml(backendHref)}">进入可用后台</a>` : ""}
                        ${user ? `<button class="ghost-btn" id="forbidden-logout" type="button">退出登录</button>` : `<a class="ghost-btn" href="#/login">前往登录</a>`}
                    </div>
                </section>
            </main>
        `;
        const logoutButton = document.getElementById("forbidden-logout");
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

    function userShell(route, user, content) {
        if (screenClockTimer) {
            window.clearInterval(screenClockTimer);
            screenClockTimer = null;
        }
        const role = normalizeRole(user?.role);
        shell.innerHTML = `
            <div class="mis-user-layout user-center-shell">
                <header class="mis-user-header">
                    <a class="mis-brand" href="#/"><span>HFUT</span><strong>校园流浪猫认养门户</strong></a>
                    <nav>${userNav(route.path, user)}</nav>
                    <div class="mis-user-chip">${languageSwitcher()}${user ? `${backendEntry(user)}<a href="#/profile">${escapeHtml(user.userName)} · ${escapeHtml(roleLabels[role] || role)}</a><button id="mis-logout" type="button">退出</button>` : `<a href="#/login">登录</a><a href="#/register">注册</a>`}</div>
                </header>
                <main class="mis-user-main">${content}</main>
            </div>
        `;
        bindLanguageSwitcher();
        applyLocale();
        startLocaleObserver();
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
                        <div class="mis-user-chip">${languageSwitcher()}<a class="ghost-btn compact" href="#/">返回前台</a><span>${escapeHtml(user.userName)} · ${escapeHtml(roleLabels[role] || role)}</span><button id="mis-admin-logout" type="button">退出</button></div>
                    </header>
                    <main class="mis-admin-main">${content}</main>
                </div>
            </div>
        `;
        bindLanguageSwitcher();
        applyLocale();
        startLocaleObserver();
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
        const selectRow = document.createElement("div");
        selectRow.className = "clue-select-row";
        select.insertAdjacentElement("beforebegin", selectRow);
        selectRow.appendChild(select);
        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.className = "clue-select-delete";
        deleteButton.textContent = "\u00d7";
        deleteButton.setAttribute("aria-label", "删除当前选项");
        selectRow.appendChild(deleteButton);

        const getSelectedOption = () => [...select.options].find(option => option.selected);
        const updateDeleteButton = () => {
            const option = getSelectedOption();
            const canDelete = Boolean(option?.value && option.value !== "__custom__");
            deleteButton.classList.toggle("is-visible", canDelete);
            deleteButton.disabled = !canDelete;
            deleteButton.title = canDelete ? `删除“${option.textContent}”` : "请先选择要删除的选项";
        };
        const setCustomMode = active => {
            input.classList.toggle("is-visible", active);
            saveButton?.classList.toggle("is-visible", active);
            updateDeleteButton();
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
        deleteButton.addEventListener("click", () => {
            const option = getSelectedOption();
            if (!option?.value || option.value === "__custom__") return;
            if (!window.confirm(`确定删除“${option.textContent}”这个选项吗？`)) return;
            const removedValue = option.value;
            option.remove();
            if (input.value === removedValue) input.value = "";
            select.value = "";
            setCustomMode(false);
            updateDeleteButton();
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
            updateDeleteButton();
        };
        saveButton?.addEventListener("click", saveCustom);
        input.addEventListener("keydown", event => {
            if (event.key !== "Enter" || !input.classList.contains("is-visible")) return;
            event.preventDefault();
            saveCustom();
        });
        setCustomMode(false);
        updateDeleteButton();
    }

    function renderClueSubmit(route, user) {
        // 线索上报页：普通用户提交地点、时间、照片和描述，后端生成待核实线索。
        userShell(route, user, `
            ${pageHero(route)}
            <form class="mis-form" id="clue-submit-form">
                <label class="wide clue-select-field">校园区域
                    <select id="found-area-select" class="clue-choice-select">
                        <option value="">请选择校园区域</option>
                        <option value="翡翠湖校区">翡翠湖校区</option>
                        <option value="屯溪路校区">屯溪路校区</option>
                        <option value="宣城校区">宣城校区</option>
                    </select>

                    <button class="ghost-btn compact add-choice-btn" type="button" data-save-custom="foundArea">保存到下拉框</button>
                </label>
                <label class="wide clue-select-field">发现地点
                    <select id="found-location-select" class="clue-choice-select">
                        <option value="">请选择发现地点</option>
                        <option value="图书馆东侧">图书馆东侧</option>
                        <option value="宿舍区楼下">宿舍区楼下</option>
                        <option value="东门附近">东门附近</option>
                        <option value="西门附近">西门附近</option>
                        <option value="南门附近">南门附近</option>
                        <option value="北门附近">北门附近</option>
                        <option value="操场看台">操场看台</option>
                        <option value="快递站附近">快递站附近</option>
                        <option value="校医院门口">校医院门口</option>
                        <option value="湖边草坪">湖边草坪</option>
                        <option value="主教学楼附近">主教学楼</option>
                        <option value="西二楼附近">西二教学楼</option>
                        <option value="食堂附近">食堂</option>
                        <option value="__custom__">新增/自定义发现地点</option>
                    </select>
                    <input name="foundLocation" class="clue-custom-input" placeholder="请输入新的发现地点">
                    <button class="ghost-btn compact add-choice-btn" type="button" data-save-custom="foundLocation">保存到下拉框</button>
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
            // 线索照片必须先上传，表单最终只提交服务器返回的 photoUrl。
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
            // 提交前做前端质量校验，减少明显无效地点、描述或未上传照片进入数据库。
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
        // 认养申请页：只有普通用户且猫咪处于可认养状态时，才允许填写申请表。
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
                // 申请表提交前校验照护条件、经济能力、回访知情和承诺条款。
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

    function hospitalCatRow(cat, tone) {
        const statusText = catStatusLabels[cat.status]?.label || cat.status || "-";
        const healthText = healthLabels[cat.healthLevel]?.label || cat.healthLevel || "-";
        return `
            <article class="hospital-cat-row ${tone === "observe" ? "observe" : ""}">
                <img ${imageAttrs(cat.coverUrl, "hospital-cat-thumb", "猫咪照片")}>
                <div>
                    <strong>${escapeHtml(cat.catName || "待命名")} <small>${escapeHtml(cat.catId)}</small></strong>
                    <span>${escapeHtml(cat.foundPlace || "地点待补充")} · ${escapeHtml(healthText)} · ${escapeHtml(statusText)}</span>
                </div>
                <a class="ghost-btn compact" href="#/admin/medical?catId=${escapeHtml(cat.catId)}">处理</a>
            </article>
        `;
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
                <div class="hospital-entry-actions">
                    <a class="primary-btn" href="#/admin/medical">进入医院工作台</a>
                    <a class="ghost-btn" href="#/admin/medical?status=MEDICAL">处理医疗中猫咪</a>
                    <a class="ghost-btn" href="#/admin/medical?status=OBSERVING">查看观察中猫咪</a>
                    <a class="ghost-btn" href="#/profile">个人中心</a>
                </div>
                <h3>待医疗猫咪</h3>
                <div class="mis-cat-grid showcase hospital-portal-cat-grid">
                    ${(medicalCats || []).slice(0, 4).map(cat => homeCatCard(cat).replaceAll("#/cats/", "#/admin/cats/")).join("") || `<div class="mis-empty">暂无医疗中猫咪</div>`}
                </div>
                <h3>观察中猫咪</h3>
                <div class="mis-cat-grid showcase hospital-portal-cat-grid">
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
        adminShell(route, user, `${adminHero(route)}<section class="admin-home"><div class="mis-loading">正在加载后台首页...</div></section>`);
        const grid = shell.querySelector(".admin-home");
        try {
            const [summary, catStatus, applicationStatus, followupStatus, warningType] = await Promise.all([
                api("/api/admin/dashboard/summary"),
                api("/api/admin/dashboard/cat-status"),
                api("/api/admin/dashboard/application-status"),
                api("/api/admin/dashboard/followup-status"),
                api("/api/admin/dashboard/warning-type")
            ]);
            grid.innerHTML = `
                <div class="admin-home-hero">
                    <div>
                        <p class="eyebrow">${user.role === "ADMIN" ? "ADMIN CONSOLE" : "VOLUNTEER WORKBENCH"}</p>
                        <h2>${escapeHtml(user.userName || "管理员")}，今天的后台待办已汇总</h2>
                        <span>围绕线索核实、猫咪档案、认养审核、协议交接、回访预警组织工作，优先处理有风险或即将逾期的事项。</span>
                    </div>
                    <div class="admin-home-actions">
                        <a class="primary-btn" href="#/admin/clues">处理线索</a>
                        <a class="ghost-btn" href="#/admin/adoption/audits">认养审核</a>
                        <a class="ghost-btn" href="#/admin/followups">回访任务</a>
                        ${user.role === "ADMIN" ? `<a class="ghost-btn" href="#/admin/system/users">用户管理</a>` : ""}
                    </div>
                </div>
                <div class="admin-home-kpis">
                    ${[
                        { label: "猫咪总数", value: summary.catCount, hint: `可认养 ${summary.adoptableCount || 0}` },
                        { label: "待核实线索", value: summary.pendingClueCount, hint: `已建档 ${summary.createdCatClueCount || 0}` },
                        { label: "待审核申请", value: (summary.pendingInitialApplicationCount || 0) + (summary.pendingFinalApplicationCount || 0), hint: `高风险 ${summary.highRiskApplicationCount || 0}` },
                        { label: "待交接", value: summary.pendingHandoverApplicationCount, hint: `已交接 ${summary.handedOverApplicationCount || 0}` },
                        { label: "待回访", value: summary.pendingFollowupTaskCount, hint: `完成率 ${summary.followupCompletionRate || 0}%` },
                        { label: "待处理预警", value: summary.pendingWarningCount, hint: `已处理 ${summary.handledWarningCount || 0}` }
                    ].map(item => `<article><span>${escapeHtml(item.label)}</span><strong>${escapeHtml(item.value ?? 0)}</strong><small>${escapeHtml(item.hint)}</small></article>`).join("")}
                </div>
                <div class="admin-home-grid">
                    <section class="admin-work-card priority">
                        <div class="mis-section-head"><h2>优先待办</h2><span>按风险和流程阻塞排序</span></div>
                        <div class="admin-task-list">
                            ${dashboardTask("线索核实", summary.pendingClueCount, "待现场确认与建档", "#/admin/clues")}
                            ${dashboardTask("认养初审/终审", (summary.pendingInitialApplicationCount || 0) + (summary.pendingFinalApplicationCount || 0), "审核材料与风险评分", "#/admin/adoption/audits")}
                            ${dashboardTask("协议交接", summary.pendingHandoverApplicationCount, "确认协议和交接信息", "#/admin/agreements")}
                            ${dashboardTask("逾期/异常回访", (summary.overdueFollowupTaskCount || 0) + (summary.abnormalFollowupTaskCount || 0), "需要联系认养人跟进", "#/admin/followups")}
                            ${dashboardTask("异常预警", summary.pendingWarningCount, "需要处理或关闭预警", "#/admin/warnings")}
                        </div>
                    </section>
                    <section class="admin-work-card">
                        <div class="mis-section-head"><h2>猫咪状态</h2><span>救助与认养库存</span></div>
                        <div class="admin-status-list">
                            ${dashboardStatusLine("可认养", summary.adoptableCount)}
                            ${dashboardStatusLine("观察中", summary.observingCount)}
                            ${dashboardStatusLine("医疗中", summary.medicalCount)}
                            ${dashboardStatusLine("回访中", summary.followingCatCount)}
                            ${dashboardStatusLine("暂停认养", summary.suspendedCount)}
                        </div>
                    </section>
                </div>
                <section class="admin-distribution-panel">
                    <div class="mis-section-head"><h2>业务分布</h2><span>按状态聚合的后台指标</span></div>
                    <div class="admin-distribution-grid">
                        ${distributionCard("猫咪状态", catStatus)}
                        ${distributionCard("申请状态", applicationStatus)}
                        ${distributionCard("回访状态", followupStatus)}
                        ${distributionCard("预警类型", warningType)}
                    </div>
                </section>
            `;
        } catch (error) {
            grid.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function dashboardTask(label, value, hint, href) {
        return `<a class="admin-task-row" href="${escapeHtml(href)}"><span>${escapeHtml(label)}</span><strong>${escapeHtml(value || 0)}</strong><small>${escapeHtml(hint)}</small></a>`;
    }

    function dashboardStatusLine(label, value) {
        return `<div class="admin-status-row"><span>${escapeHtml(label)}</span><strong>${escapeHtml(value || 0)}</strong></div>`;
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
        const content = `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载线索...</div></section>`;
        adminShell(route, user, content);
        await loadAdminClues();
    }

    async function renderAdminCats(route, user) {
        // 后台猫咪档案页：根据地址栏筛选参数请求列表，保持刷新页面后筛选条件不丢失。
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
            // 筛选条件写回 hash，由路由重新渲染页面，避免手动维护多份列表状态。
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
        // 后台猫咪编辑使用 prompt 快速录入，适合演示环境中的档案补充和状态维护。
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

    function bindMedicalAttachmentUpload() {
        const form = document.getElementById("medical-form");
        const fileInput = document.getElementById("medical-attachment-file");
        const uploadButton = document.getElementById("medical-attachment-upload");
        const preview = document.getElementById("medical-attachment-preview");
        const progressBar = document.getElementById("medical-upload-progress-bar");
        const progressText = document.getElementById("medical-upload-progress-text");
        const attachmentInput = form?.elements.attachmentUrl;
        let previewObjectUrl = "";
        const reset = () => {
            if (previewObjectUrl) {
                URL.revokeObjectURL(previewObjectUrl);
                previewObjectUrl = "";
            }
            if (fileInput) fileInput.value = "";
            if (uploadButton) uploadButton.disabled = true;
            if (attachmentInput) attachmentInput.value = "";
            if (preview) preview.src = "/uploads/cats/no-photo.svg";
            if (progressBar) progressBar.style.width = "0%";
            if (progressText) progressText.textContent = "可以上传检查、治疗或诊断照片";
        };
        const setValue = url => {
            const value = String(url || "").trim();
            if (previewObjectUrl) {
                URL.revokeObjectURL(previewObjectUrl);
                previewObjectUrl = "";
            }
            if (attachmentInput) attachmentInput.value = value;
            if (preview) preview.src = value || "/uploads/cats/no-photo.svg";
            if (progressBar) progressBar.style.width = value ? "100%" : "0%";
            if (progressText) progressText.textContent = value ? "已加载附件图片" : "可以上传检查、治疗或诊断照片";
            if (uploadButton) uploadButton.disabled = true;
            if (fileInput) fileInput.value = "";
        };
        fileInput?.addEventListener("change", () => {
            const file = fileInput.files?.[0];
            if (attachmentInput) attachmentInput.value = "";
            if (progressBar) progressBar.style.width = "0%";
            if (progressText) progressText.textContent = file ? "待上传" : "可以上传检查、治疗或诊断照片";
            if (uploadButton) uploadButton.disabled = !file;
            if (previewObjectUrl) {
                URL.revokeObjectURL(previewObjectUrl);
                previewObjectUrl = "";
            }
            if (!file) {
                if (preview) preview.src = "/uploads/cats/no-photo.svg";
                return;
            }
            if (!file.type.startsWith("image/")) {
                if (progressText) progressText.textContent = "请选择图片文件";
                if (uploadButton) uploadButton.disabled = true;
                if (preview) preview.src = "/uploads/cats/no-photo.svg";
                return;
            }
            previewObjectUrl = URL.createObjectURL(file);
            if (preview) preview.src = previewObjectUrl;
        });
        uploadButton?.addEventListener("click", async () => {
            const file = fileInput.files?.[0];
            if (!file) {
                if (progressText) progressText.textContent = "请先选择图片";
                return;
            }
            if (file.size > 5 * 1024 * 1024) {
                if (progressText) progressText.textContent = "图片不能超过 5MB";
                return;
            }
            try {
                uploadButton.disabled = true;
                if (progressBar) progressBar.style.width = "0%";
                if (progressText) progressText.textContent = "上传中 0%";
                const result = await uploadFileWithProgress("/api/uploads/clues", file, percent => {
                    if (progressBar) progressBar.style.width = `${percent}%`;
                    if (progressText) progressText.textContent = `上传中 ${percent}%`;
                });
                setValue(result.url);
                if (progressText) progressText.textContent = "上传完成";
            } catch (error) {
                if (attachmentInput) attachmentInput.value = "";
                if (progressText) progressText.textContent = error.message;
                uploadButton.disabled = false;
            }
        });
        return {
            reset,
            setValue,
            hasPendingFile: () => Boolean(fileInput?.files?.[0] && !attachmentInput?.value)
        };
    }

    async function renderAdminMedical(route, user) {
        // 医疗工作台：医院用户录入医疗记录，管理员也可查看和维护猫咪健康档案。
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
            const selectedCat = cats.find(cat => cat.catId === currentCat);
            panel.innerHTML = `
                <section class="medical-workbench">
                    <div class="medical-workbench-head">
                        <div>
                            <p class="eyebrow">MEDICAL RECORD</p>
                            <h2>医疗记录工作台</h2>
                            <span>选择猫咪后录入体检、疫苗、绝育、治疗记录，附件图片会随记录一起保存。</span>
                        </div>
                        <div class="medical-mini-kpis">
                            <article><span>医疗中</span><strong>${summary.medicalCatCount ?? 0}</strong></article>
                            <article><span>观察中</span><strong>${summary.observingCatCount ?? 0}</strong></article>
                            <article><span>异常</span><strong>${summary.abnormalRecordCount ?? 0}</strong></article>
                            <article><span>本院</span><strong>${summary.myRecordCount ?? 0}</strong></article>
                        </div>
                    </div>
                    <div class="medical-selector-bar">
                        <select id="medical-status-filter">
                            <option value="MEDICAL" ${statusFilter === "MEDICAL" ? "selected" : ""}>医疗中猫咪</option>
                            <option value="OBSERVING" ${statusFilter === "OBSERVING" ? "selected" : ""}>观察中猫咪</option>
                            <option value="ADOPTABLE" ${statusFilter === "ADOPTABLE" ? "selected" : ""}>可认养猫咪</option>
                        </select>
                        ${cats.length ? `<select id="medical-cat-select">${cats.map(cat => `<option value="${cat.catId}" ${currentCat === cat.catId ? "selected" : ""}>${cat.catId} · ${escapeHtml(cat.catName || "待命名")} · ${catStatusLabels[cat.status]?.label || cat.status}</option>`).join("")}</select>` : `<input id="medical-cat-select" value="${escapeHtml(currentCat)}" placeholder="输入猫咪编号，如 CAT260501001">`}
                        <button class="ghost-btn" id="medical-cat-jump">查看</button>
                        <a class="ghost-btn" href="#/hospital">医疗协作门户</a>
                    </div>
                    <div class="medical-current-card">
                        <img ${imageAttrs(selectedCat?.coverUrl, "medical-current-photo", "当前猫咪照片")}>
                        <div>
                            <strong>${escapeHtml(selectedCat?.catName || currentCat || "请选择猫咪")}</strong>
                            <span>${escapeHtml(currentCat || "-")} · ${escapeHtml(selectedCat?.foundPlace || "地点待补充")}</span>
                        </div>
                        <div class="medical-current-tags">
                            ${selectedCat ? `${tag(selectedCat.status, catStatusLabels)}${tag(selectedCat.healthLevel, healthLabels)}` : `<span class="mis-tag info">待选择</span>`}
                        </div>
                    </div>
                    <div class="medical-workbench-grid">
                        <form class="mis-form medical-entry-form" id="medical-form">
                            <input type="hidden" name="medicalId" value="">
                            <div class="medical-form-section">
                                <h3>基础信息</h3>
                                <div class="medical-form-grid">
                                    <label>记录类型<select name="recordType"><option value="CHECKUP">体检</option><option value="VACCINE">疫苗</option><option value="STERILIZATION">绝育</option><option value="TREATMENT">治疗</option><option value="OTHER">其他</option></select></label>
                                    <label>记录日期<input name="recordDate" type="date"></label>
                                    <label>健康结果<select name="healthResult"><option value="HEALTHY">健康</option><option value="OBSERVE">需观察</option><option value="SICK">患病</option><option value="SERIOUS">严重异常</option></select></label>
                                    <label>费用<input name="cost" type="number" step="0.01"></label>
                                </div>
                            </div>
                            <div class="medical-form-section">
                                <h3>疫苗与绝育</h3>
                                <div class="medical-form-grid">
                                    <label>疫苗状态<select name="vaccineStatus"><option value="UNKNOWN">未知</option><option value="NOT_VACCINATED">未疫苗</option><option value="PARTIAL">部分接种</option><option value="VACCINATED">已疫苗</option></select></label>
                                    <label>绝育状态<select name="sterilizedStatus"><option value="UNKNOWN">未知</option><option value="NOT_STERILIZED">未绝育</option><option value="STERILIZED">已绝育</option><option value="NOT_SUITABLE">暂不适合</option></select></label>
                                </div>
                            </div>
                            <div class="medical-form-section">
                                <h3>附件与说明</h3>
                                <label class="wide medical-attachment-field">医疗附件
                                    <input type="hidden" name="attachmentUrl">
                                    <div class="medical-upload-panel">
                                        <div class="medical-upload-preview">
                                            <img class="upload-preview-image" id="medical-attachment-preview" src="/uploads/cats/no-photo.svg" alt="医疗附件预览">
                                        </div>
                                        <div class="medical-upload-controls">
                                            <input id="medical-attachment-file" type="file" accept="image/jpeg,image/png,image/webp,image/gif">
                                            <button class="ghost-btn" type="button" id="medical-attachment-upload" disabled>上传附件图片</button>
                                            <div class="upload-progress" aria-live="polite">
                                                <div class="upload-progress-bar"><span id="medical-upload-progress-bar"></span></div>
                                                <strong id="medical-upload-progress-text">可以上传检查、治疗或诊断照片</strong>
                                            </div>
                                        </div>
                                    </div>
                                </label>
                                <label class="wide">医疗说明<textarea name="description" required placeholder="填写检查结果、处理建议、用药或复查安排"></textarea></label>
                                <label class="check"><input name="abnormalFlag" type="checkbox"> 标记异常，需要后续关注</label>
                            </div>
                            <div class="mis-form-actions"><button class="primary-btn" id="medical-submit">新增医疗记录</button><button class="ghost-btn" id="medical-reset" type="button">清空</button><span id="medical-message"></span></div>
                        </form>
                        <section class="medical-record-panel">
                            <div class="mis-section-head"><h2>当前猫咪医疗记录</h2><span>${records.length} 条</span></div>
                            <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>编号</th><th>日期</th><th>健康</th><th>说明</th><th>医院</th><th>附件</th><th>操作</th></tr></thead><tbody>
                                ${(records || []).map(item => `<tr>
                                    <td>${escapeHtml(item.medicalId)}</td>
                                    <td>${escapeHtml(item.checkDate || "-")}</td>
                                    <td>${tag(item.healthLevel, healthLabels)}</td>
                                    <td>${escapeHtml(item.treatment || item.doctorNote || "-")}</td>
                                    <td>${escapeHtml(item.hospital || "-")}</td>
                                    <td>${item.attachmentUrl ? `<a href="${escapeHtml(item.attachmentUrl)}" target="_blank" rel="noopener"><img ${imageAttrs(item.attachmentUrl, "thumb-image mis-thumb", "医疗附件")}></a>` : "-"}</td>
                                    <td>
                                        <button class="ghost-btn" data-medical-edit="${escapeHtml(item.medicalId)}">回填编辑</button>
                                        <button class="ghost-btn" data-medical-void="${escapeHtml(item.medicalId)}">作废</button>
                                    </td>
                                </tr>`).join("") || `<tr><td colspan="7"><div class="mis-empty">暂无医疗记录</div></td></tr>`}
                            </tbody></table></div>
                        </section>
                    </div>
                </section>
            `;
            const medicalAttachmentUpload = bindMedicalAttachmentUpload();
            document.getElementById("medical-cat-jump").addEventListener("click", () => {
                // 切换猫咪时把猫咪编号写入 hash，刷新后仍能保持当前选择。
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
                medicalAttachmentUpload.reset();
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
                    if (medicalAttachmentUpload.hasPendingFile()) {
                        throw new Error("请先点击上传附件图片，上传完成后再保存医疗记录。");
                    }
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
                medicalAttachmentUpload.setValue(record.attachmentUrl || "");
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
        // 认养审核页：志愿者处理初审，管理员处理终审，列表同时展示评分和风险等级。
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
        // 审核动作根据 stage 选择初审或终审接口，前端只提交结果和意见，状态流转由后端统一控制。
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
        // 线索管理页根据筛选条件读取救助线索，支持核实、标记无效和从有效线索建档。
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
        // 线索列表的按钮事件集中绑定，便于保持筛选、核实、删除和建档后的刷新逻辑一致。
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
        // 协议管理页承接终审通过后的交接流程，管理员可编辑协议、作废协议并完成交接。
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
                    { label: "协议记录", value: countRows(rows, item => item.id) },
                    { label: "待交接", value: countRows(rows, item => item.id && ["GENERATED", "DRAFT"].includes(item.status)) },
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
                        <td>${escapeHtml(row.agreementNo || "系统自动生成中")}</td>
                        <td>${escapeHtml(row.catName || row.catId)}</td>
                        <td>${escapeHtml(row.adopterName || row.adopterId)}<br><small>${escapeHtml(row.adopterPhone || "")}</small></td>
                        <td>${escapeHtml((row.finalApprovedAt || "").replace("T", " "))}</td>
                        <td>${tag(row.status || "NOT_GENERATED", agreementStatusLabels)}</td>
                        <td>${row.handoverTime ? `${escapeHtml(row.handoverLocation || "-")}<br><small>${escapeHtml(row.handoverTime.replace("T", " "))}</small>` : "待交接"}</td>
                        <td>
                            ${row.id ? `<button class="ghost-btn" data-agreement-detail="${row.id}">查看</button>` : ""}
                            ${user.role === "ADMIN" && row.id && (row.status === "GENERATED" || row.status === "DRAFT") ? `<button class="ghost-btn" data-agreement-edit="${row.id}">编辑</button>` : ""}
                            ${row.id && row.status === "GENERATED" ? `<button class="primary-btn" data-agreement-handover="${row.id}">完成交接</button>` : ""}
                            ${row.id && row.status === "GENERATED" ? `<button class="ghost-btn" data-agreement-cancel="${row.id}">取消交接</button>` : ""}
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
            shell.querySelectorAll("[data-agreement-delete]").forEach(button => button.addEventListener("click", async () => {
                if (!confirm("确认取消该未交接协议？取消后系统会重新生成待交接协议。")) return;
                const reason = prompt("请输入取消交接原因");
                if (!reason) return;
                try {
                    await api(`/api/admin/agreements/${button.dataset.agreementDelete}`, {
                        method: "DELETE",
                        body: JSON.stringify({ reason })
                    });
                    renderAdminAgreements(route, user);
                } catch (error) {
                    alert(error.message);
                }
            }));
            shell.querySelectorAll("[data-agreement-handover]").forEach(button => button.addEventListener("click", async () => {
                const handoverLocation = prompt("交接地点", "翡翠湖校区志愿者服务点");
                if (!handoverLocation) return;
                const remark = prompt("备注", "现场确认完成交接") || "";
                button.disabled = true;
                try {
                    await api(`/api/admin/agreements/${button.dataset.agreementHandover}/handover`, {
                        method: "PUT",
                        body: JSON.stringify({ handoverLocation, handoverUserId: user.userId, remark, adopterConfirmed: true, volunteerConfirmed: true })
                    });
                    alert("交接完成，已自动生成 7/30/90 天回访任务。");
                    renderAdminAgreements(route, user);
                } catch (error) {
                    alert(error.message || "交接失败，请稍后重试。");
                    button.disabled = false;
                }
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
        // 回访任务页：后台读取任务列表前由后端刷新逾期状态，前端只负责展示和触发操作。
        const content = `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载回访任务...</div></section>`;
        adminShell(route, user, content);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            ["status", "taskType", "planDate", "keyword"].forEach(key => {
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
                        ${!admin && task.feedbackEnabled ? `<button class="primary-btn" data-followup-submit="${task.id}">上传状态</button>` : ""}
                        ${admin && task.feedbackEnabled ? `<button class="primary-btn" data-followup-staff-submit="${task.id}">填写回访</button>` : ""}
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
        // 回访任务操作会区分用户端和后台端：认养人提交状态，后台可补录记录、刷新逾期和标记异常。
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
            openFollowupMessageDialog("回访任务详情", "正在加载回访任务详情...");
            try {
                const detail = await api(path);
                openFollowupDetailDialog(detail);
            } catch (error) {
                openFollowupMessageDialog("回访任务详情", `加载失败：${error.message || "请稍后再试"}`);
            }
        }));
        shell.querySelectorAll("[data-followup-submit]").forEach(button => button.addEventListener("click", async () => {
            const payload = await openFollowupRecordDialog("上传猫咪状态记录", false);
            if (!payload) return;
            await api(`/api/my/followup/tasks/${button.dataset.followupSubmit}/records`, {
                method: "POST",
                body: JSON.stringify(payload)
            });
            alert(payload.abnormalFlag ? "已提交异常状态，志愿者或管理员将跟进处理。" : "猫咪状态已上传到回访记录。");
            renderMyFollowups(route, user);
        }));
        shell.querySelectorAll("[data-followup-staff-submit]").forEach(button => button.addEventListener("click", async () => {
            const payload = await openFollowupRecordDialog("填写回访记录", true);
            if (!payload) return;
            await api(`/api/admin/followup/tasks/${button.dataset.followupStaffSubmit}/records`, {
                method: "POST",
                body: JSON.stringify(payload)
            });
            alert(payload.abnormalFlag ? "已记录异常回访，并生成预警。" : "回访记录已保存。");
            renderAdminFollowups(route, user);
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

    function openFollowupRecordDialog(title, staffMode) {
        return new Promise(resolve => {
            document.querySelector(".followup-record-modal")?.remove();
            const modal = document.createElement("div");
            modal.className = "reading-modal-backdrop followup-record-modal";
            modal.innerHTML = `
                <section class="reading-modal followup-record-dialog" role="dialog" aria-modal="true">
                    <div class="reading-modal-head">
                        <h3>${escapeHtml(title)}</h3>
                        <button class="ghost-btn compact" type="button" data-followup-close>关闭</button>
                    </div>
                    <form class="mis-form followup-record-form">
                        <label class="wide">${staffMode ? "回访内容" : "猫咪近况"}<textarea name="content" required placeholder="${staffMode ? "填写电话/现场/线上回访情况" : "写一下猫咪最近吃饭、精神、排便、适应情况"}"></textarea></label>
                        <label>猫咪状态<input name="catCondition" required placeholder="例如：精神好，食欲正常"></label>
                        <label>生活环境<input name="environmentDesc" required placeholder="例如：门窗防护正常，猫砂盆干净"></label>
                        <label class="wide followup-photo-field">回访照片
                            <input type="hidden" name="photoUrl">
                            <div class="followup-upload-panel">
                                <div class="followup-upload-preview">
                                    <img class="upload-preview-image" data-followup-preview src="/uploads/cats/no-photo.svg" alt="回访照片预览">
                                </div>
                                <div class="followup-upload-controls">
                                    <input type="file" accept="image/jpeg,image/png,image/webp,image/gif" data-followup-file>
                                    <button class="ghost-btn" type="button" data-followup-upload disabled>上传回访照片</button>
                                    <div class="upload-progress" aria-live="polite">
                                        <div class="upload-progress-bar"><span data-followup-progress-bar></span></div>
                                        <strong data-followup-progress-text>可以上传猫咪近况或环境照片</strong>
                                    </div>
                                </div>
                            </div>
                        </label>
                        <label class="check wide"><input type="checkbox" name="abnormalFlag"> <span>存在异常情况</span></label>
                        <label class="wide followup-abnormal-field">异常说明<textarea name="abnormalDesc" placeholder="如食欲下降、逃逸风险、环境不稳定等"></textarea></label>
                        <div class="mis-form-actions">
                            <button class="primary-btn" type="submit">保存记录</button>
                            <button class="ghost-btn" type="button" data-followup-close>取消</button>
                        </div>
                    </form>
                </section>
            `;
            document.body.appendChild(modal);
            const form = modal.querySelector("form");
            const abnormalCheck = form.elements.abnormalFlag;
            const abnormalField = modal.querySelector(".followup-abnormal-field");
            const fileInput = modal.querySelector("[data-followup-file]");
            const uploadButton = modal.querySelector("[data-followup-upload]");
            const preview = modal.querySelector("[data-followup-preview]");
            const progressBar = modal.querySelector("[data-followup-progress-bar]");
            const progressText = modal.querySelector("[data-followup-progress-text]");
            let previewObjectUrl = "";
            const close = value => {
                if (previewObjectUrl) {
                    URL.revokeObjectURL(previewObjectUrl);
                    previewObjectUrl = "";
                }
                modal.remove();
                resolve(value);
            };
            const syncAbnormal = () => {
                abnormalField.classList.toggle("is-visible", abnormalCheck.checked);
            };
            modal.querySelectorAll("[data-followup-close]").forEach(button => button.addEventListener("click", () => close(null)));
            modal.addEventListener("click", event => {
                if (event.target === modal) close(null);
            });
            abnormalCheck.addEventListener("change", syncAbnormal);
            fileInput.addEventListener("change", () => {
                const file = fileInput.files?.[0];
                form.elements.photoUrl.value = "";
                progressBar.style.width = "0%";
                progressText.textContent = file ? "待上传" : "可以上传猫咪近况或环境照片";
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
                const file = fileInput.files?.[0];
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
                    progressBar.style.width = "0%";
                    progressText.textContent = "上传中 0%";
                    const result = await uploadFileWithProgress("/api/uploads/clues", file, percent => {
                        progressBar.style.width = `${percent}%`;
                        progressText.textContent = `上传中 ${percent}%`;
                    });
                    form.elements.photoUrl.value = result.url;
                    preview.src = result.url;
                    progressBar.style.width = "100%";
                    progressText.textContent = "上传完成";
                } catch (error) {
                    form.elements.photoUrl.value = "";
                    progressText.textContent = error.message;
                    uploadButton.disabled = false;
                }
            });
            form.addEventListener("submit", event => {
                event.preventDefault();
                const body = Object.fromEntries(new FormData(form).entries());
                body.abnormalFlag = abnormalCheck.checked;
                body.photoUrl = String(body.photoUrl || "").trim();
                body.abnormalDesc = body.abnormalFlag ? String(body.abnormalDesc || "").trim() : "";
                if (fileInput.files?.[0] && !body.photoUrl) {
                    alert("请先点击上传回访照片，上传完成后再保存记录。");
                    return;
                }
                const validationMessage = [
                    validateCleanText(staffMode ? "回访内容" : "猫咪近况", body.content, 5, 500),
                    validateCleanText("猫咪状态描述", body.catCondition, 2, 500),
                    validateCleanText("环境描述", body.environmentDesc, 2, 500),
                    body.abnormalFlag ? validateCleanText("异常说明", body.abnormalDesc, 5, 500) : ""
                ].find(Boolean);
                if (validationMessage) {
                    alert(validationMessage);
                    return;
                }
                close(body);
            });
            syncAbnormal();
            form.elements.content.focus();
        });
    }

    function followupRecordSource(record) {
        const role = normalizeRole(record.submitterRole);
        if (role === "ADMIN") return `管理员：${record.submitterName || record.submitterId || "-"}`;
        if (role === "VOLUNTEER") return `志愿者：${record.submitterName || record.submitterId || "-"}`;
        if (role === "SYSTEM") return record.submitterName || "历史完成记录";
        return `认养人：${record.submitterName || record.submitterId || record.adopterId || "-"}`;
    }

    function inferredCompletedFollowupRecord(task) {
        if (task.status !== "COMPLETED") return null;
        const taskName = followupTaskLabels[task.taskType]?.label || "本次";
        const catName = task.catName || task.catId || "猫咪";
        return {
            content: task.recordContent || `${catName}${taskName}已完成，回访结果为正常。`,
            catCondition: task.catCondition || "猫咪精神、食欲和排便情况正常，已适应当前照护环境。",
            environmentDesc: task.environmentDesc || "生活环境稳定，基础防护和猫砂、饮水、进食条件正常。",
            photoUrl: task.photoUrl || "",
            abnormalDesc: task.abnormalDesc || "无异常",
            volunteerComment: task.volunteerComment || "由历史任务状态生成的完成摘要",
            submitterRole: "SYSTEM",
            submitterName: "历史完成记录",
            submitTime: task.submitTime || task.actualDate || task.planDate || ""
        };
    }

    function followupRecordsText(records, task = null) {
        let rows = Array.isArray(records) ? records : [];
        if (!rows.length && task) {
            const fallback = inferredCompletedFollowupRecord(task);
            if (fallback) rows = [fallback];
        }
        if (!rows.length) return "暂无回访记录";
        return rows.map((record, index) => {
            const time = String(record.submitTime || "-").replace("T", " ");
            const content = record.content || "未填写回访内容";
            const catCondition = record.catCondition || "未填写猫咪状态";
            const environmentDesc = record.environmentDesc || "未填写生活环境";
            const abnormalDesc = record.abnormalDesc || (record.abnormalFlag ? "异常情况未补充说明" : "无异常");
            return `${index + 1}. ${followupRecordSource(record)} / ${time}
内容：${content}
猫咪状态：${catCondition}
生活环境：${environmentDesc}
照片：${record.photoUrl || "-"}
异常说明：${abnormalDesc}
备注：${record.volunteerComment || "-"}`;
        }).join("\n\n");
    }

    function followupRecordRows(records, task = null) {
        let rows = Array.isArray(records) ? records : [];
        if (!rows.length && task) {
            const fallback = inferredCompletedFollowupRecord(task);
            if (fallback) rows = [fallback];
        }
        return rows;
    }

    function followupValue(value, fallback = "-") {
        const text = String(value || "").trim();
        return escapeHtml(text || fallback);
    }

    function followupTime(value) {
        return followupValue(String(value || "").replace("T", " "));
    }

    function followupPhotoHtml(url, alt) {
        const value = String(url || "").trim();
        if (!value) return `<div class="followup-no-photo">暂无照片</div>`;
        return `<a class="followup-photo-link" href="${escapeHtml(value)}" target="_blank" rel="noopener">
            <img ${imageAttrs(value, "followup-detail-photo", alt || "回访照片")}>
        </a>`;
    }

    function openFollowupMessageDialog(title, message) {
        document.querySelector(".followup-detail-modal")?.remove();
        const modal = document.createElement("div");
        modal.className = "reading-modal-backdrop followup-detail-modal";
        modal.innerHTML = `
            <section class="reading-modal followup-detail-dialog" role="dialog" aria-modal="true">
                <div class="reading-modal-head">
                    <h3>${escapeHtml(title)}</h3>
                    <button class="ghost-btn compact" type="button" data-followup-detail-close>关闭</button>
                </div>
                <div class="followup-detail-body">
                    <div class="mis-loading">${escapeHtml(message)}</div>
                </div>
            </section>
        `;
        document.body.appendChild(modal);
        const close = () => modal.remove();
        modal.querySelector("[data-followup-detail-close]").addEventListener("click", close);
        modal.addEventListener("click", event => {
            if (event.target === modal) close();
        });
    }

    function openFollowupDetailDialog(task) {
        document.querySelector(".followup-detail-modal")?.remove();
        const fallback = inferredCompletedFollowupRecord(task) || {};
        const latest = {
            content: task.recordContent || fallback.content || "-",
            catCondition: task.catCondition || fallback.catCondition || "-",
            environmentDesc: task.environmentDesc || fallback.environmentDesc || "-",
            abnormalDesc: task.abnormalDesc || fallback.abnormalDesc || "-",
            photoUrl: task.photoUrl || fallback.photoUrl || "",
            submitTime: task.submitTime || fallback.submitTime || "-"
        };
        const modal = document.createElement("div");
        modal.className = "reading-modal-backdrop followup-detail-modal";
        modal.innerHTML = `
            <section class="reading-modal followup-detail-dialog" role="dialog" aria-modal="true" aria-labelledby="followup-detail-title">
                <div class="reading-modal-head">
                    <h3 id="followup-detail-title">回访任务详情</h3>
                    <button class="ghost-btn compact" type="button" data-followup-detail-close>关闭</button>
                </div>
                <div class="followup-detail-body">
                    <section class="followup-detail-section">
                        <h4>任务信息</h4>
                        <div class="followup-detail-grid">
                            <span>任务编号</span><strong>${followupValue(task.id)}</strong>
                            <span>猫咪</span><strong>${followupValue(task.catName || task.catId)}</strong>
                            <span>认养人</span><strong>${followupValue(task.adopterName || task.adopterId)}</strong>
                            <span>任务类型</span><strong>${followupValue(followupTaskLabels[task.taskType]?.label || task.taskType)}</strong>
                            <span>计划日期</span><strong>${followupValue(task.planDate)}</strong>
                            <span>当前状态</span><strong>${followupValue(followupTaskLabels[task.status]?.label || task.status)}</strong>
                            <span>预警数量</span><strong>${followupValue(`${task.warningCount || 0} 条`)}</strong>
                        </div>
                    </section>
                    <section class="followup-detail-section">
                        <h4>最新回访</h4>
                        <div class="followup-latest-card">
                            ${followupPhotoHtml(latest.photoUrl, "最新回访照片")}
                            <div class="followup-latest-text">
                                <p><b>提交时间</b>${followupTime(latest.submitTime)}</p>
                                <p><b>回访内容</b>${followupValue(latest.content)}</p>
                                <p><b>猫咪状态</b>${followupValue(latest.catCondition)}</p>
                                <p><b>生活环境</b>${followupValue(latest.environmentDesc)}</p>
                                <p><b>异常说明</b>${followupValue(latest.abnormalDesc)}</p>
                            </div>
                        </div>
                    </section>
                </div>
            </section>
        `;
        document.body.appendChild(modal);
        const close = () => modal.remove();
        modal.querySelector("[data-followup-detail-close]").addEventListener("click", close);
        modal.addEventListener("click", event => {
            if (event.target === modal) close();
        });
    }

    function followupTaskDetailText(task, records = []) {
        const fallback = inferredCompletedFollowupRecord(task) || {};
        const latestContent = task.recordContent || fallback.content || "-";
        const latestCatCondition = task.catCondition || fallback.catCondition || "-";
        const latestEnvironment = task.environmentDesc || fallback.environmentDesc || "-";
        const latestAbnormal = task.abnormalDesc || fallback.abnormalDesc || "-";
        const latestSubmitTime = task.submitTime || fallback.submitTime || "-";
        return `任务：${task.id}
猫咪：${task.catName || task.catId}
认养人：${task.adopterName || task.adopterId}
类型：${followupTaskLabels[task.taskType]?.label || task.taskType}
计划日期：${task.planDate || "-"}
状态：${followupTaskLabels[task.status]?.label || task.status}
最新提交：${String(latestSubmitTime).replace("T", " ")}
最新内容：${latestContent}
最新猫咪状态：${latestCatCondition}
最新生活环境：${latestEnvironment}
最新异常说明：${latestAbnormal}
关联预警：${task.warningCount || 0} 条

回访记录：
${followupRecordsText(records, task)}`;
    }

    async function renderAdminWarnings(route, user) {
        // 预警中心汇总高风险申请、回访逾期、健康异常等记录，管理员在这里跟踪处理结果。
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
                        <td>${escapeHtml(warningDisplayTitle(item))}</td>
                        <td>${tag(item.warningType, warningTypeLabels)}</td>
                        <td>${tag(item.warningLevel, riskLabels)}</td>
                        <td>${escapeHtml(item.catName || item.catId || "-")}</td>
                        <td>${escapeHtml(item.userName || item.userId || "-")}</td>
                        <td>${tag(item.status, warningStatusLabels)}</td>
                        <td>${escapeHtml((item.createTime || "").replace("T", " "))}</td>
                        <td><button class="ghost-btn" data-warning-detail="${item.id}">详情</button>${user.role === "ADMIN" && !["HANDLED", "IGNORED"].includes(item.status) ? `<button class="primary-btn" data-warning-handle="${item.id}">处理</button>` : ""}${user.role === "ADMIN" ? `<button class="ghost-btn" data-warning-delete="${item.id}">删除</button>` : ""}</td>
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
            shell.querySelectorAll("[data-warning-delete]").forEach(button => button.addEventListener("click", async () => {
                if (!confirm("确认删除该异常预警？删除后列表不再显示。")) return;
                await api(`/api/admin/warnings/${button.dataset.warningDelete}`, { method: "DELETE" });
                renderAdminWarnings(route, user);
            }));
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function warningDetailText(item) {
        return `预警：${warningDisplayTitle(item)}
类型：${warningTypeLabels[item.warningType]?.label || item.warningType}
等级：${riskLabels[item.warningLevel]?.label || item.warningLevel}
状态：${warningStatusLabels[item.status]?.label || item.status}
猫咪：${item.catName || item.catId || "-"}
认养人：${item.userName || item.userId || "-"}
内容：${warningDisplayContent(item)}
处理人：${item.handlerName || item.handlerId || "-"}
处理意见：${item.handleComment || "-"}
处理时间：${(item.handleTime || "-").replace("T", " ")}`;
    }

    function warningDisplayTitle(item) {
        const title = item?.title || "";
        const cat = item?.catName || item?.catId || "";
        if (title.startsWith("High risk application warning")) return `高风险申请预警${cat ? `：${cat}` : ""}`;
        if (title.startsWith("Medical abnormal warning")) return `健康异常预警${cat ? `：${cat}` : ""}`;
        if (title.startsWith("Abnormal follow-up:")) return `回访异常：${cat || title.replace("Abnormal follow-up:", "").trim() || "-"}`;
        if (title.startsWith("Follow-up overdue:")) return `回访逾期：${cat || title.replace("Follow-up overdue:", "").trim() || "-"}`;
        if (title.startsWith("Follow-up warning:")) return `回访预警：${cat || title.replace("Follow-up warning:", "").trim() || "-"}`;
        return title || `${warningTypeLabels[item?.warningType]?.label || "异常预警"}${cat ? `：${cat}` : ""}`;
    }

    function warningDisplayContent(item) {
        const content = item?.content || "";
        if (content.startsWith("Cat: ")) {
            return content
                .replace("Cat: ", "猫咪：")
                .replace("; adopter: ", "；认养人：")
                .replace("; planDate: ", "；计划日期：")
                .replace("; taskType: ", "；回访类型：")
                .replace("; detail: ", "；详情：");
        }
        if (content.startsWith("Follow-up task is overdue. Planned date: ")) {
            return `回访任务已逾期，计划日期：${content.replace("Follow-up task is overdue. Planned date: ", "")}`;
        }
        return content || "-";
    }

    async function renderAdminUsers(route, user) {
        // 用户管理页用于维护系统账号、角色和启用状态，避免直接改库造成权限数据不一致。
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
        // 新增或编辑用户前先做基础表单校验，保证手机号、账号和说明字段符合后端业务要求。
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
            if (isHospitalUser(user) && redirect.split("?")[0] === "#/admin/dashboard") {
                return "#/admin/medical";
            }
            return user && route && normalizeRole(user.role) !== "STUDENT" && canVisit(route, user.role) ? redirect : "#/403";
        }
        return redirect.startsWith("#/") ? redirect : defaultHash;
    }

    function roleLanding(user) {
        const role = normalizeRole(user?.role);
        if (!user) return "#/";
        if (role === "HOSPITAL") return "#/admin/medical";
        if (role === "VOLUNTEER" || role === "ADMIN") return "#/admin/dashboard";
        return "#/profile";
    }

    async function renderLogin(route, user) {
        // 登录页提交账号密码后，再调用 /api/users/me 校验 token，确保前端角色信息与后端一致。
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
                    <div class="auth-inline-row">
                        <label class="check"><input name="remember" type="checkbox"> 记住登录</label>
                        <button class="link-btn" type="button" id="forgot-password-toggle">忘记密码？</button>
                    </div>
                    <button class="primary-btn">登录</button>
                    <p id="login-message" class="form-message"></p>
                    <div class="forgot-panel" id="forgot-password-panel" hidden>
                        <div class="forgot-panel-head">
                            <strong>找回密码</strong>
                            <span>请输入注册信息完成身份校验</span>
                        </div>
                        <label>账号 / 学号 / 手机号<input name="resetAccount" autocomplete="username"></label>
                        <label>注册手机号<input name="resetPhone" inputmode="tel"></label>
                        <label>身份证号<input name="resetIdCard"></label>
                        <label>新密码<input name="resetPassword" type="password" minlength="6" autocomplete="new-password"></label>
                        <button class="ghost-btn" type="button" id="forgot-password-submit">重置密码</button>
                    </div>
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
        document.getElementById("forgot-password-toggle").addEventListener("click", () => {
            const panel = document.getElementById("forgot-password-panel");
            panel.hidden = !panel.hidden;
        });
        document.getElementById("forgot-password-submit").addEventListener("click", async () => {
            const form = document.getElementById("login-form");
            const message = document.getElementById("login-message");
            const body = {
                account: form.elements.resetAccount.value,
                phone: form.elements.resetPhone.value,
                idCard: form.elements.resetIdCard.value,
                newPassword: form.elements.resetPassword.value
            };
            try {
                requireValid(message, [
                    validateCleanText("账号", body.account, 2, 30),
                    body.phone && /^1[3-9]\d{9}$/.test(body.phone) ? "" : "手机号格式不正确",
                    body.idCard && /^\d{17}[\dXx]$/.test(body.idCard) ? "" : "身份证号格式不正确",
                    body.newPassword && body.newPassword.length >= 6 ? "" : "新密码长度至少 6 位"
                ]);
                message.textContent = "正在重置密码...";
                await api("/api/users/password/forgot", {
                    method: "POST",
                    body: JSON.stringify(body)
                });
                form.elements.password.value = body.newPassword;
                document.getElementById("forgot-password-panel").hidden = true;
                message.textContent = "密码已重置，请使用新密码登录。";
            } catch (error) {
                message.textContent = error.message;
            }
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
                    <div class="auth-cat-showcase register-cat-showcase">
                        <img src="/uploads/cats/cat_02_01.jpg" alt="校园猫咪坐在草地上的照片">
                        <div class="auth-cat-note">
                            <strong>注册后可以提交线索和认养申请</strong>
                            <span>请填写真实联系方式，志愿者会根据申请和回访信息与你联系。</span>
                        </div>
                    </div>
                    <div class="register-tips">
                        <span>实名信息用于申请审核</span>
                        <span>手机号用于志愿者联系</span>
                        <span>密码至少 6 位</span>
                    </div>
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
        const hideFrontSelfService = !canUseFrontSelfService(user);
        try {
            const [stats, cats, notices, roleData] = await Promise.all([
                api("/api/dashboard/stats").catch(() => ({})),
                api("/api/cats/public").catch(() => []),
                api("/api/notices").catch(() => []),
                loadRolePortalData(user)
            ]);
            const visibleNotices = filterNoticesForUser(notices, user);
            panel.innerHTML = `
                <section class="hero">
                    <div class="hero-copy">
                        <p class="eyebrow">Campus Cat Adoption</p>
                        <h1>合肥工业大学校园流浪猫在线认养系统</h1>
                        <p>校园公益认养与流浪猫全生命周期管理平台，把发现上报、核实建档、医疗记录、在线认养、审核交接和回访预警放进同一条可追溯链路。</p>
                        <div class="actions">
                            <a class="primary-btn" href="#/cats">查看可认养猫咪</a>
                            ${hideFrontSelfService ? "" : `<a class="ghost-btn" href="${user ? "#/clues/submit" : "#/login?redirect=%23%2Fclues%2Fsubmit"}">上报猫咪线索</a>`}
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
                    ].filter(item => !hideFrontSelfService || !["回访提醒", "发现线索"].includes(item.title)).map(item => `
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
                            ${visibleNotices.map(item => {
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
                        <div class="actions">${hideFrontSelfService ? "" : `<a class="primary-btn" href="${user ? "#/clues/submit" : "#/login?redirect=%23%2Fclues%2Fsubmit"}">提交发现线索</a>`}<a class="ghost-btn" href="${user ? "#/my/messages" : "#/login?redirect=%23%2Fmy%2Fmessages"}">我的消息</a></div>
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
            const notices = filterNoticesForUser(await api("/api/notices"), user);
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
            if (!noticeVisibleForUser(notice, user)) {
                panel.innerHTML = `<div class="mis-error">当前账号不在该公告的发布范围内。</div>`;
                return;
            }
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

    function profileMetricCard(label, value, hint, href) {
        return `<a class="profile-metric-card" href="${escapeHtml(href)}">
            <span>${escapeHtml(label)}</span>
            <strong>${escapeHtml(value ?? 0)}</strong>
            <small>${escapeHtml(hint)}</small>
        </a>`;
    }

    async function renderProfile(route, user) {
        userShell(route, user, `${pageHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载个人中心...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        const hideFrontSelfService = !canUseFrontSelfService(user);
        try {
            const [freshUser, clues, apps, tasks, unread] = await Promise.all([
                api("/api/users/me"),
                hideFrontSelfService ? Promise.resolve([]) : api("/api/my/clues").catch(() => []),
                hideFrontSelfService ? Promise.resolve([]) : api("/api/my/adoption/applications").catch(() => []),
                hideFrontSelfService ? Promise.resolve([]) : api("/api/my/followup/tasks").catch(() => []),
                api("/api/user/messages/unread-count").catch(() => 0)
            ]);
            saveAuth({ token: token(), user: freshUser });
            panel.innerHTML = `
                <section class="profile-dashboard">
                    <article class="profile-account-card">
                        <div class="profile-avatar">${escapeHtml((freshUser.userName || "我").slice(0, 1))}</div>
                        <div>
                            <p class="eyebrow">ACCOUNT</p>
                            <h2>${escapeHtml(freshUser.userName)}</h2>
                            <span>${escapeHtml(freshUser.schoolNo || "-")} · ${escapeHtml(freshUser.phone || "-")}</span>
                            <span>${escapeHtml(freshUser.college || "-")}</span>
                        </div>
                        <div class="profile-role-actions">
                            <span class="mis-tag primary">${escapeHtml(roleLabels[normalizeRole(freshUser.role)] || normalizeRole(freshUser.role))}</span>
                            ${normalizeRole(freshUser.role) !== "STUDENT" ? `<a class="primary-btn compact" href="${escapeHtml(roleLanding(freshUser))}">进入后台</a>` : ""}
                        </div>
                    </article>
                    <div class="profile-quick-grid">
                        ${hideFrontSelfService ? "" : profileMetricCard("我的线索", clues.length, "查看核实进度", "#/my/clues")}
                        ${hideFrontSelfService ? "" : profileMetricCard("我的申请", apps.length, "查看审核与交接", "#/my/applications")}
                        ${hideFrontSelfService ? "" : profileMetricCard("我的回访", tasks.length, "上传猫咪近况", "#/my/followups")}
                        ${profileMetricCard("未读消息", unread, "查看系统提醒", "#/my/messages")}
                    </div>
                </section>
                <section class="profile-edit-panel">
                    <div class="mis-section-head"><h2>资料维护</h2><span>${hideFrontSelfService ? "用于医疗协作、消息通知和身份核验" : "用于线索、申请、回访联系和身份核验"}</span></div>
                    <form class="mis-form front-form profile-form" id="profile-form">
                        <label>姓名<input name="userName" value="${escapeHtml(freshUser.userName || "")}" required></label>
                        <label>手机号<input name="phone" value="${escapeHtml(freshUser.phone || "")}" required></label>
                        <label>学院/单位<input name="college" value="${escapeHtml(freshUser.college || "")}" required></label>
                        <label class="wide">养宠经验<textarea name="petExperience">${escapeHtml(freshUser.petExperience || "")}</textarea></label>
                        <div class="mis-form-actions"><button class="primary-btn">保存资料</button><span id="profile-message"></span></div>
                    </form>
                </section>
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
                    throw new Error("智能体暂时不可用，请稍后再试。");
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
                thinking.textContent = clean(answer) || "暂时没有生成有效回答。";
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
                thinking.textContent = error.message || "智能体暂时不可用，请稍后再试。";
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

    async function renderFrontAdminNotices(route, user) {
        // 公告管理页：表单和列表共用一个页面，edit 参数决定当前是新增还是编辑。
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载公告发布台...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            if (params.get("publishStatus")) query.set("publishStatus", params.get("publishStatus"));
            if (params.get("noticeType")) query.set("noticeType", params.get("noticeType"));
            // 编辑时需要全量公告用于回显，筛选列表仍按当前筛选条件展示。
            const [notices, allNotices] = await Promise.all([
                api(`/api/admin/notices${query.toString() ? `?${query}` : ""}`),
                query.toString() ? api("/api/admin/notices") : Promise.resolve(null)
            ]);
            const editingId = params.get("edit") || "";
            const editing = (allNotices || notices).find(item => String(item.id) === editingId);
            const roleOptions = noticeRoleOptions();
            const selectedRoles = noticeTargetRoleSet(editing?.targetRoles);
            panel.innerHTML = `
                <section class="front-notice-admin">
                    <form class="mis-form front-form front-notice-form" id="front-notice-form">
                        <input type="hidden" name="id" value="${escapeHtml(editing?.id || "")}">
                        <input type="hidden" name="imageUrl" id="front-notice-image-url" value="${escapeHtml(editing?.imageUrl || "")}">
                        <div class="mis-section-head"><h2>${editing ? "编辑公告" : "发布新公告"}</h2><span>公告会同步显示到前台公告页</span></div>
                        <label class="wide">标题<input name="title" value="${escapeHtml(editing?.title || "")}" placeholder="例如：本周认养开放日安排" required></label>
                        <label>类型<select name="noticeType">
                            ${["SYSTEM", "ADOPTION", "FOLLOWUP"].map(type => `<option value="${type}" ${editing?.noticeType === type ? "selected" : ""}>${noticeTypeLabel(type)}</option>`).join("")}
                        </select></label>
                        <label>状态<select name="publishStatus">
                            ${["DRAFT", "PUBLISHED", "OFFLINE"].map(status => `<option value="${status}" ${editing?.publishStatus === status ? "selected" : ""}>${noticeStatusLabel(status)}</option>`).join("")}
                        </select></label>
                        <label>排序<input name="sortOrder" type="number" value="${escapeHtml(editing?.sortOrder ?? 0)}"></label>
                        <div class="wide notice-role-scope">
                            <strong>发布范围</strong>
                            <div>
                                ${roleOptions.map(option => `<label class="role-check"><input type="checkbox" name="targetRoles" value="${option.value}" ${selectedRoles.has(option.value) ? "checked" : ""}><span class="role-check-card" data-role="${option.value}"><span class="role-cat-icon" aria-hidden="true">${noticeRoleEmoji(option.value)}</span><span class="role-check-label">${option.label}</span></span></label>`).join("")}
                            </div>
                        </div>
                        <div class="wide notice-image-editor">
                            <div class="notice-image-preview ${editing?.imageUrl ? "" : "is-empty"}">
                                <img id="front-notice-image-preview" class="notice-edit-preview-image" src="${escapeHtml(editing?.imageUrl || "")}" alt="公告图片预览" ${editing?.imageUrl ? "" : "hidden"}>
                                <span id="front-notice-image-placeholder">暂无公告图片</span>
                            </div>
                            <div class="notice-image-controls">
                                <strong>公告图片</strong>
                                <input id="front-notice-image-file" type="file" accept="image/jpeg,image/png,image/webp,image/gif">
                                <div class="notice-image-actions">
                                    <button class="ghost-btn compact" type="button" id="front-notice-upload-image">上传/更换图片</button>
                                    <button class="ghost-btn compact" type="button" id="front-notice-clear-image">移除图片</button>
                                </div>
                                <span id="front-notice-upload-message">${editing?.imageUrl ? "当前公告已设置图片" : "未上传图片时公告不显示配图"}</span>
                            </div>
                        </div>
                        <label class="wide">公告内容<textarea name="content" rows="8" placeholder="填写公告正文，建议包含时间、地点、对象和注意事项" required>${escapeHtml(editing?.content || "")}</textarea></label>
                        <div class="mis-form-actions">
                            <button class="primary-btn">${editing ? "保存公告" : "新增公告"}</button>
                            ${editing ? `<a class="ghost-btn" href="#/admin/notices">取消编辑</a>` : ""}
                            <span id="front-notice-message"></span>
                        </div>
                    </form>
                    <section class="front-notice-list">
                        <div class="mis-section-head"><h2>公告列表</h2><span>${notices.length} 条</span></div>
                        <div class="mis-filter-row">
                            <select id="front-notice-status"><option value="">全部状态</option>${["DRAFT", "PUBLISHED", "OFFLINE"].map(v => `<option value="${v}" ${params.get("publishStatus") === v ? "selected" : ""}>${noticeStatusLabel(v)}</option>`).join("")}</select>
                            <select id="front-notice-type"><option value="">全部类型</option>${["SYSTEM", "ADOPTION", "FOLLOWUP"].map(v => `<option value="${v}" ${params.get("noticeType") === v ? "selected" : ""}>${noticeTypeLabel(v)}</option>`).join("")}</select>
                            <button class="ghost-btn" id="front-notice-filter">筛选</button>
                        </div>
                        <div class="front-notice-admin-list">
                            ${(notices || []).map(item => `
                                <article class="front-notice-admin-card">
                                    <img ${imageAttrs(item.imageUrl || noticeMeta(item).image, "front-notice-admin-thumb", item.title)}>
                                    <div>
                                        <strong>${escapeHtml(item.title)}</strong>
                                        <p>${escapeHtml(item.content)}</p>
                                        <span>${noticeTypeLabel(item.noticeType)} · ${noticeStatusLabel(item.publishStatus)} · ${noticeRoleText(item.targetRoles)} · ${(item.publishTime || item.updateTime || "").replace("T", " ")}</span>
                                    </div>
                                    <div class="actions">
                                        <a class="ghost-btn compact" href="#/admin/notices?edit=${encodeURIComponent(item.id)}">编辑</a>
                                        <button class="ghost-btn compact" data-front-notice-publish="${escapeHtml(item.id)}">发布</button>
                                        <button class="ghost-btn compact" data-front-notice-offline="${escapeHtml(item.id)}">下架</button>
                                        <button class="ghost-btn compact" data-front-notice-delete="${escapeHtml(item.id)}">删除</button>
                                    </div>
                                </article>
                            `).join("") || `<div class="mis-empty">暂无公告</div>`}
                        </div>
                    </section>
                </section>
            `;
            bindFrontNoticeAdminEvents(route, user);
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function noticeTypeLabel(value) {
        return ({ SYSTEM: "系统公告", ADOPTION: "认养公告", FOLLOWUP: "回访提醒" })[value] || value || "-";
    }

    function noticeStatusLabel(value) {
        return ({ DRAFT: "草稿", PUBLISHED: "已发布", OFFLINE: "已下架" })[value] || value || "-";
    }

    function noticeRoleOptions() {
        return [
            { value: "STUDENT", label: "普通用户" },
            { value: "VOLUNTEER", label: "志愿者" },
            { value: "HOSPITAL", label: "合作医院" },
            { value: "ADMIN", label: "管理员" }
        ];
    }

    function noticeRoleEmoji(value) {
        return ({ STUDENT: "😺", VOLUNTEER: "😻", HOSPITAL: "😽", ADMIN: "😼" })[value] || "😺";
    }

    function noticeTargetRoleSet(value) {
        // 后端按三范式存储角色关联，前端仍按逗号字符串处理，便于表单复选框回显。
        const roles = String(value || "STUDENT,VOLUNTEER,HOSPITAL,ADMIN").split(",").map(item => item.trim().toUpperCase()).filter(Boolean);
        return new Set(roles.length ? roles : noticeRoleOptions().map(item => item.value));
    }

    function noticeRoleText(value) {
        const selected = noticeTargetRoleSet(value);
        const labels = noticeRoleOptions().filter(item => selected.has(item.value)).map(item => item.label);
        return labels.length === noticeRoleOptions().length ? "全部角色" : labels.join("、");
    }

    function noticeVisibleForUser(notice, user) {
        const role = normalizeRole(user?.role || "STUDENT");
        return noticeTargetRoleSet(notice?.targetRoles).has(role);
    }

    function filterNoticesForUser(notices, user) {
        return (notices || []).filter(item => noticeVisibleForUser(item, user));
    }

    function frontNoticeBaseHash() {
        const next = new URLSearchParams();
        const status = document.getElementById("front-notice-status")?.value;
        const type = document.getElementById("front-notice-type")?.value;
        if (status) next.set("publishStatus", status);
        if (type) next.set("noticeType", type);
        return `#/admin/notices${next.toString() ? `?${next}` : ""}`;
    }

    function bindFrontNoticeAdminEvents(route, user) {
        document.getElementById("front-notice-filter").addEventListener("click", () => {
            window.location.hash = frontNoticeBaseHash();
        });
        const imageUrlInput = document.getElementById("front-notice-image-url");
        const imagePreview = document.getElementById("front-notice-image-preview");
        const imagePreviewWrap = imagePreview?.closest(".notice-image-preview");
        const imagePlaceholder = document.getElementById("front-notice-image-placeholder");
        const uploadMessage = document.getElementById("front-notice-upload-message");
        const setNoticeImagePreview = url => {
            // 图片上传成功后先更新隐藏字段和预览图，真正保存仍由公告表单提交完成。
            const value = String(url || "").trim();
            imageUrlInput.value = value;
            if (value) {
                imagePreview.src = value;
                imagePreview.hidden = false;
                imagePreviewWrap?.classList.remove("is-empty");
                if (imagePlaceholder) imagePlaceholder.hidden = true;
            } else {
                imagePreview.removeAttribute("src");
                imagePreview.hidden = true;
                imagePreviewWrap?.classList.add("is-empty");
                if (imagePlaceholder) imagePlaceholder.hidden = false;
            }
        };
        const uploadNoticeImage = async () => {
            const file = document.getElementById("front-notice-image-file")?.files?.[0];
            if (!file) {
                uploadMessage.textContent = "请先选择一张图片";
                return;
            }
            const button = document.getElementById("front-notice-upload-image");
            button.disabled = true;
            uploadMessage.textContent = "正在上传 0%";
            try {
                const result = await uploadNoticeImageFile(file, percent => {
                    uploadMessage.textContent = `正在上传 ${percent}%`;
                });
                setNoticeImagePreview(result.url);
                uploadMessage.textContent = "图片已上传，保存公告后生效";
            } catch (error) {
                uploadMessage.textContent = error.message;
            } finally {
                button.disabled = false;
            }
        };
        document.getElementById("front-notice-upload-image")?.addEventListener("click", uploadNoticeImage);
        document.getElementById("front-notice-image-file")?.addEventListener("change", () => {
            uploadNoticeImage();
        });
        document.getElementById("front-notice-clear-image")?.addEventListener("click", () => {
            setNoticeImagePreview("");
            uploadMessage.textContent = "已移除图片，保存公告后生效";
        });
        shell.querySelectorAll('input[name="targetRoles"]').forEach(input => {
            input.addEventListener("change", () => {
                const checked = shell.querySelectorAll('input[name="targetRoles"]:checked');
                if (!checked.length) {
                    input.checked = true;
                    const message = document.getElementById("front-notice-message");
                    if (message) message.textContent = "发布范围至少选择一个角色";
                }
            });
        });
        document.getElementById("front-notice-form").addEventListener("submit", async event => {
            event.preventDefault();
            // 公告表单提交保持旧接口字段不变：title/content/status/imageUrl/targetRoles 一次性提交。
            const form = new FormData(event.currentTarget);
            const id = form.get("id");
            const message = document.getElementById("front-notice-message");
            const targetRoles = form.getAll("targetRoles").join(",");
            if (!targetRoles) {
                message.textContent = "请至少选择一个发布范围";
                return;
            }
            const selectedNoticeFile = document.getElementById("front-notice-image-file")?.files?.[0];
            if (selectedNoticeFile && !String(form.get("imageUrl") || "").trim()) {
                message.textContent = "图片还没有上传成功，请先上传完成后再保存公告";
                return;
            }
            const body = {
                title: form.get("title"),
                content: form.get("content"),
                noticeType: form.get("noticeType"),
                publishStatus: form.get("publishStatus"),
                sortOrder: Number(form.get("sortOrder") || 0),
                imageUrl: form.get("imageUrl"),
                targetRoles,
                sendMessage: true
            };
            try {
                message.textContent = "正在保存...";
                await api(id ? `/api/admin/notices/${id}` : "/api/admin/notices", {
                    method: id ? "PUT" : "POST",
                    body: JSON.stringify(body)
                });
                message.textContent = "公告已保存";
                window.location.hash = "#/admin/notices";
                renderFrontAdminNotices(route, user);
            } catch (error) {
                message.textContent = error.message;
            }
        });
        shell.querySelectorAll("[data-front-notice-publish]").forEach(button => button.addEventListener("click", async () => {
            await runFrontNoticeAction(button, "正在发布...", "公告已发布", async () => {
                await api(`/api/admin/notices/${button.dataset.frontNoticePublish}/publish`, { method: "PUT" });
            }, route, user);
        }));
        shell.querySelectorAll("[data-front-notice-offline]").forEach(button => button.addEventListener("click", async () => {
            await runFrontNoticeAction(button, "正在下架...", "公告已下架", async () => {
                await api(`/api/admin/notices/${button.dataset.frontNoticeOffline}/offline`, { method: "PUT" });
            }, route, user);
        }));
        shell.querySelectorAll("[data-front-notice-delete]").forEach(button => button.addEventListener("click", async () => {
            if (!confirm("确认删除该公告？")) return;
            await runFrontNoticeAction(button, "正在删除...", "公告已删除", async () => {
                await api(`/api/admin/notices/${button.dataset.frontNoticeDelete}`, { method: "DELETE" });
            }, route, user);
        }));
    }

    async function runFrontNoticeAction(button, pendingText, doneText, action, route, user) {
        // 发布、下架、删除共用按钮状态处理，操作完成后重新加载公告列表。
        const oldText = button.textContent;
        const message = document.getElementById("front-notice-message") || document.getElementById("front-notice-upload-message");
        button.disabled = true;
        button.textContent = pendingText;
        if (message) message.textContent = pendingText;
        try {
            await action();
            if (message) message.textContent = doneText;
            await renderFrontAdminNotices(route, user);
        } catch (error) {
            button.disabled = false;
            button.textContent = oldText;
            if (message) message.textContent = error.message;
        }
    }

    async function renderAdminLogs(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载操作日志...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            ["operatorKeyword", "operationType", "bizType", "status"].forEach(key => { if (params.get(key)) query.set(key, params.get(key)); });
            const logs = await api(`/api/admin/logs${query.toString() ? `?${query}` : ""}`);
            const statusOptions = ["已完成", "待处理", "处理中", "已处理", "已忽略", "已删除", "已生成", "已发布", "已下架", "已取消", "已作废", "待交接", "已交接", "草稿"];
            panel.innerHTML = `
                <div class="mis-filter-row"><input id="log-operator" value="${escapeHtml(params.get("operatorKeyword") || "")}" placeholder="操作人"><input id="log-type" value="${escapeHtml(params.get("operationType") || "")}" placeholder="操作类型"><input id="log-biz" value="${escapeHtml(params.get("bizType") || "")}" placeholder="业务类型"><select id="log-status"><option value="">全部状态</option>${statusOptions.map(status => `<option value="${escapeHtml(status)}"${params.get("status") === status ? " selected" : ""}>${escapeHtml(status)}</option>`).join("")}</select><button class="ghost-btn" id="log-filter">筛选</button></div>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>时间</th><th>操作人</th><th>操作</th><th>业务</th><th>对象</th><th>状态</th><th>操作</th></tr></thead><tbody>
                    ${(logs || []).map(item => `<tr><td>${escapeHtml((item.createTime || "").replace("T", " "))}</td><td>${escapeHtml(item.operatorName || item.operatorId || "-")}</td><td>${escapeHtml(item.operationType)}</td><td>${escapeHtml(item.bizType)}</td><td>${escapeHtml(item.bizId)}</td><td>${escapeHtml(item.status)}</td><td><button class="ghost-btn" data-log-detail="${item.id}">详情</button></td></tr>`).join("") || `<tr><td colspan="7"><div class="mis-empty">暂无日志</div></td></tr>`}
                </tbody></table></div>
            `;
            document.getElementById("log-filter").addEventListener("click", () => {
                const next = new URLSearchParams();
                [["operatorKeyword", "log-operator"], ["operationType", "log-type"], ["bizType", "log-biz"], ["status", "log-status"]].forEach(([key, id]) => {
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

    async function renderAdminLogsLocalized(route, user) {
        adminShell(route, user, `${adminHero(route)}<section class="mis-table-panel"><div class="mis-loading">正在加载操作日志...</div></section>`);
        const panel = shell.querySelector(".mis-table-panel");
        try {
            const params = new URLSearchParams(location.hash.split("?")[1] || "");
            const query = new URLSearchParams();
            ["operatorKeyword", "operationType", "bizType", "status"].forEach(key => { if (params.get(key)) query.set(key, params.get(key)); });
            const logs = await api(`/api/admin/logs${query.toString() ? `?${query}` : ""}`);
            const statusOptions = ["已完成", "待处理", "处理中", "已处理", "已忽略", "已删除", "已生成", "已发布", "已下架", "已取消", "已作废", "待交接", "已交接", "草稿"];
            panel.innerHTML = `
                <div class="mis-filter-row">
                    <input id="log-operator" value="${escapeHtml(params.get("operatorKeyword") || "")}" placeholder="操作人">
                    <input id="log-type" value="${escapeHtml(params.get("operationType") || "")}" placeholder="操作类型">
                    <input id="log-biz" value="${escapeHtml(params.get("bizType") || "")}" placeholder="业务类型">
                    <select id="log-status">
                        <option value="">全部状态</option>
                        ${statusOptions.map(status => `<option value="${escapeHtml(status)}"${params.get("status") === status ? " selected" : ""}>${escapeHtml(status)}</option>`).join("")}
                    </select>
                    <button class="ghost-btn" id="log-filter">筛选</button>
                </div>
                <div class="mis-table-wrap"><table class="mis-table"><thead><tr><th>时间</th><th>操作人</th><th>操作</th><th>业务</th><th>对象</th><th>状态</th><th>操作</th></tr></thead><tbody>
                    ${(logs || []).map(item => `<tr>
                        <td>${escapeHtml((item.createTime || "").replace("T", " "))}</td>
                        <td>${escapeHtml(item.operatorName || item.operatorId || "-")}</td>
                        <td>${escapeHtml(logOperationLabel(item.operationType))}</td>
                        <td>${escapeHtml(logBizLabel(item.bizType))}</td>
                        <td>${escapeHtml(item.bizId || "-")}</td>
                        <td>${escapeHtml(logValueLabel(item.afterData || "-"))}</td>
                        <td><button class="ghost-btn" data-log-detail="${item.id}">详情</button></td>
                    </tr>`).join("") || `<tr><td colspan="7"><div class="mis-empty">暂无日志</div></td></tr>`}
                </tbody></table></div>
            `;
            document.getElementById("log-filter").addEventListener("click", () => {
                const next = new URLSearchParams();
                [["operatorKeyword", "log-operator"], ["operationType", "log-type"], ["bizType", "log-biz"], ["status", "log-status"]].forEach(([key, id]) => {
                    const value = document.getElementById(id).value;
                    if (value) next.set(key, value);
                });
                window.location.hash = `#/admin/logs${next.toString() ? `?${next}` : ""}`;
            });
            shell.querySelectorAll("[data-log-detail]").forEach(button => button.addEventListener("click", async () => {
                const item = await api(`/api/admin/logs/${button.dataset.logDetail}`);
                alert(`操作人：${item.operatorName || item.operatorId || "-"}\n操作类型：${logOperationLabel(item.operationType)}\n业务：${logBizLabel(item.bizType)}/${item.bizId || "-"}\n变更前：${logValueLabel(item.beforeData || "-")}\n变更后：${logValueLabel(item.afterData || "-")}\n备注：${logValueLabel(item.remark || "-")}`);
            }));
        } catch (error) {
            panel.innerHTML = `<div class="mis-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function logOperationLabel(value) {
        return ({
            "Auto-generate adoption agreement": "自动生成认养协议",
            "Generate adoption agreement": "生成认养协议",
            "Edit adoption agreement": "编辑认养协议",
            "Cat enters follow-up after handover": "交接后猫咪进入回访",
            "Complete adoption handover": "完成认养交接",
            "Adoption application handed over": "认养申请已交接",
            "Submit staff follow-up record": "工作人员填写回访记录",
            "Submit adopter follow-up record": "认养人提交回访记录",
            "Submit follow-up record": "提交回访记录",
            "Mark follow-up abnormal": "标记回访异常",
            "Handle warning": "处理异常预警",
            "Delete warning": "删除异常预警",
            "Create notice": "新增公告",
            "Update notice": "更新公告",
            "Publish notice": "发布公告",
            "Offline notice": "下架公告",
            "Delete notice": "删除公告",
            "Create dict item": "新增字典项",
            "Update dict item": "更新字典项",
            "Toggle dict item": "启停字典项",
            "Delete dict item": "删除字典项",
            "Create follow-up tasks": "生成回访任务",
            "Refresh overdue follow-up task": "刷新逾期回访任务",
            "Create follow-up warning": "生成回访预警",
            "Follow-up closed and cat adopted": "回访结束并标记已认养"
        })[value] || value || "-";
    }

    function logBizLabel(value) {
        return ({
            USER: "用户",
            CAT: "猫咪",
            CLUE: "线索",
            MEDICAL: "医疗记录",
            APPLICATION: "认养申请",
            AGREEMENT: "认养协议",
            FOLLOWUP_TASK: "回访任务",
            WARNING: "异常预警",
            NOTICE: "公告",
            DICT: "字典"
        })[value] || value || "-";
    }

    function logValueLabel(value) {
        return String(value || "-")
            .replaceAll("PENDING_VERIFY", "待核实")
            .replaceAll("VERIFIED_VALID", "已核实有效")
            .replaceAll("CREATED_CAT", "已建档")
            .replaceAll("INVALID", "无效")
            .replaceAll("OBSERVING", "观察中")
            .replaceAll("MEDICAL", "医疗中")
            .replaceAll("ADOPTABLE", "可认养")
            .replaceAll("APPLYING", "申请中")
            .replaceAll("ADOPTED", "已认养")
            .replaceAll("FOLLOWING", "回访中")
            .replaceAll("PENDING_INITIAL", "待初审")
            .replaceAll("PENDING_FINAL", "待终审")
            .replaceAll("INITIAL_REJECTED", "初审拒绝")
            .replaceAll("FINAL_REJECTED", "终审拒绝")
            .replaceAll("PENDING_HANDOVER", "待交接")
            .replaceAll("HANDED_OVER", "已交接")
            .replaceAll("CANCELLED", "已取消")
            .replaceAll("GENERATED", "已生成")
            .replaceAll("COMPLETED", "已完成")
            .replaceAll("HANDLED", "已处理")
            .replaceAll("IGNORED", "已忽略")
            .replaceAll("PROCESSING", "处理中")
            .replaceAll("PENDING", "待处理")
            .replaceAll("PUBLISHED", "已发布")
            .replaceAll("OFFLINE", "已下架")
            .replaceAll("DELETED", "已删除")
            .replaceAll("VOID", "已作废");
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
        // 单页应用的总调度入口：先完成路由规范化和权限校验，再分发到具体页面渲染函数。
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
        if (isAdmin && isHospitalUser(user) && cleanHash === "#/admin/dashboard") {
            window.location.hash = "#/admin/medical";
            return;
        }
        if (isAdmin && (normalizeRole(user.role) === "STUDENT" || !canVisit(route, user.role))) {
            renderForbidden(user);
            return;
        }

        if (!isAdmin && route.roles && !user) {
            window.location.hash = loginHashFor(hash);
            return;
        }

        if (!isAdmin && user && !canVisit(route, user.role)) {
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
            renderFrontAdminNotices(route, user);
        } else if (route.path === "#/admin/logs") {
            renderAdminLogsLocalized(route, user);
        } else {
            renderPlaceholder(route, user, isAdmin);
        }
    }

    window.addEventListener("hashchange", render);
    window.addEventListener("storage", render);
    render();
})();
