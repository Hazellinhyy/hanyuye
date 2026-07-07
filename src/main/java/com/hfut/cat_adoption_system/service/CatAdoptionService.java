package com.hfut.cat_adoption_system.service;

import com.hfut.cat_adoption_system.auth.AuthContext;
import com.hfut.cat_adoption_system.auth.AuthPrincipal;
import com.hfut.cat_adoption_system.auth.PasswordHasher;
import com.hfut.cat_adoption_system.auth.TokenService;
import com.hfut.cat_adoption_system.common.BusinessException;
import com.hfut.cat_adoption_system.common.DataQualityValidator;
import com.hfut.cat_adoption_system.dto.*;
import com.hfut.cat_adoption_system.mapper.*;
import com.hfut.cat_adoption_system.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class CatAdoptionService {
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("yyMMdd");

    private final CodeGenerator codeGenerator;
    private final UserMapper userMapper;
    private final CatMapper catMapper;
    private final RescueReportMapper rescueReportMapper;
    private final MedicalRecordMapper medicalRecordMapper;
    private final ApplicationMapper applicationMapper;
    private final AdoptionAuditMapper adoptionAuditMapper;
    private final FollowupMapper followupMapper;
    private final AgreementMapper agreementMapper;
    private final FollowupTaskMapper followupTaskMapper;
    private final WarningRecordMapper warningRecordMapper;
    private final SystemMessageMapper systemMessageMapper;
    private final DashboardMapper dashboardMapper;
    private final NoticeMapper noticeMapper;
    private final AuditLogMapper auditLogMapper;
    private final OperationLogMapper operationLogMapper;
    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;
    private final ProductOrderMapper productOrderMapper;
    private final DonationRecordMapper donationRecordMapper;
    private final DonationChannelMapper donationChannelMapper;
    private final CommunityMapper communityMapper;
    private final CatPhotoMapper catPhotoMapper;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    public CatAdoptionService(CodeGenerator codeGenerator,
                              UserMapper userMapper,
                              CatMapper catMapper,
                              RescueReportMapper rescueReportMapper,
                              MedicalRecordMapper medicalRecordMapper,
                              ApplicationMapper applicationMapper,
                              AdoptionAuditMapper adoptionAuditMapper,
                              FollowupMapper followupMapper,
                              AgreementMapper agreementMapper,
                              FollowupTaskMapper followupTaskMapper,
                              WarningRecordMapper warningRecordMapper,
                              SystemMessageMapper systemMessageMapper,
                              DashboardMapper dashboardMapper,
                              NoticeMapper noticeMapper,
                              AuditLogMapper auditLogMapper,
                              OperationLogMapper operationLogMapper,
                              FavoriteMapper favoriteMapper,
                              ProductMapper productMapper,
                              ProductOrderMapper productOrderMapper,
                              DonationRecordMapper donationRecordMapper,
                              DonationChannelMapper donationChannelMapper,
                              CommunityMapper communityMapper,
                              CatPhotoMapper catPhotoMapper,
                              PasswordHasher passwordHasher,
                              TokenService tokenService) {
        this.codeGenerator = codeGenerator;
        this.userMapper = userMapper;
        this.catMapper = catMapper;
        this.rescueReportMapper = rescueReportMapper;
        this.medicalRecordMapper = medicalRecordMapper;
        this.applicationMapper = applicationMapper;
        this.adoptionAuditMapper = adoptionAuditMapper;
        this.followupMapper = followupMapper;
        this.agreementMapper = agreementMapper;
        this.followupTaskMapper = followupTaskMapper;
        this.warningRecordMapper = warningRecordMapper;
        this.systemMessageMapper = systemMessageMapper;
        this.dashboardMapper = dashboardMapper;
        this.noticeMapper = noticeMapper;
        this.auditLogMapper = auditLogMapper;
        this.operationLogMapper = operationLogMapper;
        this.favoriteMapper = favoriteMapper;
        this.productMapper = productMapper;
        this.productOrderMapper = productOrderMapper;
        this.donationRecordMapper = donationRecordMapper;
        this.donationChannelMapper = donationChannelMapper;
        this.communityMapper = communityMapper;
        this.catPhotoMapper = catPhotoMapper;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
    }

    public List<User> listUsers() {
        return userMapper.findAll();
    }

    public List<User> listAdminUsers(Role role, Boolean enabled, String keyword) {
        return userMapper.findAdminUsers(role, enabled, blankToNull(keyword));
    }

    @Transactional
    public User createAdminUser(AdminUserRequest request) {
        validateAdminUserRequest(request, true);
        if (userMapper.countBySchoolNo(request.schoolNo()) > 0) {
            throw new BusinessException("学工号已注册");
        }
        String userIdPrefix = "U" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDateTime now = LocalDateTime.now();
        User user = new User(codeGenerator.nextUserIdAfter(userMapper.maxSuffixByPrefix(userIdPrefix)),
                request.userName().trim(), request.schoolNo().trim(), request.phone(), request.idCard(),
                request.college().trim(), request.petExperience(), request.role(), request.enabled() == null || request.enabled(), now);
        userMapper.insert(user);
        userMapper.updatePassword(user.userId(), passwordHasher.hash(request.password()));
        logOperation(currentUser(), "新增后台用户", "USER", user.userId(), null, user.role().name(), user.schoolNo());
        return findUser(user.userId());
    }

    @Transactional
    public User updateAdminUser(String userId, AdminUserRequest request) {
        validateAdminUserRequest(request, false);
        User old = findUser(userId);
        if (userMapper.countBySchoolNoExcept(request.schoolNo(), userId) > 0) {
            throw new BusinessException("学工号已被其他用户使用");
        }
        int updated = userMapper.updateAdminUser(userId, request.userName().trim(), request.schoolNo().trim(), request.phone(),
                request.idCard(), request.college().trim(), request.petExperience());
        if (updated == 0) {
            throw new BusinessException("用户更新失败");
        }
        logOperation(currentUser(), "编辑后台用户", "USER", userId, old.userName(), request.userName(), request.college());
        return findUser(userId);
    }

    @Transactional
    public User updateAdminUserRole(String userId, Role role) {
        User old = findUser(userId);
        if (role == null) {
            throw new BusinessException("用户角色不能为空");
        }
        int updated = userMapper.updateRole(userId, role);
        if (updated == 0) {
            throw new BusinessException("用户角色更新失败");
        }
        logOperation(currentUser(), "修改用户角色", "USER", userId, old.role().name(), role.name(), old.schoolNo());
        return findUser(userId);
    }

    @Transactional
    public User updateAdminUserStatus(String userId, boolean enabled) {
        User old = findUser(userId);
        if (userId.equals(AuthContext.userId()) && !enabled) {
            throw new BusinessException("管理员不能禁用自己");
        }
        int updated = userMapper.updateEnabled(userId, enabled);
        if (updated == 0) {
            throw new BusinessException("用户状态更新失败");
        }
        logOperation(currentUser(), enabled ? "启用用户" : "禁用用户", "USER", userId,
                String.valueOf(old.enabled()), String.valueOf(enabled), old.schoolNo());
        return findUser(userId);
    }

    @Transactional
    public User register(RegisterRequest request) {
        validateRegisterRequest(request);
        if (userMapper.countBySchoolNo(request.schoolNo()) > 0) {
            throw new BusinessException("学工号已注册");
        }
        Role registerRole = Role.STUDENT;
        String userIdPrefix = "U" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        User user = new User(codeGenerator.nextUserIdAfter(userMapper.maxSuffixByPrefix(userIdPrefix)),
                request.userName(), request.schoolNo(), request.phone(),
                request.idCard(), request.college(), request.petExperience(), registerRole, true, LocalDateTime.now());
        userMapper.insert(user);
        userMapper.updatePassword(user.userId(), passwordHasher.hash(request.password()));
        log(request.userName(), "注册用户", "USER", user.userId(), "实名注册并分配角色：" + registerRole.getLabel());
        return user;
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        User user = userMapper.findByAccount(request.account());
        if (user == null || !user.enabled()) {
            throw new BusinessException("账号或密码错误");
        }
        String passwordHash = userMapper.findPasswordHash(user.userId());
        if (!passwordHasher.matches(request.password(), passwordHash)) {
            throw new BusinessException("账号或密码错误");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            userMapper.updatePassword(user.userId(), passwordHasher.hash(request.password()));
        }
        int tokenVersion = userMapper.findTokenVersion(user.userId());
        String token = tokenService.issue(new AuthPrincipal(user.userId(), user.userName(), user.role(), tokenVersion));
        log(user.userName(), "用户登录", "USER", user.userId(), "账号登录成功");
        return new LoginResult(token, user);
    }

    public User currentUser() {
        return findUser(AuthContext.userId());
    }

    @Transactional
    public User updateProfile(ProfileUpdateRequest request) {
        validateProfileRequest(request);
        String userId = AuthContext.userId();
        userMapper.updateProfile(userId, request.userName(), request.phone(), request.college(), request.petExperience());
        log(userId, "更新个人资料", "USER", userId, request.college());
        return findUser(userId);
    }

    @Transactional
    public void updatePassword(PasswordUpdateRequest request) {
        String userId = AuthContext.userId();
        String passwordHash = userMapper.findPasswordHash(userId);
        if (!passwordHasher.matches(request.oldPassword(), passwordHash)) {
            throw new BusinessException("原密码不正确");
        }
        userMapper.updatePassword(userId, passwordHasher.hash(request.newPassword()));
        log(userId, "修改密码", "USER", userId, "密码已更新，旧登录状态失效");
    }

    @Transactional
    public void resetPassword(ForgotPasswordRequest request) {
        // 忘记密码流程不依赖登录态，必须同时校验账号、手机号和身份证号，避免只凭账号重置密码。
        User user = userMapper.findByAccount(request.account());
        if (user == null || !user.enabled()) {
            throw new BusinessException("账号信息不匹配");
        }
        if (!valueOrDefault(user.phone(), "").equals(request.phone().trim())
                || !valueOrDefault(user.idCard(), "").equalsIgnoreCase(request.idCard().trim())) {
            throw new BusinessException("账号、手机号或身份证号不匹配");
        }
        if (request.newPassword() == null || request.newPassword().length() < 6) {
            throw new BusinessException("新密码长度至少 6 位");
        }
        userMapper.updatePassword(user.userId(), passwordHasher.hash(request.newPassword()));
        log(user.userName(), "找回密码", "USER", user.userId(), "用户通过身份信息校验后重置密码");
    }

    @Transactional
    public void logout() {
        String userId = AuthContext.userId();
        userMapper.incrementTokenVersion(userId);
        log(userId, "用户退出", "USER", userId, "主动退出登录");
    }

    public List<Cat> listCats(CatStatus status, String keyword) {
        return catMapper.findAll(status, keyword);
    }

    public List<Cat> listPublicCats(String keyword, String gender, HealthLevel healthLevel,
                                    Boolean sterilized, Boolean vaccinated, String tag) {
        return catMapper.findPublic(keyword, gender, healthLevel, sterilized, vaccinated, tag);
    }

    public Cat getPublicCat(String catId) {
        Cat cat = findCat(catId);
        if (cat.status() != CatStatus.ADOPTABLE) {
            throw new BusinessException("当前猫咪暂未发布认养");
        }
        return cat;
    }

    public List<Cat> listAdminCats(CatStatus status, HealthLevel healthLevel, String gender,
                                   String keyword, Integer page, Integer size) {
        int safeSize = size == null || size <= 0 || size > 100 ? 50 : size;
        int safePage = page == null || page <= 0 ? 1 : page;
        return catMapper.findAdmin(status, healthLevel, gender, keyword, safeSize, (safePage - 1) * safeSize);
    }

    public Cat getCat(String catId) {
        return findCat(catId);
    }

    public List<CatPhoto> listCatPhotos(String catId) {
        findCat(catId);
        return catPhotoMapper.findByCatId(catId);
    }

    public List<CatPhoto> listAllCatPhotos() {
        return catPhotoMapper.findAll();
    }

    @Transactional
    public CatPhoto addCatPhoto(CatPhotoRequest request) {
        findCat(request.catId());
        CatPhoto photo = new CatPhoto(codeGenerator.next("PH"), request.catId(), request.photoUrl(),
                request.angleCode(), request.photoScene(), request.cover(), request.recognitionWeight(),
                null, request.featureNote(), LocalDateTime.now());
        catPhotoMapper.insert(photo);
        if (request.cover()) {
            Cat cat = findCat(request.catId());
            Cat updated = new Cat(cat.catId(), cat.catName(), cat.foundPlace(), cat.foundDate(), cat.gender(),
                    cat.color(), cat.ageEstimate(), cat.personality(), cat.healthLevel(), cat.sterilized(),
                    cat.vaccinated(), cat.status(), request.photoUrl(), cat.tags(), cat.description(),
                    cat.createdAt(), LocalDateTime.now());
            catMapper.update(updated);
        }
        log("志愿者", "新增猫咪识别照片", "CAT_PHOTO", photo.photoId(), request.catId() + " " + request.angleCode());
        return photo;
    }

    @Transactional
    public void deleteCatPhoto(String photoId) {
        int deleted = catPhotoMapper.delete(photoId);
        if (deleted == 0) {
            throw new BusinessException("猫咪照片不存在");
        }
        log("管理员", "删除猫咪识别照片", "CAT_PHOTO", photoId, "照片样本维护");
    }

    @Transactional
    public Cat saveCat(CatRequest request) {
        validateCatRequest(request, true);
        LocalDateTime now = LocalDateTime.now();
        Cat cat = buildCat(nextCatId(), forceNewCatObserving(request), now, now);
        catMapper.insert(cat);
        replaceCatTags(cat.catId(), cat.tags());
        User operator = currentUser();
        logOperation(operator, "新增猫咪档案", "CAT", cat.catId(), null, cat.status().name(), cat.foundPlace());
        return cat;
    }

    @Transactional
    public Cat updateCat(String catId, CatRequest request) {
        validateCatRequest(request, false);
        Cat old = findCat(catId);
        if (old.status() == CatStatus.ADOPTED || old.status() == CatStatus.FOLLOWING) {
            throw new BusinessException("已认养或回访中的猫咪不能直接编辑关键档案");
        }
        Cat updated = buildCat(catId, request, old.createdAt(), LocalDateTime.now());
        catMapper.update(updated);
        replaceCatTags(catId, updated.tags());
        logOperation(currentUser(), "更新猫咪档案", "CAT", catId, old.status().name(), updated.status().name(), updated.catName());
        return findCat(catId);
    }

    @Transactional
    public void deleteCat(String catId) {
        Cat cat = findCat(catId);
        if (applicationMapper.countByCatId(catId) > 0) {
            throw new BusinessException("该猫咪已有认养申请记录，不能删除，可改为失联或观察中");
        }
        int deleted = catMapper.logicalDelete(catId, LocalDateTime.now());
        if (deleted == 0) {
            throw new BusinessException("猫咪档案删除失败");
        }
        logOperation(currentUser(), "逻辑删除猫咪档案", "CAT", catId, cat.status().name(), "DELETED", cat.catName());
    }

    @Transactional
    public Cat updateCatStatus(String catId, CatStatus status) {
        return changeCatStatus(catId, new CatStatusChangeRequest(status, "旧接口状态维护"));
    }

    @Transactional
    public Cat changeCatStatus(String catId, CatStatusChangeRequest request) {
        Cat cat = findCat(catId);
        validateCatStatusTransition(cat, request.targetStatus());
        if (request.targetStatus() == CatStatus.ADOPTABLE) {
            validatePublishable(cat);
        }
        LocalDateTime now = LocalDateTime.now();
        catMapper.updateStatus(catId, request.targetStatus(), now);
        User operator = currentUser();
        logOperation(operator, request.targetStatus() == CatStatus.ADOPTABLE ? "发布猫咪认养" : "更新猫咪状态",
                "CAT", catId, cat.status().name(), request.targetStatus().name(), request.reason());
        if (request.targetStatus() == CatStatus.MEDICAL && cat.status() != CatStatus.MEDICAL) {
            sendMessageToRole(Role.HOSPITAL, "新的医疗协作任务",
                    "猫咪 " + valueOrDefault(cat.catName(), catId) + " 已进入医疗中状态，请合作医院在医疗工作台处理。",
                    "CAT", catId, operator, operator.userId());
        }
        return findCat(catId);
    }

    @Transactional
    public RescueReport submitReport(RescueReportRequest request) {
        RescueReport report = new RescueReport(codeGenerator.next("RP"), request.reporterName(), request.reporterPhone(),
                request.foundPlace(), request.color(), request.gender(), request.healthDescription(), request.urgent(),
                request.photoUrl(), request.urgent() ? "待紧急核实" : "待核实", LocalDateTime.now());
        rescueReportMapper.insert(report);
        log(request.reporterName(), "提交发现上报", "REPORT", report.reportId(), report.foundPlace());
        return report;
    }

    public List<RescueReport> listReports() {
        return rescueReportMapper.findAll();
    }

    @Transactional
    public Clue submitClue(ClueSubmitRequest request) {
        validateClueRequest(request);
        User user = findUser(AuthContext.userId());
        if (user.role() != Role.STUDENT) {
            throw new BusinessException("只有普通用户可以提交猫咪线索");
        }
        LocalDateTime now = LocalDateTime.now();
        String clueIdPrefix = "RP" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String clueId = codeGenerator.nextAfter("RP", rescueReportMapper.maxSuffixByPrefix(clueIdPrefix));
        String urgency = normalizeUrgency(request.urgencyLevel());
        rescueReportMapper.insertClue(clueId, user.userId(), user.userName(), user.phone(),
                request.foundLocation(), request.foundArea(), request.foundTime() == null ? now : request.foundTime(),
                request.description(), "URGENT".equals(urgency), urgency, request.photoUrl(),
                ClueStatus.PENDING_VERIFY, now);
        logOperation(user, "提交猫咪线索", "CLUE", clueId, null, ClueStatus.PENDING_VERIFY.name(), request.foundLocation());
        sendMessage(user.userId(), "线索提交成功", "你的猫咪线索已提交，志愿者将尽快核实。", "CLUE", clueId, user);
        sendMessageToRole(Role.VOLUNTEER, "新的线索待审核",
                "有一条新的猫咪线索需要核实，地点：" + request.foundLocation() + "。请在前台“线索审核”中处理。",
                "CLUE", clueId, user, null);
        return findClue(clueId);
    }

    public List<Clue> listMyClues(String status) {
        return rescueReportMapper.findClues(AuthContext.userId(), normalizeClueStatusFilter(status), null, null, null, null);
    }

    public List<Clue> listAdminClues(String status, String urgencyLevel, String keyword, Integer page, Integer size) {
        int safeSize = size == null || size <= 0 || size > 100 ? 50 : size;
        int safePage = page == null || page <= 0 ? 1 : page;
        return rescueReportMapper.findClues(null, normalizeClueStatusFilter(status), normalizeUrgencyFilter(urgencyLevel),
                keyword, safeSize, (safePage - 1) * safeSize);
    }

    public Clue getAdminClue(String clueId) {
        return findClue(clueId);
    }

    @Transactional
    public Clue markClueInvalid(String clueId, ActionReasonRequest request) {
        String reason = DataQualityValidator.requireCleanText("无效原因", request.reason(), 2, 300);
        Clue clue = findClue(clueId);
        if (ClueStatus.CREATED_CAT.name().equals(clue.status()) || clue.createdCatId() != null) {
            throw new BusinessException("已建档线索不能标记无效");
        }
        if (ClueStatus.INVALID.name().equals(clue.status())) {
            throw new BusinessException("线索已是无效状态");
        }
        User operator = currentUser();
        LocalDateTime now = LocalDateTime.now();
        int updated = rescueReportMapper.markInvalid(clueId, reason, operator.userId(), now);
        if (updated == 0) {
            throw new BusinessException("线索状态已变化，标记无效失败");
        }
        logOperation(operator, "标记线索无效", "CLUE", clueId, clue.status(), ClueStatus.INVALID.name(), reason);
        if (clue.reporterId() != null) {
            sendMessage(clue.reporterId(), "线索已标记无效", "你的线索已被标记为无效，原因：" + reason, "CLUE", clueId, operator);
        }
        return findClue(clueId);
    }

    @Transactional
    public void deleteAdminClue(String clueId, ActionReasonRequest request) {
        String reason = DataQualityValidator.requireCleanText("删除原因", request.reason(), 2, 300);
        Clue clue = findClue(clueId);
        if (ClueStatus.CREATED_CAT.name().equals(clue.status()) || clue.createdCatId() != null) {
            throw new BusinessException("已建档线索不能逻辑删除");
        }
        User operator = currentUser();
        LocalDateTime now = LocalDateTime.now();
        int updated = rescueReportMapper.logicalDeleteClue(clueId, reason, operator.userId(), now);
        if (updated == 0) {
            throw new BusinessException("线索状态已变化，逻辑删除失败");
        }
        logOperation(operator, "逻辑删除线索", "CLUE", clueId, clue.status(), "DELETED", reason);
        if (clue.reporterId() != null) {
            sendMessage(clue.reporterId(), "线索已归档处理", "你的线索已由管理员归档处理，原因：" + reason, "CLUE", clueId, operator);
        }
    }

    @Transactional
    public Clue verifyClue(String clueId, ClueVerifyRequest request) {
        Clue clue = findClue(clueId);
        ensureCanVerifyClue(clue);
        User operator = findUser(AuthContext.userId());
        ClueStatus nextStatus = switch (request.verifyResult()) {
            case VALID -> ClueStatus.VERIFIED_VALID;
            case DUPLICATE -> ClueStatus.DUPLICATE;
            case INVALID -> ClueStatus.INVALID;
        };
        LocalDateTime now = LocalDateTime.now();
        rescueReportMapper.updateClueVerification(clueId, nextStatus, operator.userId(),
                request.verifyResult().name(), request.verifyComment(), now);
        logOperation(operator, "核实猫咪线索", "CLUE", clueId, clue.status(), nextStatus.name(), request.verifyComment());
        if (clue.reporterId() != null) {
            sendMessage(clue.reporterId(), "线索核实结果", "你的线索核实结果为：" + nextStatus.name(), "CLUE", clueId, operator);
        }
        sendMessage(operator.userId(), "感谢完成线索审核",
                "你已完成线索 " + clue.clueNo() + " 的核实处理，系统已记录结果。",
                "CLUE", clueId, operator);
        return findClue(clueId);
    }

    @Transactional
    public Cat createCatFromClue(String clueId, CreateCatFromClueRequest request) {
        Clue clue = findClue(clueId);
        ensureCanCreateCatFromClue(clue);
        User operator = findUser(AuthContext.userId());
        LocalDateTime now = LocalDateTime.now();
        String catId = nextCatId();
        String foundPlace = valueOrDefault(clue.foundLocation(), "待补充");
        String description = valueOrDefault(request.extraDescription(), clue.description());
        String personality = valueOrDefault(request.personality(), "待观察");
        HealthLevel healthLevel = request.healthStatus() == null ? HealthLevel.B : request.healthStatus();
        Cat cat = new Cat(catId, cleanCatName(request.name(), "待命名"), foundPlace,
                clue.foundTime() == null ? LocalDate.now() : clue.foundTime().toLocalDate(),
                valueOrDefault(request.gender(), "U"), valueOrDefault(request.coatColor(), "待观察"),
                valueOrDefault(request.ageEstimate(), "待估计"), personality, healthLevel,
                Boolean.TRUE.equals(request.sterilizedStatus()), Boolean.TRUE.equals(request.vaccineStatus()),
                CatStatus.OBSERVING, clue.photoUrl(), List.of("线索建档", clue.clueNo(), valueOrDefault(clue.foundArea(), "校园")),
                description, now, now);
        catMapper.insert(cat);
        replaceCatTags(cat.catId(), cat.tags());
        rescueReportMapper.updateCreatedCat(clueId, catId, operator.userId(), now);
        logOperation(operator, "线索一键生成猫咪档案", "CLUE", clueId, clue.status(), ClueStatus.CREATED_CAT.name(), catId);
        logOperation(operator, "新增猫咪档案", "CAT", catId, null, CatStatus.OBSERVING.name(), "来源线索：" + clueId);
        if (clue.reporterId() != null) {
            sendMessage(clue.reporterId(), "线索已生成猫咪档案", "你的线索已生成猫咪档案：" + cat.catName(), "CAT", catId, operator);
        }
        sendMessage(operator.userId(), "感谢完成线索建档",
                "你已将线索 " + clue.clueNo() + " 转为猫咪档案，系统已记录建档结果。",
                "CAT", catId, operator);
        if (healthLevel == HealthLevel.C) {
            sendMessageToRole(Role.HOSPITAL, "新的医疗协作任务",
                    "猫咪 " + cat.catName() + " 建档时标记为需治疗，请合作医院在医疗工作台处理。",
                    "CAT", catId, operator, null);
        }
        return cat;
    }

    @Transactional
    public Cat reviewReport(String reportId, ReportReviewRequest request) {
        RescueReport report = rescueReportMapper.findById(reportId);
        if (report == null) {
            throw new BusinessException("上报记录不存在");
        }
        if (!request.approved()) {
            rescueReportMapper.updateStatus(reportId, "核实不通过");
            log(request.operatorName(), "驳回发现上报", "REPORT", reportId, report.foundPlace());
            return null;
        }
        CatRequest catRequest = request.cat() == null
                ? new CatRequest("待命名", report.foundPlace(), LocalDate.now(), valueOrDefault(report.gender(), "U"),
                report.color(), "待估计", report.healthDescription(), HealthLevel.B, false, false,
                CatStatus.OBSERVING, report.photoUrl(), List.of("上报建档", report.urgent() ? "紧急核实" : "待观察"),
                "由发现上报核实后建立档案：" + report.healthDescription())
                : request.cat();
        Cat cat = saveCat(catRequest);
        rescueReportMapper.updateStatus(reportId, "已核实建档：" + cat.catId());
        log(request.operatorName(), "核实上报并建档", "REPORT", reportId, cat.catId());
        return cat;
    }

    @Transactional
    public MedicalRecord addMedicalRecord(MedicalRecordRequest request) {
        Cat cat = findCat(request.catId());
        MedicalRecord record = new MedicalRecord(codeGenerator.next("MD"), request.catId(),
                request.checkDate() == null ? LocalDate.now() : request.checkDate(), request.hospital(),
                request.healthLevel(), request.vaccinated(), request.sterilized(), request.treatment(),
                request.doctorNote(), null, LocalDateTime.now());
        medicalRecordMapper.insert(record);

        CatStatus nextStatus = request.healthLevel() == HealthLevel.C ? CatStatus.MEDICAL : cat.status();
        catMapper.updateHealth(cat.catId(), request.healthLevel(), request.sterilized(), request.vaccinated(), nextStatus, LocalDateTime.now());
        log(request.hospital(), "新增医疗记录", "CAT", cat.catId(), request.doctorNote());
        return record;
    }

    public List<MedicalRecord> listMedicalRecords(String catId) {
        return medicalRecordMapper.findAll(catId);
    }

    public HospitalDashboardSummary hospitalDashboardSummary() {
        String hospitalUserId = AuthContext.role() == Role.HOSPITAL ? AuthContext.userId() : null;
        return new HospitalDashboardSummary(
                catMapper.countActiveByStatus(CatStatus.MEDICAL),
                catMapper.countActiveByStatus(CatStatus.OBSERVING),
                medicalRecordMapper.countAbnormalActive(),
                hospitalUserId == null ? medicalRecordMapper.countAllActive() : medicalRecordMapper.countByHospitalUserId(hospitalUserId),
                medicalRecordMapper.countAllActive(),
                catMapper.countPendingVaccine(),
                catMapper.countPendingSterilization()
        );
    }

    public List<MedicalRecord> listHospitalMedicalRecords(Integer limit) {
        int safeLimit = limit == null || limit <= 0 || limit > 100 ? 20 : limit;
        String hospitalUserId = AuthContext.role() == Role.HOSPITAL ? AuthContext.userId() : null;
        return medicalRecordMapper.findHospitalRecords(hospitalUserId, safeLimit);
    }

    public MedicalRecord getMedicalRecord(String medicalId) {
        MedicalRecord record = medicalRecordMapper.findById(medicalId);
        if (record == null) {
            throw new BusinessException("医疗记录不存在");
        }
        return record;
    }

    @Transactional
    public MedicalRecord addAdminMedicalRecord(String catId, AdminMedicalRecordRequest request) {
        validateAdminMedicalRequest(request);
        Cat cat = findCat(catId);
        User operator = currentUser();
        LocalDateTime now = LocalDateTime.now();
        HealthLevel healthLevel = mapHealthResult(request.healthResult());
        boolean vaccinated = mapVaccineStatus(request.vaccineStatus(), cat.vaccinated());
        boolean sterilized = mapSterilizedStatus(request.sterilizedStatus(), cat.sterilized());
        String medicalIdPrefix = "MD" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String medicalId = codeGenerator.nextAfter("MD", medicalRecordMapper.maxSuffixByPrefix(medicalIdPrefix));
        String note = request.healthResult() + (request.abnormalFlag() ? "；异常记录" : "");
        medicalRecordMapper.insertAdminRecord(medicalId, catId, request.recordDate() == null ? LocalDate.now() : request.recordDate(),
                operator.college() == null || operator.college().isBlank() ? "校内合作医院" : operator.college(),
                healthLevel, vaccinated, sterilized, request.description(), note, normalizeRecordType(request.recordType()),
                operator.userId(), request.cost(), request.attachmentUrl(), request.abnormalFlag(), now);
        CatStatus nextStatus = (healthLevel == HealthLevel.C || request.abnormalFlag()) ? CatStatus.MEDICAL : cat.status();
        catMapper.updateHealth(catId, healthLevel, sterilized, vaccinated, nextStatus, now);
        logOperation(operator, "新增医疗记录", "CAT", catId, cat.status().name(), nextStatus.name(),
                normalizeRecordType(request.recordType()) + "；" + request.description());
        if (operator.role() == Role.HOSPITAL) {
            sendMessage(operator.userId(), "感谢完成医疗记录",
                    "你已完成猫咪 " + valueOrDefault(cat.catName(), catId) + " 的医疗记录录入，系统已同步健康状态。",
                    "MEDICAL", medicalId, operator);
        }
        return medicalRecordMapper.findAll(catId).stream()
                .filter(record -> record.medicalId().equals(medicalId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("医疗记录保存后查询失败"));
    }

    @Transactional
    public MedicalRecord updateAdminMedicalRecord(String medicalId, AdminMedicalRecordRequest request) {
        validateAdminMedicalRequest(request);
        MedicalRecord old = getMedicalRecord(medicalId);
        Cat cat = findCat(old.catId());
        User operator = currentUser();
        LocalDateTime now = LocalDateTime.now();
        HealthLevel healthLevel = mapHealthResult(request.healthResult());
        boolean vaccinated = mapVaccineStatus(request.vaccineStatus(), cat.vaccinated());
        boolean sterilized = mapSterilizedStatus(request.sterilizedStatus(), cat.sterilized());
        String note = request.healthResult() + (request.abnormalFlag() ? "；异常记录" : "");
        int updated = medicalRecordMapper.updateAdminRecord(medicalId,
                request.recordDate() == null ? LocalDate.now() : request.recordDate(),
                operator.college() == null || operator.college().isBlank() ? "校内合作医院" : operator.college(),
                healthLevel, vaccinated, sterilized, request.description(), note, normalizeRecordType(request.recordType()),
                operator.userId(), request.cost(), request.attachmentUrl(), request.abnormalFlag(), now);
        if (updated == 0) {
            throw new BusinessException("医疗记录更新失败");
        }
        CatStatus nextStatus = (healthLevel == HealthLevel.C || request.abnormalFlag()) ? CatStatus.MEDICAL : cat.status();
        catMapper.updateHealth(old.catId(), healthLevel, sterilized, vaccinated, nextStatus, now);
        logOperation(operator, "编辑医疗记录", "MEDICAL", medicalId, old.healthLevel().name(), healthLevel.name(), request.description());
        if (operator.role() == Role.HOSPITAL) {
            sendMessage(operator.userId(), "感谢更新医疗记录",
                    "你已更新猫咪 " + valueOrDefault(cat.catName(), old.catId()) + " 的医疗记录，系统已同步健康状态。",
                    "MEDICAL", medicalId, operator);
        }
        return getMedicalRecord(medicalId);
    }

    @Transactional
    public void voidAdminMedicalRecord(String medicalId, ActionReasonRequest request) {
        String reason = DataQualityValidator.requireCleanText("作废原因", request.reason(), 2, 300);
        MedicalRecord old = getMedicalRecord(medicalId);
        User operator = currentUser();
        int updated = medicalRecordMapper.voidRecord(medicalId, reason, operator.userId(), LocalDateTime.now());
        if (updated == 0) {
            throw new BusinessException("医疗记录作废失败");
        }
        logOperation(operator, "作废医疗记录", "MEDICAL", medicalId, old.healthLevel().name(), "VOID", reason);
    }

    public List<CatTimelineEvent> catTimeline(String catId) {
        Cat cat = findCat(catId);
        List<CatTimelineEvent> events = new ArrayList<>();
        Clue clue = rescueReportMapper.findClueByCreatedCatId(catId);
        if (clue != null) {
            events.add(new CatTimelineEvent("CLUE", "线索上报", clue.description(), clue.createTime()));
            events.add(new CatTimelineEvent("VERIFY", "核实建档", valueOrDefault(clue.verifyComment(), "线索已生成猫咪档案"), clue.verifyTime()));
        }
        events.add(new CatTimelineEvent("CAT", "猫咪档案创建", cat.catName(), cat.createdAt()));
        for (MedicalRecord record : medicalRecordMapper.findAll(catId)) {
            events.add(new CatTimelineEvent("MEDICAL", "医疗记录：" + record.healthLevel().getLabel(),
                    valueOrDefault(record.treatment(), record.doctorNote()), record.createdAt()));
        }
        events.add(new CatTimelineEvent("STATUS", "当前状态：" + cat.status().getLabel(), cat.description(), cat.updatedAt()));
        events.sort(Comparator.comparing(CatTimelineEvent::eventTime, Comparator.nullsLast(Comparator.naturalOrder())));
        return events;
    }

    @Transactional
    public ApplicationReviewDetail submitAdoptionApplication(AdoptionApplicationRequest request) {
        // 认养申请入口：校验申请人身份、猫咪状态和重复申请后，写入申请主表并同步评分原因。
        validateAdoptionApplicationRequest(request);
        User user = currentUser();
        if (user.role() != Role.STUDENT) {
            throw new BusinessException("只有普通用户可以提交认养申请");
        }
        Cat cat = findCat(request.catId());
        if (cat.status() != CatStatus.ADOPTABLE) {
            throw new BusinessException("猫咪当前不可认养");
        }
        if (applicationMapper.countEffective(user.userId(), request.catId()) > 0) {
            throw new BusinessException("你已提交过该猫咪的有效申请，请到我的申请查看进度");
        }
        ApplicationScore score = scoreApplication(request);
        LocalDateTime now = LocalDateTime.now();
        String applicationPrefix = "APP" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String applicationId = codeGenerator.nextAfter("APP", applicationMapper.maxSuffixByPrefix(applicationPrefix));
        applicationMapper.insertMis(applicationId, user.userId(), request.catId(), request.livingCondition(),
                request.petExperience(), request.familySupport(), request.costAffordability(),
                request.acceptFollowup(), request.commitmentText(), request.extraReason(),
                score.score(), score.riskLevel(), score.reasons(), ApplicationStatus.PENDING_INITIAL, now);
        saveApplicationScoreReasons(applicationId, score.reasons());
        logOperation(user, "提交认养申请", "APPLICATION", applicationId, null, ApplicationStatus.PENDING_INITIAL.name(),
                "score=" + score.score() + "; risk=" + score.riskLevel());
        sendMessage(user.userId(), "认养申请提交成功", "你的认养申请已提交，当前状态为待初审。", "APPLICATION", applicationId, user);
        sendMessageToRole(Role.VOLUNTEER, "新的认养初审任务",
                "申请 " + applicationId + " 正在等待志愿者初审，请在“认养初审”中处理。",
                "APPLICATION", applicationId, user, null);
        return getApplicationReviewDetail(applicationId, true);
    }

    public List<ApplicationReviewDetail> listMyAdoptionApplications(ApplicationStatus status) {
        return applicationMapper.findReviewRows(AuthContext.userId(), status, null, null, null, null).stream()
                .map(row -> toApplicationReviewDetail(row, false))
                .toList();
    }

    public ApplicationReviewDetail getMyAdoptionApplication(String applicationId) {
        return getMyApplicationReviewDetail(applicationId, true);
    }

    @Transactional
    public ApplicationReviewDetail cancelMyAdoptionApplication(String applicationId, ApplicationCancelRequest request) {
        ApplicationReviewRow row = requireApplicationRow(applicationId);
        if (!row.userId().equals(AuthContext.userId())) {
            throw new BusinessException("只能取消自己的申请");
        }
        if (row.status() != ApplicationStatus.PENDING_INITIAL) {
            throw new BusinessException("只有待初审申请可以取消");
        }
        User operator = currentUser();
        LocalDateTime now = LocalDateTime.now();
        applicationMapper.cancelMis(applicationId, request.cancelReason(), operator.userId(), now);
        logOperation(operator, "取消认养申请", "APPLICATION", applicationId, row.status().name(),
                ApplicationStatus.CANCELLED.name(), request.cancelReason());
        return getApplicationReviewDetail(applicationId, true);
    }

    public List<ApplicationReviewDetail> listAdminAdoptionApplications(ApplicationStatus status, String riskLevel,
                                                                      String keyword, Integer page, Integer size) {
        // 后台列表统一做分页兜底；志愿者默认只看待初审任务，管理员可按状态筛选全量申请。
        int safeSize = size == null || size <= 0 || size > 100 ? 50 : size;
        int safePage = page == null || page <= 0 ? 1 : page;
        ApplicationStatus effectiveStatus = status;
        if (AuthContext.role() == Role.VOLUNTEER && effectiveStatus == null) {
            effectiveStatus = ApplicationStatus.PENDING_INITIAL;
        }
        return applicationMapper.findReviewRows(null, effectiveStatus, riskLevel, keyword, safeSize, (safePage - 1) * safeSize)
                .stream()
                .map(row -> toApplicationReviewDetail(row, false))
                .toList();
    }

    public ApplicationReviewDetail getAdminAdoptionApplication(String applicationId) {
        return getApplicationReviewDetail(applicationId, true);
    }

    @Transactional
    public ApplicationReviewDetail initialAudit(String applicationId, ApplicationAuditRequest request) {
        // 初审只允许志愿者处理，通过后进入终审，驳回则停留在初审驳回状态。
        if (AuthContext.role() != Role.VOLUNTEER) {
            throw new BusinessException("只有志愿者可以进行初审");
        }
        ApplicationReviewRow row = requireApplicationRow(applicationId);
        if (row.status() != ApplicationStatus.PENDING_INITIAL) {
            throw new BusinessException("当前申请状态不能初审");
        }
        ApplicationStatus next = "APPROVED".equals(request.auditResult())
                ? ApplicationStatus.PENDING_FINAL : ApplicationStatus.INITIAL_REJECTED;
        return auditApplication(row, "INITIAL", request.auditResult(), request.auditComment(), next);
    }

    @Transactional
    public ApplicationReviewDetail finalAudit(String applicationId, ApplicationAuditRequest request) {
        // 终审只允许管理员处理；终审通过后联动猫咪状态，避免继续被其他用户申请。
        if (AuthContext.role() != Role.ADMIN) {
            throw new BusinessException("只有管理员可以进行终审");
        }
        ApplicationReviewRow row = requireApplicationRow(applicationId);
        if (row.status() != ApplicationStatus.PENDING_FINAL) {
            throw new BusinessException("当前申请状态不能终审");
        }
        ApplicationStatus next = "APPROVED".equals(request.auditResult())
                ? ApplicationStatus.PENDING_HANDOVER : ApplicationStatus.FINAL_REJECTED;
        ApplicationReviewDetail detail = auditApplication(row, "FINAL", request.auditResult(), request.auditComment(), next);
        if (next == ApplicationStatus.PENDING_HANDOVER) {
            catMapper.updateStatus(row.catId(), CatStatus.APPLYING, LocalDateTime.now());
            logOperation(currentUser(), "终审通过联动猫咪申请中", "CAT", row.catId(), CatStatus.ADOPTABLE.name(),
                    CatStatus.APPLYING.name(), applicationId);
        }
        return detail;
    }

    @Transactional
    public ApplicationReviewDetail voidAdminAdoptionApplication(String applicationId, ActionReasonRequest request) {
        if (AuthContext.role() != Role.ADMIN) {
            throw new BusinessException("只有管理员可以作废申请");
        }
        String reason = DataQualityValidator.requireCleanText("作废原因", request.reason(), 2, 300);
        ApplicationReviewRow row = requireApplicationRow(applicationId);
        if (row.status() == ApplicationStatus.HANDED_OVER) {
            throw new BusinessException("已交接申请不能作废");
        }
        AgreementInfo agreement = agreementMapper.findByApplicationId(applicationId);
        if (agreement != null && "HANDED_OVER".equals(agreement.status())) {
            throw new BusinessException("已完成协议交接的申请不能作废");
        }
        User operator = currentUser();
        int updated = applicationMapper.voidApplication(applicationId, reason, operator.userId(), LocalDateTime.now());
        if (updated == 0) {
            throw new BusinessException("申请状态已变化，作废失败");
        }
        logOperation(operator, "作废认养申请", "APPLICATION", applicationId, row.status().name(), ApplicationStatus.CANCELLED.name(), reason);
        sendMessage(row.userId(), "认养申请已作废", "你的认养申请已由管理员作废，原因：" + reason, "APPLICATION", applicationId, operator);
        return getApplicationReviewDetail(applicationId, true);
    }

    public List<AgreementInfo> listPendingAgreements(String status, String keyword) {
        return agreementMapper.findPending(blankToNull(status), blankToNull(keyword));
    }

    @Transactional
    public AgreementInfo generateAgreement(String applicationId) {
        if (AuthContext.role() != Role.ADMIN) {
            throw new BusinessException("Only admins can generate agreements");
        }
        ApplicationReviewRow row = requireApplicationRow(applicationId);
        if (row.status() != ApplicationStatus.PENDING_HANDOVER) {
            throw new BusinessException("Only PENDING_HANDOVER applications can generate agreements");
        }
        AgreementInfo existing = agreementMapper.findByApplicationId(applicationId);
        if (existing != null) {
            return existing;
        }
        User operator = currentUser();
        LocalDateTime now = LocalDateTime.now();
        String agreementNo = nextAgreementNo();
        String content = buildAgreementContent(row, agreementNo, now);
        agreementMapper.insertGenerated(agreementNo, row.applicationId(), row.catId(), row.userId(),
                content, "Generated from final-approved adoption application", operator.userId(), now);
        logOperation(operator, "Generate adoption agreement", "AGREEMENT", applicationId, null,
                "GENERATED", agreementNo);
        sendMessage(row.userId(), "认养协议已生成", "你的认养协议已生成，请等待交接安排。", "AGREEMENT", agreementNo, operator);
        return agreementMapper.findByApplicationId(applicationId);
    }

    public AgreementInfo getAgreement(Long id) {
        AgreementInfo agreement = agreementMapper.findById(id);
        if (agreement == null) {
            throw new BusinessException("Agreement does not exist");
        }
        return agreement;
    }

    @Transactional
    public AgreementInfo updateAgreement(Long id, AgreementEditRequest request) {
        if (AuthContext.role() != Role.ADMIN) {
            throw new BusinessException("Only admins can edit agreements");
        }
        AgreementInfo agreement = getAgreement(id);
        if (!"DRAFT".equals(agreement.status()) && !"GENERATED".equals(agreement.status())) {
            throw new BusinessException("Handed-over agreements cannot be edited");
        }
        User operator = currentUser();
        int updated = agreementMapper.updateContent(id, request.agreementContent(), request.remark(),
                operator.userId(), LocalDateTime.now());
        if (updated == 0) {
            throw new BusinessException("Agreement status changed, edit failed");
        }
        logOperation(operator, "Edit adoption agreement", "AGREEMENT", String.valueOf(id),
                agreement.status(), agreement.status(), request.remark());
        return getAgreement(id);
    }

    @Transactional
    public AgreementInfo cancelAgreement(Long id, ActionReasonRequest request) {
        if (AuthContext.role() != Role.ADMIN) {
            throw new BusinessException("只有管理员可以取消协议");
        }
        String reason = DataQualityValidator.requireCleanText("取消原因", request.reason(), 2, 300);
        AgreementInfo agreement = getAgreement(id);
        if ("HANDED_OVER".equals(agreement.status()) || agreement.handoverTime() != null) {
            throw new BusinessException("已交接协议不能取消");
        }
        User operator = currentUser();
        int updated = agreementMapper.cancelAgreement(id, reason, operator.userId(), LocalDateTime.now());
        if (updated == 0) {
            throw new BusinessException("协议状态已变化，取消失败");
        }
        logOperation(operator, "取消认养协议", "AGREEMENT", String.valueOf(id), agreement.status(), "CANCELLED", reason);
        sendMessage(agreement.adopterId(), "认养协议已取消", "你的认养协议已取消，原因：" + reason, "AGREEMENT", agreement.agreementNo(), operator);
        return getAgreement(id);
    }

    @Transactional
    public void deleteAgreement(Long id, ActionReasonRequest request) {
        if (AuthContext.role() != Role.ADMIN) {
            throw new BusinessException("只有管理员可以删除协议");
        }
        String reason = DataQualityValidator.requireCleanText("删除原因", request.reason(), 2, 300);
        AgreementInfo agreement = getAgreement(id);
        User operator = currentUser();
        int deleted = agreementMapper.deleteAgreement(id, reason, operator.userId(), LocalDateTime.now());
        if (deleted == 0) {
            throw new BusinessException("当前协议状态不能删除");
        }
        logOperation(operator, "删除认养协议", "AGREEMENT", String.valueOf(id), agreement.status(), "DELETED", reason);
    }

    @Transactional
    public AgreementInfo completeHandover(Long id, AgreementHandoverRequest request) {
        if (AuthContext.role() != Role.ADMIN) {
            throw new BusinessException("Only admins can complete handover");
        }
        AgreementInfo agreement = getAgreement(id);
        if (!"GENERATED".equals(agreement.status())) {
            throw new BusinessException("Only GENERATED agreements can be handed over");
        }
        ApplicationReviewRow row = requireApplicationRow(agreement.applicationId());
        if (row.status() != ApplicationStatus.PENDING_HANDOVER) {
            throw new BusinessException("Application is not PENDING_HANDOVER");
        }
        if (isBlank(request.handoverLocation()) || isBlank(request.handoverUserId()) || request.handoverTime() == null) {
            throw new BusinessException("Handover time, location and handover user are required");
        }
        findUser(request.handoverUserId());
        if (followupTaskMapper.countByApplicationId(row.applicationId()) > 0) {
            throw new BusinessException("Follow-up tasks already exist for this application");
        }
        User operator = currentUser();
        LocalDateTime now = LocalDateTime.now();
        int agreementUpdated = agreementMapper.markHandedOver(id, request.handoverTime(), request.handoverLocation(),
                request.handoverUserId(), request.adopterConfirmed(), request.volunteerConfirmed(),
                request.remark(), operator.userId(), now);
        if (agreementUpdated == 0) {
            throw new BusinessException("Agreement is not in a handover-ready status");
        }
        int applicationUpdated = applicationMapper.markMisHandedOver(row.applicationId(),
                "Adoption handover completed", operator.userId(), request.handoverTime());
        if (applicationUpdated == 0) {
            throw new BusinessException("Application handover status update failed");
        }
        Cat cat = findCat(row.catId());
        if (cat.status() != CatStatus.FOLLOWING && cat.status() != CatStatus.ADOPTED) {
            catMapper.updateStatus(row.catId(), CatStatus.FOLLOWING, now);
            logOperation(operator, "Cat enters follow-up after handover", "CAT", row.catId(),
                    cat.status().name(), CatStatus.FOLLOWING.name(), row.applicationId());
        }
        int cancelled = applicationMapper.cancelOtherMisActive(row.catId(), row.applicationId(),
                "Cat adoption handover has been completed", operator.userId(), now);
        createFollowupTasks(row, id, request.handoverTime().toLocalDate(), operator, now);
        logOperation(operator, "Complete adoption handover", "AGREEMENT", String.valueOf(id),
                "GENERATED", "HANDED_OVER", "cancelledOtherApplications=" + cancelled);
        logOperation(operator, "Adoption application handed over", "APPLICATION", row.applicationId(),
                row.status().name(), ApplicationStatus.HANDED_OVER.name(), agreement.agreementNo());
        sendMessage(row.userId(), "认养交接已完成", "交接已完成，系统已生成 7/30/90 天回访任务。", "AGREEMENT", agreement.agreementNo(), operator);
        return getAgreement(id);
    }

    public List<FollowupTaskInfo> listMyFollowupTasks(FollowupTaskStatus status) {
        return followupTaskMapper.findTasks(AuthContext.userId(), null, status, null, null);
    }

    public FollowupTaskInfo getMyFollowupTask(Long id) {
        FollowupTaskInfo task = requireFollowupTask(id);
        if (!task.adopterId().equals(AuthContext.userId())) {
            throw new BusinessException("You can only view your own follow-up task");
        }
        return task;
    }

    @Transactional
    public FollowupTaskInfo submitFollowupRecord(Long id, FollowupRecordSubmitRequest request) {
        validateFollowupSubmitRequest(request);
        FollowupTaskInfo task = requireFollowupTask(id);
        if (!task.adopterId().equals(AuthContext.userId())) {
            throw new BusinessException("You can only submit your own follow-up task");
        }
        return submitFollowupRecordInternal(id, request, false);
    }

    public List<FollowupRecordInfo> listMyFollowupRecords(Long id) {
        FollowupTaskInfo task = getMyFollowupTask(id);
        return followupTaskMapper.findRecordsByTaskId(task.id());
    }

    private FollowupTaskInfo submitFollowupRecordInternal(Long id, FollowupRecordSubmitRequest request, boolean staffRecord) {
        // 回访记录共用流程：认养人自填和后台补录都写入记录表，再根据异常标记推进任务状态。
        if (request.abnormalFlag() && isBlank(request.abnormalDesc())) {
            throw new BusinessException("Abnormal description is required");
        }
        FollowupTaskInfo task = requireFollowupTask(id);
        if ("COMPLETED".equals(task.status())) {
            throw new BusinessException("Completed follow-up tasks cannot be edited");
        }
        User operator = currentUser();
        LocalDateTime now = LocalDateTime.now();
        String sourceLabel = staffRecord
                ? (operator.role() == Role.ADMIN ? "管理员回访记录" : "志愿者回访记录")
                : "认养人猫咪状态记录";
        String content = "[" + sourceLabel + "] " + request.content();
        String volunteerComment = staffRecord ? "记录人：" + operator.userName() : null;
        FollowupTaskStatus nextStatus = nextFollowupStatus(task.status(), request.abnormalFlag());
        followupTaskMapper.insertRecord(id, task.applicationId(), task.agreementId(), task.catId(), task.adopterId(),
                content, request.catCondition(), request.environmentDesc(), request.photoUrl(),
                request.abnormalFlag(), request.abnormalDesc(), volunteerComment, operator.userId(), now);
        followupTaskMapper.updateTaskStatus(id, nextStatus, now.toLocalDate(), request.abnormalFlag(),
                staffRecord ? operator.userId() : task.handlerId(), operator.userId(), now);
        if (request.abnormalFlag()) {
            // 异常回访会立即生成预警，方便管理员在预警中心持续跟踪。
            createFollowupWarning(task, "FOLLOWUP_ABNORMAL", "HIGH", request.abnormalDesc(), operator);
            sendMessage(task.adopterId(), "异常回访已记录", sourceLabel + "已记录异常情况，志愿者或管理员将继续跟进。", "FOLLOWUP", String.valueOf(id), operator);
            if (staffRecord && operator.role() == Role.VOLUNTEER) {
                sendMessage(operator.userId(), "感谢完成异常回访记录",
                        "你已完成猫咪 " + valueOrDefault(task.catName(), task.catId()) + " 的异常回访记录，系统已生成预警。",
                        "FOLLOWUP", String.valueOf(id), operator);
            }
        } else if (staffRecord) {
            sendMessage(task.adopterId(), "回访记录已更新", sourceLabel + "已补充到你的回访任务中。", "FOLLOWUP", String.valueOf(id), operator);
            if (operator.role() == Role.VOLUNTEER) {
                sendMessage(operator.userId(), "感谢完成回访任务",
                        "你已完成猫咪 " + valueOrDefault(task.catName(), task.catId()) + " 的回访记录，系统已同步任务状态。",
                        "FOLLOWUP", String.valueOf(id), operator);
            }
        } else {
            sendMessage(task.adopterId(), "猫咪状态已上传", "你上传的猫咪状态已保存到回访记录。", "FOLLOWUP", String.valueOf(id), operator);
        }
        logOperation(operator, staffRecord ? "Submit staff follow-up record" : "Submit adopter follow-up record",
                "FOLLOWUP_TASK", String.valueOf(id),
                task.status(), nextStatus.name(), request.abnormalFlag() ? request.abnormalDesc() : request.content());
        maybeMarkCatAdopted(task.catId(), operator);
        return requireFollowupTask(id);
    }

    private FollowupTaskStatus nextFollowupStatus(String currentStatus, boolean abnormalFlag) {
        if (abnormalFlag) {
            return FollowupTaskStatus.ABNORMAL;
        }
        if ("PENDING".equals(currentStatus) || "OVERDUE".equals(currentStatus)) {
            return FollowupTaskStatus.COMPLETED;
        }
        if ("ABNORMAL".equals(currentStatus)) {
            return FollowupTaskStatus.ABNORMAL;
        }
        return FollowupTaskStatus.COMPLETED;
    }

    public List<FollowupTaskInfo> listAdminFollowupTasks(FollowupTaskStatus status, LocalDate planDate, String keyword) {
        // 后台查看任务前先刷新逾期状态，保证列表展示的是实时待办。
        refreshOverdueTasksInternal(null);
        return followupTaskMapper.findTasks(null, null, status, planDate, blankToNull(keyword));
    }

    public List<FollowupTaskInfo> listAdminFollowupTasks(FollowupTaskStatus status, String taskType,
                                                         LocalDate planDate, String keyword) {
        refreshOverdueTasksInternal(null);
        return followupTaskMapper.findTasks(null, null, null, status, blankToNull(taskType), planDate, blankToNull(keyword));
    }

    public FollowupTaskInfo getAdminFollowupTask(Long id) {
        return requireFollowupTask(id);
    }

    public List<FollowupRecordInfo> listAdminFollowupRecords(Long id) {
        requireFollowupTask(id);
        return followupTaskMapper.findRecordsByTaskId(id);
    }

    @Transactional
    public FollowupTaskInfo submitAdminFollowupRecord(Long id, FollowupRecordSubmitRequest request) {
        validateFollowupSubmitRequest(request);
        return submitFollowupRecordInternal(id, request, true);
    }

    @Transactional
    public FollowupTaskInfo markFollowupAbnormal(Long id, String abnormalDesc, String volunteerComment) {
        if (isBlank(abnormalDesc)) {
            throw new BusinessException("Abnormal description is required");
        }
        FollowupTaskInfo task = requireFollowupTask(id);
        User operator = currentUser();
        LocalDateTime now = LocalDateTime.now();
        followupTaskMapper.updateTaskStatus(id, FollowupTaskStatus.ABNORMAL, task.actualDate(),
                true, operator.userId(), operator.userId(), now);
        if (!isBlank(volunteerComment)) {
            followupTaskMapper.updateRecordVolunteerComment(id, volunteerComment, operator.userId(), now);
        }
        createFollowupWarning(task, "FOLLOWUP_ABNORMAL", "HIGH", abnormalDesc, operator);
        logOperation(operator, "Mark follow-up abnormal", "FOLLOWUP_TASK", String.valueOf(id),
                task.status(), FollowupTaskStatus.ABNORMAL.name(), abnormalDesc);
        if (operator.role() == Role.VOLUNTEER) {
            sendMessage(operator.userId(), "感谢处理异常回访",
                    "你已标记并记录异常回访，系统已生成预警并同步任务状态。",
                    "FOLLOWUP", String.valueOf(id), operator);
        }
        return requireFollowupTask(id);
    }

    @Transactional
    public FollowupRefreshResult refreshOverdueTasks() {
        if (AuthContext.role() != Role.ADMIN) {
            throw new BusinessException("Only admins can refresh overdue tasks");
        }
        return refreshOverdueTasksInternal(currentUser());
    }

    public List<WarningInfo> listWarnings(String warningType, String warningLevel, String status, String keyword) {
        return warningRecordMapper.findWarnings(null, blankToNull(warningType), blankToNull(warningLevel),
                blankToNull(status), null, blankToNull(keyword));
    }

    public WarningInfo getWarning(Long id) {
        WarningInfo warning = warningRecordMapper.findById(id);
        if (warning == null) {
            throw new BusinessException("Warning does not exist");
        }
        return warning;
    }

    @Transactional
    public WarningInfo handleWarning(Long id, WarningHandleRequest request) {
        // 预警处理保留处理意见和处理人，处理完成后可能联动猫咪进入已认养状态。
        if (AuthContext.role() != Role.ADMIN) {
            throw new BusinessException("Only admins can handle warnings");
        }
        String targetStatus = normalizeWarningStatus(request.targetStatus());
        WarningInfo warning = getWarning(id);
        if ((("HANDLED".equals(warning.status()) || "IGNORED".equals(warning.status()))
                && warning.status().equals(targetStatus))) {
            throw new BusinessException("Warning has already been handled with the same status");
        }
        User operator = currentUser();
        LocalDateTime now = LocalDateTime.now();
        warningRecordMapper.updateHandleStatus(id, targetStatus, request.handleComment(), operator.userId(), now);
        logOperation(operator, "Handle warning", "WARNING", String.valueOf(id),
                warning.status(), targetStatus, request.handleComment());
        if (warning.userId() != null) {
            sendMessage(warning.userId(), "异常预警处理结果", "你的异常反馈处理状态：" + targetStatus + "。处理意见：" + request.handleComment(), "WARNING", String.valueOf(id), operator);
        }
        if (warning.catId() != null && ("HANDLED".equals(targetStatus) || "IGNORED".equals(targetStatus))) {
            maybeMarkCatAdopted(warning.catId(), operator);
        }
        return getWarning(id);
    }

    @Transactional
    public void deleteWarning(Long id) {
        if (AuthContext.role() != Role.ADMIN) {
            throw new BusinessException("Only admins can delete warnings");
        }
        WarningInfo warning = getWarning(id);
        User operator = currentUser();
        int deleted = warningRecordMapper.deleteWarning(id, operator.userId(), LocalDateTime.now());
        if (deleted == 0) {
            throw new BusinessException("Warning delete failed");
        }
        logOperation(operator, "Delete warning", "WARNING", String.valueOf(id), warning.status(), "DELETED", warning.warningType());
    }

    @Transactional
    public AdoptionApplication submitApplication(ApplicationRequest request) {
        User user = findUser(AuthContext.userId());
        if (user.role() != Role.STUDENT) {
            throw new BusinessException("只有普通用户可以提交认养申请");
        }
        Cat cat = findCat(request.catId());
        if (cat.status() != CatStatus.ADOPTABLE) {
            throw new BusinessException("当前猫咪不处于可认养状态");
        }
        if (applicationMapper.countActive(user.userId(), request.catId()) > 0) {
            throw new BusinessException("已存在待处理或已通过的申请");
        }
        String applicationPrefix = "APP" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String applicationId = codeGenerator.nextAfter("APP", applicationMapper.maxSuffixByPrefix(applicationPrefix));
        AdoptionApplication application = new AdoptionApplication(applicationId, user.userId(), request.catId(),
                request.housingInfo(), request.familyAttitude(), request.petExperience(), request.economicAbility(),
                request.promiseAccepted(), ApplicationStatus.PENDING, null, null, null, LocalDateTime.now(), null, null);
        applicationMapper.insert(application);
        log(user.userName(), "提交认养申请", "APPLICATION", application.applicationId(), cat.catName());
        sendMessageToRole(Role.VOLUNTEER, "新的认养初审任务",
                "申请 " + applicationId + " 正在等待志愿者初审，请在“认养初审”中处理。",
                "APPLICATION", applicationId, user, null);
        return application;
    }

    public List<AdoptionApplication> listApplications(ApplicationStatus status) {
        return applicationMapper.findAll(status);
    }

    public List<ApplicationDetail> listApplicationDetails(ApplicationStatus status) {
        return applicationMapper.findDetails(status);
    }

    public List<ApplicationDetail> listMyApplicationDetails(ApplicationStatus status) {
        return applicationMapper.findDetailsByUser(AuthContext.userId(), status);
    }

    @Transactional
    public AdoptionApplication reviewApplication(String applicationId, ReviewRequest request) {
        AdoptionApplication application = findApplication(applicationId);
        if (application.status() != ApplicationStatus.PENDING) {
            throw new BusinessException("只有待审核申请可以审核");
        }
        if (request.approved() && findCat(application.catId()).status() != CatStatus.ADOPTABLE) {
            throw new BusinessException("猫咪当前不处于可认养状态，不能审核通过");
        }
        ApplicationStatus status = request.approved() ? ApplicationStatus.APPROVED : ApplicationStatus.REJECTED;
        String agreementNo = request.approved() ? "AGR-" + application.applicationId() : null;
        LocalDateTime reviewedAt = LocalDateTime.now();
        applicationMapper.updateReview(applicationId, status, request.reviewNote(), request.interviewNote(), agreementNo, reviewedAt);
        if (request.approved()) {
            catMapper.updateStatus(application.catId(), CatStatus.RESERVED, reviewedAt);
            int closed = applicationMapper.rejectOtherPending(application.catId(), applicationId, reviewedAt);
            if (closed > 0) {
                log("审核员", "自动关闭同猫待审申请", "CAT", application.catId(), "已关闭 " + closed + " 条待审申请");
            }
        }
        log("审核员", request.approved() ? "审核通过" : "审核拒绝", "APPLICATION", applicationId, request.reviewNote());
        return findApplication(applicationId);
    }

    @Transactional
    public AdoptionApplication handover(String applicationId) {
        AdoptionApplication application = findApplication(applicationId);
        if (application.status() != ApplicationStatus.APPROVED) {
            throw new BusinessException("只有已通过申请可以交接");
        }
        LocalDateTime handedOverAt = LocalDateTime.now();
        applicationMapper.updateHandover(applicationId, handedOverAt);
        catMapper.updateStatus(application.catId(), CatStatus.ADOPTED, handedOverAt);
        log("管理员", "完成认养交接", "APPLICATION", applicationId, application.agreementNo());
        return findApplication(applicationId);
    }

    @Transactional
    public AdoptionApplication withdraw(String applicationId) {
        AdoptionApplication application = findApplication(applicationId);
        Role role = AuthContext.role();
        boolean canManage = role == Role.ADMIN || role == Role.VOLUNTEER;
        if (!canManage && !application.userId().equals(AuthContext.userId())) {
            throw new BusinessException("只能撤回自己的认养申请");
        }
        if (application.status() != ApplicationStatus.PENDING) {
            throw new BusinessException("只有待审核申请可以撤回");
        }
        applicationMapper.updateWithdraw(applicationId, LocalDateTime.now());
        log("申请人", "撤回认养申请", "APPLICATION", applicationId, application.catId());
        return findApplication(applicationId);
    }

    @Transactional
    public FollowupRecord addFollowup(FollowupRequest request) {
        AdoptionApplication application = findApplication(request.applicationId());
        if (application.status() != ApplicationStatus.HANDED_OVER) {
            throw new BusinessException("未完成交接的申请不能回访");
        }
        FollowupRecord record = new FollowupRecord(codeGenerator.next("RF"), request.applicationId(), LocalDateTime.now(),
                request.method(), request.catCondition(), request.environmentDescription(), request.result(),
                request.photoUrl(), request.suggestion(), request.operatorName());
        followupMapper.insert(record);
        if (request.result() == FollowupResult.RETURNED) {
            catMapper.updateStatus(application.catId(), CatStatus.OBSERVING, LocalDateTime.now());
        }
        log(request.operatorName(), "提交回访记录", "FOLLOWUP", record.followupId(), request.result().getLabel());
        return record;
    }

    public List<FollowupRecord> listFollowups(String applicationId, FollowupResult result) {
        return followupMapper.findAll(applicationId, result);
    }

    @Transactional
    public Notice createNotice(NoticeRequest request) {
        Notice notice = new Notice(codeGenerator.next("NT"), request.title(), request.content(), request.publisher(),
                request.pinned(), request.enabled(), LocalDateTime.now(), null, defaultNoticeTargetRoles());
        noticeMapper.insert(notice);
        replaceNoticeTargetRoles(notice.noticeId(), notice.targetRoles());
        log(request.publisher(), "发布公告", "NOTICE", notice.noticeId(), notice.title());
        return notice;
    }

    public List<Notice> listNotices(boolean includeDisabled) {
        return noticeMapper.findAll(!includeDisabled);
    }

    public Notice getNotice(String noticeId) {
        Notice notice = noticeMapper.findById(noticeId);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        return notice;
    }

    @Transactional
    public Notice updateNotice(String noticeId, NoticeRequest request) {
        Notice old = getNotice(noticeId);
        Notice updated = new Notice(noticeId, request.title(), request.content(), request.publisher(),
                request.pinned(), request.enabled(), old.publishedAt(), old.imageUrl(), old.targetRoles());
        noticeMapper.update(updated);
        replaceNoticeTargetRoles(noticeId, updated.targetRoles());
        log(request.publisher(), "更新公告", "NOTICE", noticeId, request.title());
        return getNotice(noticeId);
    }

    @Transactional
    public Notice updateNoticeEnabled(String noticeId, boolean enabled) {
        getNotice(noticeId);
        noticeMapper.updateEnabled(noticeId, enabled);
        log("管理员", enabled ? "上架公告" : "下架公告", "NOTICE", noticeId, String.valueOf(enabled));
        return getNotice(noticeId);
    }

    @Transactional
    public Notice updateNoticePinned(String noticeId, boolean pinned) {
        getNotice(noticeId);
        noticeMapper.updatePinned(noticeId, pinned);
        log("管理员", pinned ? "置顶公告" : "取消置顶公告", "NOTICE", noticeId, String.valueOf(pinned));
        return getNotice(noticeId);
    }

    @Transactional
    public void deleteNotice(String noticeId) {
        getNotice(noticeId);
        noticeMapper.delete(noticeId);
        log("管理员", "删除公告", "NOTICE", noticeId, "公告已删除");
    }

    @Transactional
    public boolean toggleFavorite(String userId, String catId) {
        findUser(userId);
        Cat cat = findCat(catId);
        if (favoriteMapper.count(userId, catId) > 0) {
            favoriteMapper.delete(userId, catId);
            log(userId, "取消收藏", "CAT", catId, cat.catName());
            return false;
        }
        favoriteMapper.insert(userId, catId, LocalDateTime.now());
        log(userId, "收藏猫咪", "CAT", catId, cat.catName());
        return true;
    }

    public List<Cat> listFavoriteCats(String userId) {
        findUser(userId);
        return favoriteMapper.findCatsByUser(userId);
    }

    @Transactional
    public boolean toggleMyFavorite(String catId) {
        return toggleFavorite(AuthContext.userId(), catId);
    }

    public List<Cat> listMyFavoriteCats() {
        return listFavoriteCats(AuthContext.userId());
    }

    public List<AuditLog> listAuditLogs() {
        return auditLogMapper.findAll();
    }

    public List<Product> listProducts() {
        return productMapper.findActive();
    }

    public List<Product> listAllProducts() {
        return productMapper.findAll();
    }

    @Transactional
    public Product saveProduct(ProductRequest request) {
        Product product = new Product(codeGenerator.next("PR"), request.productName(), request.category(), request.price(),
                request.imageUrl(), request.description(), request.payUrl(), request.stock(), request.status(), LocalDateTime.now());
        productMapper.insert(product);
        log("管理员", "新增公益商品", "PRODUCT", product.productId(), product.productName());
        return product;
    }

    @Transactional
    public Product updateProduct(String productId, ProductRequest request) {
        Product old = findProduct(productId);
        Product product = new Product(productId, request.productName(), request.category(), request.price(),
                request.imageUrl(), request.description(), request.payUrl(), request.stock(), request.status(), old.createdAt());
        productMapper.update(product);
        log("管理员", "更新公益商品", "PRODUCT", productId, product.productName());
        return findProduct(productId);
    }

    @Transactional
    public Product updateProductStatus(String productId, boolean status) {
        findProduct(productId);
        productMapper.updateStatus(productId, status);
        log("管理员", status ? "上架公益商品" : "下架公益商品", "PRODUCT", productId, String.valueOf(status));
        return findProduct(productId);
    }

    @Transactional
    public void deleteProduct(String productId) {
        findProduct(productId);
        productMapper.delete(productId);
        log("管理员", "删除公益商品", "PRODUCT", productId, "商品已删除");
    }

    @Transactional
    public ProductOrder createOrder(OrderRequest request) {
        User user = findUser(AuthContext.userId());
        Product product = findProduct(request.productId());
        if (!product.status()) {
            throw new BusinessException("商品已下架");
        }
        if (product.stock() == null || product.stock() < request.quantity()) {
            throw new BusinessException("库存不足");
        }
        if (productMapper.decreaseStock(product.productId(), request.quantity()) == 0) {
            throw new BusinessException("库存不足");
        }
        BigDecimal amount = product.price().multiply(BigDecimal.valueOf(request.quantity()));
        ProductOrder order = new ProductOrder(codeGenerator.next("OR"), user.userId(), user.userName(),
                product.productId(), product.productName(), request.quantity(), amount, product.payUrl(), "CREATED", LocalDateTime.now());
        productOrderMapper.insert(order);
        log(user.userName(), "创建文创订单", "ORDER", order.orderId(), product.productName());
        return order;
    }

    public List<ProductOrder> listOrders(boolean mineOnly) {
        return productOrderMapper.findAll(mineOnly ? AuthContext.userId() : null);
    }

    @Transactional
    public ProductOrder updateOrderStatus(String orderId, String status) {
        productOrderMapper.updateStatus(orderId, status);
        log("管理员", "更新订单状态", "ORDER", orderId, status);
        return productOrderMapper.findAll(null).stream()
                .filter(order -> order.orderId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("订单不存在"));
    }

    public DonationChannel getDonationChannel() {
        return donationChannelMapper.findEnabled();
    }

    @Transactional
    public DonationRecord createDonation(DonationRequest request) {
        User user = findUser(AuthContext.userId());
        DonationChannel channel = getDonationChannel();
        DonationRecord record = new DonationRecord(codeGenerator.next("DN"), user.userId(), user.userName(), request.amount(),
                channel == null ? null : channel.channelId(), request.donorMessage(), "RECORDED", LocalDateTime.now());
        donationRecordMapper.insert(record);
        log(user.userName(), "登记公益捐赠", "DONATION", record.donationId(), request.amount().toPlainString());
        return record;
    }

    public List<DonationRecord> listDonations(boolean mineOnly) {
        return donationRecordMapper.findAll(mineOnly ? AuthContext.userId() : null);
    }

    @Transactional
    public DonationRecord updateDonationStatus(String donationId, String status) {
        donationRecordMapper.updateStatus(donationId, status);
        log("管理员", "更新捐赠状态", "DONATION", donationId, status);
        return donationRecordMapper.findAll(null).stream()
                .filter(record -> record.donationId().equals(donationId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("捐赠记录不存在"));
    }

    public List<Article> listArticles(boolean includeUnpublished, String keyword) {
        return communityMapper.findArticles(!includeUnpublished, keyword);
    }

    @Transactional
    public Article getArticle(Integer articleId) {
        Article article = communityMapper.findArticleById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        communityMapper.increaseArticleHits(articleId);
        return communityMapper.findArticleById(articleId);
    }

    @Transactional
    public Article createArticle(ArticleRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Article article = new Article(null, request.title(), request.typeName(), request.coverUrl(), request.summary(),
                request.content(), request.source(), request.sourceUrl(), request.tags(), 0, 0,
                request.published(), request.pinned(), now, now);
        communityMapper.insertArticle(article);
        Article saved = communityMapper.findLatestArticle();
        replaceArticleTags(saved.articleId(), request.tags());
        log("管理员", "发布社区文章", "ARTICLE", request.title(), request.typeName());
        return communityMapper.findArticleById(saved.articleId());
    }

    @Transactional
    public Article updateArticle(Integer articleId, ArticleRequest request) {
        Article old = getArticle(articleId);
        Article updated = new Article(articleId, request.title(), request.typeName(), request.coverUrl(), request.summary(),
                request.content(), request.source(), request.sourceUrl(), request.tags(), old.hits(), old.praiseCount(),
                request.published(), request.pinned(), old.createdAt(), LocalDateTime.now());
        communityMapper.updateArticle(updated);
        replaceArticleTags(articleId, request.tags());
        log("管理员", "更新社区文章", "ARTICLE", String.valueOf(articleId), request.title());
        return communityMapper.findArticleById(articleId);
    }

    @Transactional
    public void deleteArticle(Integer articleId) {
        communityMapper.deleteArticle(articleId);
        log("管理员", "删除社区文章", "ARTICLE", String.valueOf(articleId), "文章已删除");
    }

    public List<ForumPost> listPosts(String keyword) {
        return communityMapper.findPosts(true, keyword);
    }

    @Transactional
    public ForumPost createPost(ForumPostRequest request) {
        User user = findUser(AuthContext.userId());
        LocalDateTime now = LocalDateTime.now();
        ForumPost post = new ForumPost(null, user.userId(), user.userName(), request.typeName(), request.title(),
                request.content(), request.coverUrl(), 0, 0, "PUBLISHED", now, now);
        communityMapper.insertPost(post);
        log(user.userName(), "发布社区帖子", "POST", request.title(), request.typeName());
        return communityMapper.findLatestPost();
    }

    @Transactional
    public ForumPost updatePostStatus(Integer postId, String status) {
        ForumPost post = communityMapper.findPostById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        Role role = AuthContext.role();
        if (role != Role.ADMIN && !post.userId().equals(AuthContext.userId())) {
            throw new BusinessException("只能管理自己的帖子");
        }
        communityMapper.updatePostStatus(postId, status);
        return communityMapper.findPostById(postId);
    }

    public DashboardStats dashboard() {
        long catCount = catMapper.countAll();
        long applicationCount = applicationMapper.countAll();
        long pendingApplicationCount = applicationMapper.countByStatus(ApplicationStatus.PENDING_INITIAL)
                + applicationMapper.countByStatus(ApplicationStatus.PENDING_FINAL)
                + applicationMapper.countByStatus(ApplicationStatus.PENDING_HANDOVER)
                + applicationMapper.countByStatus(ApplicationStatus.PENDING);
        long adoptedCount = applicationMapper.countSuccessful();
        return new DashboardStats(catCount,
                catMapper.countByStatus(CatStatus.ADOPTABLE),
                adoptedCount,
                pendingApplicationCount,
                catMapper.countByStatus(CatStatus.MEDICAL),
                followupMapper.countAll(),
                rate(catMapper.countSterilized(), catCount),
                rate(catMapper.countVaccinated(), catCount),
                rate(applicationMapper.countSuccessful(), applicationCount));
    }

    public AdminDashboardSummary adminDashboardSummary() {
        refreshOverdueTasksInternal(null);
        long totalFollowupTasks = followupTaskMapper.countAllActive();
        long completedFollowupTasks = followupTaskMapper.countByStatus(FollowupTaskStatus.COMPLETED);
        return new AdminDashboardSummary(
                catMapper.countAll(),
                catMapper.countByStatus(CatStatus.ADOPTABLE),
                catMapper.countByStatus(CatStatus.OBSERVING),
                catMapper.countByStatus(CatStatus.MEDICAL),
                catMapper.countByStatus(CatStatus.SUSPENDED),
                rescueReportMapper.countCluesByStatus(ClueStatus.PENDING_VERIFY),
                rescueReportMapper.countCluesByStatus(ClueStatus.CREATED_CAT),
                applicationMapper.countByStatus(ApplicationStatus.PENDING_INITIAL),
                applicationMapper.countByStatus(ApplicationStatus.PENDING_FINAL),
                applicationMapper.countByStatus(ApplicationStatus.PENDING_HANDOVER),
                applicationMapper.countByStatus(ApplicationStatus.HANDED_OVER),
                followupTaskMapper.countByStatus(FollowupTaskStatus.PENDING),
                completedFollowupTasks,
                followupTaskMapper.countByStatus(FollowupTaskStatus.OVERDUE),
                followupTaskMapper.countByStatus(FollowupTaskStatus.ABNORMAL),
                warningRecordMapper.countByStatus("PENDING"),
                warningRecordMapper.countByStatus("HANDLED"),
                rate(completedFollowupTasks, totalFollowupTasks),
                catMapper.countByStatus(CatStatus.FOLLOWING),
                applicationMapper.countByRiskLevel("HIGH"),
                AuthContext.userId() == null ? 0 : systemMessageMapper.countUnread(AuthContext.userId())
        );
    }

    public List<SystemMessageInfo> listMyMessages(String readStatus) {
        return systemMessageMapper.findMessages(AuthContext.userId(), normalizeReadStatus(readStatus), null);
    }

    public long unreadMessageCount() {
        return systemMessageMapper.countUnread(AuthContext.userId());
    }

    @Transactional
    public void markMessageRead(Long id) {
        int updated = systemMessageMapper.markRead(id, AuthContext.userId(), LocalDateTime.now());
        if (updated == 0) {
            throw new BusinessException("消息不存在或无权操作");
        }
    }

    @Transactional
    public void markAllMessagesRead() {
        systemMessageMapper.markAllRead(AuthContext.userId(), LocalDateTime.now());
    }

    public List<SystemMessageInfo> listAdminMessages(String receiverId, String bizType, String readStatus) {
        return systemMessageMapper.findMessages(blankToNull(receiverId), normalizeReadStatus(readStatus), blankToNull(bizType));
    }

    public List<NoticeAdminInfo> listAdminNotices(String publishStatus, String noticeType) {
        return noticeMapper.findAdmin(blankToNull(publishStatus), blankToNull(noticeType));
    }

    @Transactional
    public Notice createAdminNotice(NoticeAdminRequest request) {
        // 公告采用三范式设计：公告正文写入 t_notice，可见角色单独写入 notice_target_role。
        User operator = currentUser();
        String targetRoles = normalizeNoticeTargetRoles(request.targetRoles());
        Notice notice = new Notice(codeGenerator.next("NT"), request.title(), request.content(), operator.userName(),
                false, "PUBLISHED".equals(valueOrDefault(request.publishStatus(), "DRAFT")),
                "PUBLISHED".equals(request.publishStatus()) ? LocalDateTime.now() : null,
                blankToNull(request.imageUrl()), targetRoles);
        noticeMapper.insert(notice);
        noticeMapper.updateAdminFields(notice.noticeId(), valueOrDefault(request.noticeType(), "SYSTEM"),
                valueOrDefault(request.publishStatus(), "DRAFT"), operator.userId(), request.sortOrder(),
                blankToNull(request.imageUrl()), targetRoles, LocalDateTime.now());
        replaceNoticeTargetRoles(notice.noticeId(), targetRoles);
        logOperation(operator, "Create notice", "NOTICE", notice.noticeId(), null, request.publishStatus(), request.title());
        if (Boolean.TRUE.equals(request.sendMessage())) {
            broadcastMessageToRoles(targetRoles, "公告发布：" + request.title(), request.content(), "NOTICE", notice.noticeId(), operator);
        }
        return getNotice(notice.noticeId());
    }

    @Transactional
    public Notice updateAdminNotice(String noticeId, NoticeAdminRequest request) {
        // 编辑公告时同时重建角色关联表，保证前端仍按原 targetRoles 字符串回显。
        Notice old = getNotice(noticeId);
        User operator = currentUser();
        String targetRoles = normalizeNoticeTargetRoles(request.targetRoles());
        noticeMapper.update(new Notice(noticeId, request.title(), request.content(), operator.userName(),
                old.pinned(), "PUBLISHED".equals(valueOrDefault(request.publishStatus(), "DRAFT")), old.publishedAt(),
                blankToNull(request.imageUrl()), targetRoles));
        noticeMapper.updateAdminFields(noticeId, valueOrDefault(request.noticeType(), "SYSTEM"),
                valueOrDefault(request.publishStatus(), old.enabled() ? "PUBLISHED" : "DRAFT"), operator.userId(),
                request.sortOrder(), blankToNull(request.imageUrl()), targetRoles, LocalDateTime.now());
        replaceNoticeTargetRoles(noticeId, targetRoles);
        logOperation(operator, "Update notice", "NOTICE", noticeId, old.title(), request.title(), request.noticeType());
        if (Boolean.TRUE.equals(request.sendMessage()) && "PUBLISHED".equals(valueOrDefault(request.publishStatus(), "DRAFT"))) {
            broadcastMessageToRoles(targetRoles, "公告更新：" + request.title(), request.content(), "NOTICE", noticeId, operator);
        }
        return getNotice(noticeId);
    }

    @Transactional
    public Notice publishNotice(String noticeId) {
        User operator = currentUser();
        noticeMapper.updatePublishStatus(noticeId, "PUBLISHED", true, LocalDateTime.now(), LocalDateTime.now());
        Notice notice = getNotice(noticeId);
        logOperation(operator, "Publish notice", "NOTICE", noticeId, null, "PUBLISHED", notice.title());
        return notice;
    }

    @Transactional
    public Notice offlineNotice(String noticeId) {
        User operator = currentUser();
        noticeMapper.updatePublishStatus(noticeId, "OFFLINE", false, null, LocalDateTime.now());
        logOperation(operator, "Offline notice", "NOTICE", noticeId, null, "OFFLINE", noticeId);
        return getNotice(noticeId);
    }

    @Transactional
    public void deleteAdminNotice(String noticeId) {
        User operator = currentUser();
        noticeMapper.logicalDelete(noticeId, LocalDateTime.now());
        logOperation(operator, "Delete notice", "NOTICE", noticeId, null, "DELETED", noticeId);
    }

    public List<OperationLogInfo> listOperationLogs(String operatorKeyword, String operationType, String bizType,
                                                    LocalDateTime startTime, LocalDateTime endTime,
                                                    String keyword, Integer page, Integer size) {
        int safeSize = size == null || size <= 0 || size > 100 ? 50 : size;
        int safePage = page == null || page <= 0 ? 1 : page;
        return operationLogMapper.findLogs(blankToNull(operatorKeyword), blankToNull(operationType), blankToNull(bizType),
                startTime, endTime, blankToNull(keyword), safeSize, (safePage - 1) * safeSize);
    }

    public OperationLogInfo getOperationLog(Long id) {
        OperationLogInfo log = operationLogMapper.findById(id);
        if (log == null) {
            throw new BusinessException("操作日志不存在");
        }
        return log;
    }

    public List<DashboardDistributionItem> dashboardDistribution(String type) {
        return switch (type) {
            case "cat-status" -> dashboardMapper.catStatus();
            case "application-status" -> dashboardMapper.applicationStatus();
            case "followup-status" -> dashboardMapper.followupStatus();
            case "warning-type" -> dashboardMapper.warningType();
            default -> List.of();
        };
    }

    public String exportCatsCsv(String status, String healthLevel, String gender, String keyword) {
        List<Cat> cats = catMapper.findAdmin(parseCatStatus(status), parseHealthLevel(healthLevel),
                blankToNull(gender), blankToNull(keyword), null, null);
        List<List<?>> rows = new ArrayList<>();
        rows.add(List.of("猫咪编号", "昵称", "性别", "年龄估计", "毛色", "健康状态", "疫苗状态", "绝育状态", "猫咪状态", "发现地点", "创建时间"));
        for (Cat cat : cats) {
            rows.add(List.of(cat.catId(), valueOrDefault(cat.catName(), ""), valueOrDefault(cat.gender(), ""),
                    valueOrDefault(cat.ageEstimate(), ""), valueOrDefault(cat.color(), ""),
                    cat.healthLevel() == null ? "" : cat.healthLevel().name(), cat.vaccinated() ? "已免疫" : "未免疫",
                    cat.sterilized() ? "已绝育" : "未绝育", cat.status() == null ? "" : cat.status().name(),
                    valueOrDefault(cat.foundPlace(), ""), cat.createdAt()));
        }
        return toCsv(rows);
    }

    public String exportApplicationsCsv(String status, String riskLevel, String keyword) {
        List<ApplicationReviewRow> rowsData = applicationMapper.findReviewRows(null, parseApplicationStatus(status),
                blankToNull(riskLevel), blankToNull(keyword), null, null);
        List<List<?>> rows = new ArrayList<>();
        rows.add(List.of("申请编号", "猫咪编号", "猫咪昵称", "申请人", "申请状态", "系统评分", "风险等级", "提交时间", "初/终审意见"));
        for (ApplicationReviewRow app : rowsData) {
            rows.add(List.of(app.applicationId(), app.catId(), valueOrDefault(app.catName(), ""), valueOrDefault(app.userName(), ""),
                    app.status() == null ? "" : app.status().name(), app.score() == null ? "" : String.valueOf(app.score()), valueOrDefault(app.riskLevel(), ""),
                    app.appliedAt(), valueOrDefault(app.reviewNote(), "")));
        }
        return toCsv(rows);
    }

    public String exportFollowupsCsv(String status, String taskType, String planDate, String keyword) {
        LocalDate date = parseDateOrNull(planDate);
        List<FollowupTaskInfo> tasks = followupTaskMapper.findTasks(null, null, null, parseFollowupTaskStatus(status),
                blankToNull(taskType), date, blankToNull(keyword));
        List<List<?>> rows = new ArrayList<>();
        rows.add(List.of("猫咪编号", "猫咪昵称", "认养人", "回访类型", "计划日期", "实际提交时间", "回访状态", "是否异常", "异常说明"));
        for (FollowupTaskInfo task : tasks) {
            rows.add(List.of(task.catId(), valueOrDefault(task.catName(), ""), valueOrDefault(task.adopterName(), ""),
                    valueOrDefault(task.taskType(), ""), task.planDate(), task.submitTime(), valueOrDefault(task.status(), ""),
                    Boolean.TRUE.equals(task.recordAbnormalFlag()) || Boolean.TRUE.equals(task.abnormalFlag()) ? "是" : "否",
                    valueOrDefault(task.abnormalDesc(), "")));
        }
        return toCsv(rows);
    }

    public String exportWarningsCsv(String warningType, String warningLevel, String status, String keyword) {
        List<WarningInfo> warnings = warningRecordMapper.findWarnings(null, blankToNull(warningType),
                blankToNull(warningLevel), blankToNull(status), null, blankToNull(keyword));
        List<List<?>> rows = new ArrayList<>();
        rows.add(List.of("预警类型", "预警等级", "关联猫咪", "关联用户", "预警状态", "创建时间", "处理人", "处理意见", "处理时间"));
        for (WarningInfo warning : warnings) {
            rows.add(List.of(warning.warningType(), warning.warningLevel(),
                    valueOrDefault(warning.catName(), valueOrDefault(warning.catId(), "")),
                    valueOrDefault(warning.userName(), valueOrDefault(warning.userId(), "")),
                    valueOrDefault(warning.status(), ""), warning.createTime(), valueOrDefault(warning.handlerName(), ""),
                    valueOrDefault(warning.handleComment(), ""), warning.handleTime()));
        }
        return toCsv(rows);
    }

    public List<AdminSearchGroup> adminSearch(String keyword) {
        String kw = blankToNull(keyword);
        if (kw == null) {
            return List.of();
        }
        Role role = AuthContext.role();
        List<AdminSearchGroup> groups = new ArrayList<>();
        List<AdminSearchResult> cats = catMapper.findAdmin(null, null, null, kw, 8, 0).stream()
                .map(cat -> new AdminSearchResult(cat.catId(), valueOrDefault(cat.catName(), cat.catId()),
                        valueOrDefault(cat.foundPlace(), "") + " / " + (cat.status() == null ? "" : cat.status().name()),
                        "#/admin/cats/" + cat.catId(), cat.status() == null ? "" : cat.status().name()))
                .toList();
        groups.add(new AdminSearchGroup("cats", "猫咪档案", cats));
        if (role != Role.HOSPITAL) {
            groups.add(new AdminSearchGroup("clues", "线索", rescueReportMapper.findClues(null, null, null, kw, 8, 0).stream()
                    .map(clue -> new AdminSearchResult(clue.id(), clue.id(),
                            valueOrDefault(clue.foundLocation(), "") + " / " + valueOrDefault(clue.reporterName(), ""),
                            "#/admin/clues?keyword=" + urlHashValue(clue.id()), clue.status()))
                    .toList()));
            groups.add(new AdminSearchGroup("applications", "认养申请", applicationMapper.findReviewRows(null, null, null, kw, 8, 0).stream()
                    .map(app -> new AdminSearchResult(app.applicationId(), app.applicationId(),
                            valueOrDefault(app.catName(), app.catId()) + " / " + valueOrDefault(app.userName(), ""),
                            "#/admin/adoption/audits?keyword=" + urlHashValue(app.applicationId()),
                            app.status() == null ? "" : app.status().name()))
                    .toList()));
            groups.add(new AdminSearchGroup("agreements", "协议交接", agreementMapper.findPending(null, kw).stream().limit(8)
                    .map(agreement -> new AdminSearchResult(String.valueOf(agreement.id()),
                            valueOrDefault(agreement.agreementNo(), agreement.applicationId()),
                            valueOrDefault(agreement.catName(), agreement.catId()) + " / " + valueOrDefault(agreement.adopterName(), ""),
                            "#/admin/agreements?keyword=" + urlHashValue(valueOrDefault(agreement.agreementNo(), agreement.applicationId())),
                            valueOrDefault(agreement.status(), "")))
                    .toList()));
            groups.add(new AdminSearchGroup("warnings", "异常预警", warningRecordMapper.findWarnings(null, null, null, null, null, kw).stream().limit(8)
                    .map(warning -> new AdminSearchResult(String.valueOf(warning.id()), warning.title(),
                            valueOrDefault(warning.catName(), warning.catId()) + " / " + valueOrDefault(warning.userName(), ""),
                            "#/admin/warnings?keyword=" + urlHashValue(warning.title()),
                            valueOrDefault(warning.status(), "")))
                    .toList()));
        }
        return groups;
    }

    public List<LocationStat> locationStats() {
        return catMapper.countByLocation();
    }

    private String nextAgreementNo() {
        String seed = codeGenerator.next("AG").replaceAll("\\D", "");
        String suffix = seed.length() >= 4 ? seed.substring(seed.length() - 4) : String.format("%04d", Integer.parseInt(seed));
        return "AGR" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + suffix;
    }

    private String buildAgreementContent(ApplicationReviewRow row, String agreementNo, LocalDateTime generatedTime) {
        Cat cat = findCat(row.catId());
        String auditText = adoptionAuditMapper.findByApplicationId(row.applicationId()).stream()
                .map(audit -> audit.auditStage() + " " + audit.auditResult() + ": " + audit.auditComment())
                .reduce((left, right) -> left + "\n" + right)
                .orElse("No audit record");
        return """
                Campus Stray Cat Adoption Agreement

                Agreement No: %s
                Application No: %s
                Cat: %s / %s / gender=%s / health=%s / vaccinated=%s / sterilized=%s
                Adopter: %s / %s
                Generated Time: %s

                The adopter promises:
                1. Never abandon or abuse the cat.
                2. Accept scheduled follow-up visits.
                3. Provide timely medical care when the cat is ill.
                4. Do not transfer the cat without notifying the volunteer group.
                5. Contact the volunteer group if continued care becomes impossible.

                Audit Result:
                %s

                Handover time, place and handler will be filled before final handover.
                """.formatted(agreementNo, row.applicationId(), cat.catId(), cat.catName(), cat.gender(),
                cat.healthLevel(), cat.vaccinated(), cat.sterilized(), row.userName(), row.phone(),
                generatedTime, auditText);
    }

    private void createFollowupTasks(ApplicationReviewRow row, Long agreementId, LocalDate handoverDate,
                                     User operator, LocalDateTime now) {
        int created = 0;
        created += followupTaskMapper.insertTask(row.applicationId(), agreementId, row.catId(), row.userId(),
                handoverDate.plusDays(7), FollowupTaskType.DAY_7, FollowupTaskStatus.PENDING, operator.userId(), now);
        created += followupTaskMapper.insertTask(row.applicationId(), agreementId, row.catId(), row.userId(),
                handoverDate.plusDays(30), FollowupTaskType.DAY_30, FollowupTaskStatus.PENDING, operator.userId(), now);
        created += followupTaskMapper.insertTask(row.applicationId(), agreementId, row.catId(), row.userId(),
                handoverDate.plusDays(90), FollowupTaskType.DAY_90, FollowupTaskStatus.PENDING, operator.userId(), now);
        if (created != 3) {
            throw new BusinessException("Failed to create 7/30/90 day follow-up tasks");
        }
        logOperation(operator, "Create follow-up tasks", "FOLLOWUP_TASK", row.applicationId(),
                null, "DAY_7,DAY_30,DAY_90", "agreementId=" + agreementId);
        sendMessage(row.userId(), "回访任务已生成", "系统已生成 7/30/90 天回访任务，请按时提交反馈。", "FOLLOWUP", row.applicationId(), operator);
        sendMessageToRole(Role.VOLUNTEER, "新的回访任务待处理",
                "申请 " + row.applicationId() + " 已完成交接，系统已生成 7/30/90 天回访任务，请在前台“回访任务”中跟进。",
                "FOLLOWUP", row.applicationId(), operator, null);
    }

    private CatStatus parseCatStatus(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return CatStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException error) {
            return null;
        }
    }

    private HealthLevel parseHealthLevel(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return HealthLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException error) {
            return null;
        }
    }

    private ApplicationStatus parseApplicationStatus(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return ApplicationStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException error) {
            return null;
        }
    }

    private FollowupTaskStatus parseFollowupTaskStatus(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return FollowupTaskStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException error) {
            return null;
        }
    }

    private LocalDate parseDateOrNull(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception error) {
            return null;
        }
    }

    private String toCsv(List<List<?>> rows) {
        return rows.stream()
                .map(row -> row.stream().map(this::csvCell).reduce((left, right) -> left + "," + right).orElse(""))
                .reduce((left, right) -> left + "\n" + right)
                .orElse("");
    }

    private String csvCell(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"").replace("\r", " ").replace("\n", " ") + "\"";
    }

    private String urlHashValue(String value) {
        return value == null ? "" : value.replace("%", "%25").replace(" ", "%20").replace("#", "%23").replace("&", "%26");
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void sendMessage(String receiverId, String title, String content, String bizType, String bizId, User operator) {
        if (isBlank(receiverId)) {
            return;
        }
        systemMessageMapper.insert(receiverId, title, content, bizType, bizId,
                operator == null ? "SYSTEM" : operator.userId(), LocalDateTime.now());
    }

    private void broadcastMessage(String title, String content, String bizType, String bizId, User operator) {
        for (User user : userMapper.findAll()) {
            sendMessage(user.userId(), title, content, bizType, bizId, operator);
        }
    }

    private void broadcastMessageToRoles(String roles, String title, String content, String bizType, String bizId, User operator) {
        // 将公告或业务提醒广播给多个角色，避免前端逐个用户发送造成重复逻辑。
        Set<Role> targetRoles = parseNoticeTargetRoles(roles);
        if (targetRoles.isEmpty()) {
            broadcastMessage(title, content, bizType, bizId, operator);
            return;
        }
        Set<String> sent = new LinkedHashSet<>();
        for (Role role : targetRoles) {
            for (User user : userMapper.findAdminUsers(role, true, null)) {
                if (sent.add(user.userId())) {
                    sendMessage(user.userId(), title, content, bizType, bizId, operator);
                }
            }
        }
    }

    private String defaultNoticeTargetRoles() {
        return "STUDENT,VOLUNTEER,HOSPITAL,ADMIN";
    }

    private String normalizeNoticeTargetRoles(String roles) {
        Set<Role> parsed = parseNoticeTargetRoles(roles);
        if (parsed.isEmpty()) {
            return defaultNoticeTargetRoles();
        }
        return String.join(",", parsed.stream().map(Role::name).toList());
    }

    private Set<Role> parseNoticeTargetRoles(String roles) {
        Set<Role> parsed = new LinkedHashSet<>();
        if (roles == null || roles.isBlank()) {
            return parsed;
        }
        for (String item : roles.split(",")) {
            String value = item.trim().toUpperCase();
            if (value.isEmpty() || "ALL".equals(value)) {
                continue;
            }
            try {
                parsed.add(Role.valueOf(value));
            } catch (IllegalArgumentException ignored) {
                // Ignore stale role values from older clients.
            }
        }
        return parsed;
    }

    private void sendMessageToRole(Role role, String title, String content, String bizType, String bizId,
                                   User operator, String excludeUserId) {
        for (User user : userMapper.findAdminUsers(role, true, null)) {
            if (!isBlank(excludeUserId) && excludeUserId.equals(user.userId())) {
                continue;
            }
            sendMessage(user.userId(), title, content, bizType, bizId, operator);
        }
    }

    private String normalizeReadStatus(String value) {
        if (isBlank(value)) {
            return null;
        }
        String status = value.trim().toUpperCase();
        return "READ".equals(status) ? "READ" : "UNREAD";
    }

    private FollowupTaskInfo requireFollowupTask(Long id) {
        FollowupTaskInfo task = followupTaskMapper.findById(id);
        if (task == null) {
            throw new BusinessException("Follow-up task does not exist");
        }
        return task;
    }

    private FollowupRefreshResult refreshOverdueTasksInternal(User operator) {
        // 逾期刷新按计划日期批量扫描，既更新任务状态，也为逾期任务生成预警记录。
        List<Long> overdueIds = followupTaskMapper.findPendingOverdueIds(LocalDate.now());
        int updated = 0;
        int warnings = 0;
        LocalDateTime now = LocalDateTime.now();
        for (Long id : overdueIds) {
            FollowupTaskInfo task = requireFollowupTask(id);
            int changed = followupTaskMapper.updateTaskStatus(id, FollowupTaskStatus.OVERDUE, null,
                    false, null, operator == null ? "SYSTEM" : operator.userId(), now);
            if (changed > 0) {
                updated++;
                if (createFollowupWarning(task, "FOLLOWUP_OVERDUE", "MEDIUM",
                        "Follow-up task is overdue. Planned date: " + task.planDate(), operator)) {
                    warnings++;
                }
                sendMessage(task.adopterId(), "回访任务已逾期",
                        "您的 " + task.taskType() + " 回访任务已逾期，请尽快提交反馈或联系志愿者。",
                        "FOLLOWUP_TASK", String.valueOf(id), operator);
                logOperation(operator, "Refresh overdue follow-up task", "FOLLOWUP_TASK", String.valueOf(id),
                        task.status(), FollowupTaskStatus.OVERDUE.name(), String.valueOf(task.planDate()));
            }
        }
        return new FollowupRefreshResult(updated, warnings);
    }

    private boolean createFollowupWarning(FollowupTaskInfo task, String warningType, String warningLevel,
                                          String description, User operator) {
        // 同一个回访任务只保留一条待处理预警，防止反复提交导致预警重复堆积。
        if (warningRecordMapper.countOpenByTaskAndType(task.id(), warningType) > 0) {
            return false;
        }
        String title = switch (warningType) {
            case "FOLLOWUP_OVERDUE" -> "Follow-up overdue: " + task.catName();
            case "FOLLOWUP_ABNORMAL" -> "Abnormal follow-up: " + task.catName();
            default -> "Follow-up warning: " + task.catName();
        };
        String content = "Cat: " + valueOrDefault(task.catName(), task.catId())
                + "; adopter: " + valueOrDefault(task.adopterName(), task.adopterId())
                + "; planDate: " + task.planDate()
                + "; taskType: " + task.taskType()
                + "; detail: " + valueOrDefault(description, "-");
        LocalDateTime now = LocalDateTime.now();
        warningRecordMapper.insert(warningType, warningLevel, "FOLLOWUP_TASK", String.valueOf(task.id()),
                task.catId(), task.adopterId(), task.applicationId(), task.id(), title, content,
                operator == null ? "SYSTEM" : operator.userId(), now);
        logOperation(operator, "Create follow-up warning", "WARNING", String.valueOf(task.id()),
                null, warningType, description);
        return true;
    }

    private void maybeMarkCatAdopted(String catId, User operator) {
        Cat cat = findCat(catId);
        if (cat.status() != CatStatus.FOLLOWING) {
            return;
        }
        if (followupTaskMapper.countByCatAndStatus(catId, FollowupTaskStatus.COMPLETED) < 3) {
            return;
        }
        if (followupTaskMapper.countUnclosedByCat(catId) > 0) {
            return;
        }
        if (warningRecordMapper.countOpenByCat(catId) > 0) {
            return;
        }
        catMapper.updateStatus(catId, CatStatus.ADOPTED, LocalDateTime.now());
        logOperation(operator, "Follow-up closed and cat adopted", "CAT", catId,
                CatStatus.FOLLOWING.name(), CatStatus.ADOPTED.name(), "All 7/30/90 day tasks completed");
    }

    private String normalizeWarningStatus(String value) {
        if (isBlank(value)) {
            throw new BusinessException("Warning target status is required");
        }
        String status = value.trim().toUpperCase();
        if (!List.of("PROCESSING", "HANDLED", "IGNORED").contains(status)) {
            throw new BusinessException("Unsupported warning target status");
        }
        return status;
    }

    private User findUser(String userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private Cat findCat(String catId) {
        Cat cat = catMapper.findById(catId);
        if (cat == null) {
            throw new BusinessException("猫咪档案不存在");
        }
        return cat;
    }

    private Product findProduct(String productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return product;
    }

    private AdoptionApplication findApplication(String applicationId) {
        AdoptionApplication application = applicationMapper.findById(applicationId);
        if (application == null) {
            throw new BusinessException("认养申请不存在");
        }
        return application;
    }

    private ApplicationReviewRow requireApplicationRow(String applicationId) {
        ApplicationReviewRow row = applicationMapper.findReviewRowById(applicationId);
        if (row == null) {
            throw new BusinessException("认养申请不存在");
        }
        return row;
    }

    private ApplicationReviewDetail getApplicationReviewDetail(String applicationId, boolean includeAudits) {
        ApplicationReviewRow row = requireApplicationRow(applicationId);
        if (AuthContext.role() == Role.STUDENT && !row.userId().equals(AuthContext.userId())) {
            throw new BusinessException("只能查看自己的申请");
        }
        return toApplicationReviewDetail(row, includeAudits);
    }

    private ApplicationReviewDetail getMyApplicationReviewDetail(String applicationId, boolean includeAudits) {
        ApplicationReviewRow row = requireApplicationRow(applicationId);
        if (!row.userId().equals(AuthContext.userId())) {
            throw new BusinessException("只能查看自己的申请");
        }
        return toApplicationReviewDetail(row, includeAudits);
    }

    private ApplicationReviewDetail toApplicationReviewDetail(ApplicationReviewRow row, boolean includeAudits) {
        return new ApplicationReviewDetail(row.applicationId(), row.userId(), row.userName(), row.phone(),
                row.catId(), row.catName(), row.coverUrl(), row.livingCondition(), row.petExperience(),
                row.familySupport(), row.costAffordability(), row.acceptFollowup(), row.commitmentText(),
                row.extraReason(), row.score(), row.riskLevel(), row.scoreReasons(), row.status(),
                row.reviewNote(), row.appliedAt(), row.reviewedAt(),
                includeAudits ? adoptionAuditMapper.findByApplicationId(row.applicationId()) : List.of(),
                agreementMapper.findByApplicationId(row.applicationId()),
                followupTaskMapper.findTasks(row.userId(), row.applicationId(), null, null, null));
    }

    private ApplicationReviewDetail auditApplication(ApplicationReviewRow row, String stage, String result,
                                                     String comment, ApplicationStatus nextStatus) {
        User operator = currentUser();
        LocalDateTime now = LocalDateTime.now();
        applicationMapper.updateMisStatus(row.applicationId(), nextStatus, comment, operator.userId(), now);
        adoptionAuditMapper.insert(row.applicationId(), operator.userId(), stage, result, comment,
                row.status().name(), nextStatus.name(), now);
        logOperation(operator, stage.equals("INITIAL") ? "认养申请初审" : "认养申请终审",
                "APPLICATION", row.applicationId(), row.status().name(), nextStatus.name(), comment);
        sendMessage(row.userId(), stage.equals("INITIAL") ? "认养申请初审结果" : "认养申请终审结果",
                "你的认养申请状态已更新为：" + nextStatus.name() + "。审核意见：" + valueOrDefault(comment, ""),
                "APPLICATION", row.applicationId(), operator);
        if ("INITIAL".equals(stage)) {
            sendMessage(operator.userId(), "感谢完成认养初审",
                    "你已完成申请 " + row.applicationId() + " 的初审，系统已记录处理结果。",
                    "APPLICATION", row.applicationId(), operator);
            if (nextStatus == ApplicationStatus.PENDING_FINAL) {
                sendMessageToRole(Role.ADMIN, "新的认养终审任务",
                        "申请 " + row.applicationId() + " 已通过初审，等待管理员终审。",
                        "APPLICATION", row.applicationId(), operator, null);
            }
        } else if ("FINAL".equals(stage)) {
            sendMessage(operator.userId(), "感谢完成认养终审",
                    "你已完成申请 " + row.applicationId() + " 的终审，系统已记录处理结果。",
                    "APPLICATION", row.applicationId(), operator);
        }
        return getApplicationReviewDetail(row.applicationId(), true);
    }

    private Clue findClue(String clueId) {
        Clue clue = rescueReportMapper.findClueById(clueId);
        if (clue == null) {
            throw new BusinessException("线索不存在");
        }
        return clue;
    }

    private void ensureCanVerifyClue(Clue clue) {
        if (ClueStatus.CREATED_CAT.name().equals(clue.status())) {
            throw new BusinessException("线索已建档，不能重复核实");
        }
        if (ClueStatus.DUPLICATE.name().equals(clue.status())) {
            throw new BusinessException("重复线索不能再次核实");
        }
        if (ClueStatus.INVALID.name().equals(clue.status())) {
            throw new BusinessException("无效线索不能再次核实");
        }
    }

    private void ensureCanCreateCatFromClue(Clue clue) {
        if (ClueStatus.CREATED_CAT.name().equals(clue.status()) || clue.createdCatId() != null) {
            throw new BusinessException("线索已建档，不能重复生成");
        }
        if (ClueStatus.DUPLICATE.name().equals(clue.status())) {
            throw new BusinessException("重复线索不能生成猫咪档案");
        }
        if (ClueStatus.INVALID.name().equals(clue.status())) {
            throw new BusinessException("无效线索不能生成猫咪档案");
        }
        if (!ClueStatus.VERIFIED_VALID.name().equals(clue.status())) {
            throw new BusinessException("只有已核实有效线索可以生成猫咪档案");
        }
    }

    private void validateRegisterRequest(RegisterRequest request) {
        DataQualityValidator.requireCleanText("用户姓名", request.userName(), 3, 30);
        DataQualityValidator.requireCleanText("学号或工号", request.schoolNo(), 2, 30);
        DataQualityValidator.requireCleanText("学院或单位", request.college(), 2, 80);
        DataQualityValidator.optionalCleanText("养宠经验", request.petExperience(), 2, 300);
        if (request.password() == null || request.password().length() < 6) {
            throw new BusinessException("密码长度至少 6 位");
        }
        if (request.role() != null && request.role() != Role.STUDENT) {
            throw new BusinessException("前台注册只能创建普通用户");
        }
    }

    private void validateProfileRequest(ProfileUpdateRequest request) {
        DataQualityValidator.requireCleanText("用户姓名", request.userName(), 3, 30);
        DataQualityValidator.requireCleanText("学院或单位", request.college(), 2, 80);
        DataQualityValidator.optionalCleanText("养宠经验", request.petExperience(), 2, 300);
        if (request.phone() == null || !request.phone().matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }
    }

    private void validateAdminUserRequest(AdminUserRequest request, boolean creating) {
        DataQualityValidator.requireCleanText("用户姓名", request.userName(), 2, 30);
        DataQualityValidator.requireCleanText("登录账号", request.schoolNo(), 2, 30);
        DataQualityValidator.requireCleanText("学院或单位", request.college(), 2, 80);
        DataQualityValidator.optionalCleanText("养宠经验", request.petExperience(), 2, 300);
        DataQualityValidator.optionalCleanText("身份证号", request.idCard(), 6, 30);
        if (creating && (request.password() == null || request.password().length() < 6)) {
            throw new BusinessException("密码长度至少 6 位");
        }
        if (request.phone() == null || !request.phone().matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }
        if (request.role() == null) {
            throw new BusinessException("用户角色不能为空");
        }
    }

    private void validateClueRequest(ClueSubmitRequest request) {
        DataQualityValidator.requireCleanText("发现地点", request.foundLocation(), 2, 100);
        DataQualityValidator.optionalCleanText("校园区域", request.foundArea(), 2, 80);
        DataQualityValidator.requireCleanText("描述", request.description(), 5, 500);
        DataQualityValidator.requireEnumValue("紧急程度", request.urgencyLevel(), "NORMAL", "HIGH", "URGENT");
    }

    private void validateCatRequest(CatRequest request, boolean creating) {
        DataQualityValidator.requireCleanText("猫咪名称", request.catName(), 1, 20);
        DataQualityValidator.requireCleanText("发现地点", request.foundPlace(), 2, 100);
        DataQualityValidator.requireCleanText("毛色", request.color(), 1, 30);
        DataQualityValidator.requireCleanText("年龄估计", request.ageEstimate(), 1, 30);
        DataQualityValidator.optionalCleanText("性格描述", request.personality(), 2, 200);
        DataQualityValidator.optionalCleanText("猫咪描述", request.description(), 2, 500);
        if (request.gender() == null || !request.gender().matches("(?i)M|F|U")) {
            throw new BusinessException("猫咪性别不合法");
        }
        if (creating && request.status() != null && request.status() != CatStatus.OBSERVING) {
            throw new BusinessException("新增猫咪状态必须从观察中开始");
        }
        if (request.tags() != null) {
            request.tags().forEach(tag -> DataQualityValidator.optionalCleanText("标签", tag, 1, 40));
        }
    }

    private void validateAdoptionApplicationRequest(AdoptionApplicationRequest request) {
        DataQualityValidator.requireCleanText("居住条件", request.livingCondition(), 5, 300);
        DataQualityValidator.requireCleanText("养宠经验", request.petExperience(), 2, 300);
        DataQualityValidator.requireCleanText("家庭支持", request.familySupport(), 2, 300);
        DataQualityValidator.requireCleanText("经济能力", request.costAffordability(), 2, 300);
        DataQualityValidator.requireCleanText("认养承诺", request.commitmentText(), 10, 500);
        DataQualityValidator.optionalCleanText("申请理由", request.extraReason(), 2, 500);
    }

    private void validateAdminMedicalRequest(AdminMedicalRecordRequest request) {
        DataQualityValidator.requireEnumValue("记录类型", request.recordType(), "CHECKUP", "VACCINE", "STERILIZATION", "TREATMENT", "OTHER");
        DataQualityValidator.requireEnumValue("健康结果", request.healthResult(), "HEALTHY", "OBSERVE", "SICK", "SERIOUS", "A", "B", "C");
        DataQualityValidator.requireCleanText("医疗说明", request.description(), 5, 500);
        DataQualityValidator.requireNonNegative("费用", request.cost());
        DataQualityValidator.requireReasonableDate("记录日期", request.recordDate());
    }

    private void validateFollowupSubmitRequest(FollowupRecordSubmitRequest request) {
        DataQualityValidator.requireCleanText("回访内容", request.content(), 5, 500);
        DataQualityValidator.requireCleanText("猫咪状态描述", request.catCondition(), 2, 500);
        DataQualityValidator.requireCleanText("环境描述", request.environmentDesc(), 2, 500);
        if (request.abnormalFlag()) {
            DataQualityValidator.requireCleanText("异常说明", request.abnormalDesc(), 5, 500);
        } else {
            DataQualityValidator.optionalCleanText("异常说明", request.abnormalDesc(), 5, 500);
        }
    }

    private Cat buildCat(String catId, CatRequest request, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Cat(catId, cleanCatName(request.catName(), "未命名"), request.foundPlace(),
                request.foundDate() == null ? LocalDate.now() : request.foundDate(), valueOrDefault(request.gender(), "U"),
                request.color(), request.ageEstimate(), request.personality(), request.healthLevel(), request.sterilized(),
                request.vaccinated(), request.status(), request.coverUrl(), request.tags() == null ? List.of() : request.tags(),
                request.description(), createdAt, updatedAt);
    }

    private CatRequest forceNewCatObserving(CatRequest request) {
        return new CatRequest(request.catName(), request.foundPlace(), request.foundDate(), request.gender(),
                request.color(), request.ageEstimate(), request.personality(), request.healthLevel(),
                request.sterilized(), request.vaccinated(), CatStatus.OBSERVING, request.coverUrl(),
                request.tags(), request.description());
    }

    private String nextCatId() {
        String prefix = "CAT" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        return codeGenerator.nextAfter("CAT", catMapper.maxSuffixByPrefix(prefix));
    }

    private String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private void replaceCatTags(String catId, List<String> tags) {
        catMapper.deleteTags(catId);
        if (tags == null) {
            return;
        }
        tags.stream()
                .filter(tag -> tag != null && !tag.isBlank())
                .map(String::trim)
                .distinct()
                .forEach(tag -> catMapper.insertTag(catId, tag));
    }

    private void replaceArticleTags(Integer articleId, String tags) {
        communityMapper.deleteArticleTags(articleId);
        if (isBlank(tags)) {
            return;
        }
        for (String tag : tags.split(",")) {
            String cleaned = tag.trim();
            if (!cleaned.isEmpty()) {
                communityMapper.insertArticleTag(articleId, cleaned);
            }
        }
    }

    private String cleanCatName(String value, String fallback) {
        String cleaned = valueOrDefault(value, fallback).trim();
        if (isDirtyPlaceholderName(cleaned)) {
            return fallback;
        }
        return cleaned;
    }

    private boolean isDirtyPlaceholderName(String value) {
        String normalized = value.replaceAll("\\s+", "");
        return normalized.equals("?")
                || normalized.contains("???")
                || normalized.toLowerCase().startsWith("codex");
    }

    private void replaceNoticeTargetRoles(String noticeId, String targetRoles) {
        // 物理表已拆分为公告主表和角色关联表，这里负责把前端逗号字符串落到关联表。
        noticeMapper.deleteTargetRoles(noticeId);
        if (isBlank(targetRoles)) {
            return;
        }
        for (String role : targetRoles.split(",")) {
            String cleaned = role.trim();
            if (!cleaned.isEmpty()) {
                noticeMapper.insertTargetRole(noticeId, cleaned);
            }
        }
    }

    private void validateCatStatusTransition(Cat cat, CatStatus targetStatus) {
        if (cat.status() == targetStatus) {
            return;
        }
        boolean allowed = switch (cat.status()) {
            case PENDING_VERIFY -> targetStatus == CatStatus.OBSERVING || targetStatus == CatStatus.MEDICAL;
            case OBSERVING -> targetStatus == CatStatus.MEDICAL || targetStatus == CatStatus.ADOPTABLE || targetStatus == CatStatus.SUSPENDED;
            case MEDICAL -> targetStatus == CatStatus.OBSERVING || targetStatus == CatStatus.ADOPTABLE || targetStatus == CatStatus.SUSPENDED;
            case ADOPTABLE -> targetStatus == CatStatus.SUSPENDED;
            case SUSPENDED -> targetStatus == CatStatus.OBSERVING;
            default -> false;
        };
        if (!allowed) {
            throw new BusinessException("当前状态不能流转为：" + targetStatus.getLabel());
        }
    }

    private void validatePublishable(Cat cat) {
        if (cat.status() == CatStatus.ADOPTED || cat.status() == CatStatus.FOLLOWING || cat.status() == CatStatus.RETURN_PENDING) {
            throw new BusinessException("当前状态不能发布认养");
        }
        if (isBlank(cat.catName()) || isBlank(cat.foundPlace()) || isBlank(cat.gender()) || isBlank(cat.color())
                || isBlank(cat.ageEstimate()) || isBlank(cat.personality()) || isBlank(cat.coverUrl())) {
            throw new BusinessException("猫咪基础信息不完整，不能发布认养");
        }
        if (medicalRecordMapper.countByCatId(cat.catId()) == 0) {
            throw new BusinessException("缺少健康记录，不能发布认养");
        }
        if (cat.healthLevel() == HealthLevel.C) {
            throw new BusinessException("猫咪健康状态仍需治疗，不能发布认养");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private HealthLevel mapHealthResult(String healthResult) {
        if (healthResult == null) {
            return HealthLevel.B;
        }
        return switch (healthResult.trim().toUpperCase()) {
            case "HEALTHY", "A" -> HealthLevel.A;
            case "SICK", "SERIOUS", "C" -> HealthLevel.C;
            default -> HealthLevel.B;
        };
    }

    private boolean mapVaccineStatus(String vaccineStatus, boolean current) {
        if (vaccineStatus == null || vaccineStatus.isBlank()) {
            return current;
        }
        return "VACCINATED".equalsIgnoreCase(vaccineStatus) || "TRUE".equalsIgnoreCase(vaccineStatus);
    }

    private boolean mapSterilizedStatus(String sterilizedStatus, boolean current) {
        if (sterilizedStatus == null || sterilizedStatus.isBlank()) {
            return current;
        }
        return "STERILIZED".equalsIgnoreCase(sterilizedStatus) || "TRUE".equalsIgnoreCase(sterilizedStatus);
    }

    private String normalizeRecordType(String recordType) {
        if (recordType == null || recordType.isBlank()) {
            return "CHECKUP";
        }
        return switch (recordType.trim().toUpperCase()) {
            case "VACCINE", "STERILIZATION", "TREATMENT", "OTHER" -> recordType.trim().toUpperCase();
            default -> "CHECKUP";
        };
    }

    private ApplicationScore scoreApplication(AdoptionApplicationRequest request) {
        // 申请评分用于辅助初审：根据居住、经验、回访承诺等字段计算风险等级和评分原因。
        int score = 0;
        List<String> reasons = new ArrayList<>();
        if (positive(request.petExperience())) {
            score += 20;
            reasons.add("有养宠经验 +20");
        }
        if (stableLiving(request.livingCondition())) {
            score += 20;
            reasons.add("居住条件稳定 +20");
        }
        if (request.acceptFollowup()) {
            score += 20;
            reasons.add("接受定期回访 +20");
        }
        if (supportive(request.familySupport())) {
            score += 15;
            reasons.add("家庭成员支持 +15");
        }
        if (request.commitmentAccepted() && !isBlank(request.commitmentText())) {
            score += 15;
            reasons.add("明确承诺不弃养 +15");
        }
        if (canAfford(request.costAffordability())) {
            score += 10;
            reasons.add("可承担医疗等费用 +10");
        }
        if (isIncomplete(request)) {
            score -= 10;
            reasons.add("申请信息不完整 -10");
        }
        String merged = (request.livingCondition() + " " + request.petExperience() + " " + request.familySupport()
                + " " + request.costAffordability() + " " + request.extraReason()).toLowerCase();
        if (!request.acceptFollowup() || containsAny(merged, "不接受回访", "宿舍", "违规", "经常搬家", "家人反对", "冲动")) {
            score -= 20;
            reasons.add("存在风险描述 -20");
        }
        score = Math.max(0, Math.min(100, score));
        String risk = score >= 80 ? "LOW" : score >= 60 ? "MEDIUM" : "HIGH";
        return new ApplicationScore(score, risk, String.join("；", reasons));
    }

    private void saveApplicationScoreReasons(String applicationId, String reasons) {
        if (isBlank(reasons)) {
            return;
        }
        String[] parts = reasons.split("；");
        for (int i = 0; i < parts.length; i++) {
            String reason = parts[i].trim();
            if (!reason.isEmpty()) {
                applicationMapper.insertScoreReason(applicationId, i + 1, reason, parseScoreDelta(reason));
            }
        }
    }

    private Integer parseScoreDelta(String reason) {
        int plus = reason.lastIndexOf('+');
        int minus = reason.lastIndexOf('-');
        int index = Math.max(plus, minus);
        if (index < 0 || index == reason.length() - 1) {
            return null;
        }
        try {
            int value = Integer.parseInt(reason.substring(index + 1).trim());
            return reason.charAt(index) == '-' ? -value : value;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private boolean positive(String value) {
        return !isBlank(value) && containsAny(value, "有", "养过", "经验", "多年", "照顾");
    }

    private boolean stableLiving(String value) {
        return !isBlank(value) && containsAny(value, "稳定", "自有", "租期", "长期", "封窗", "家");
    }

    private boolean supportive(String value) {
        return !isBlank(value) && containsAny(value, "支持", "同意", "接受", "一起");
    }

    private boolean canAfford(String value) {
        return !isBlank(value) && containsAny(value, "可以", "能", "稳定", "承担", "预算", "收入");
    }

    private boolean isIncomplete(AdoptionApplicationRequest request) {
        return request.livingCondition().length() < 6 || request.petExperience().length() < 4
                || request.familySupport().length() < 4 || request.costAffordability().length() < 4;
    }

    private boolean containsAny(String value, String... words) {
        if (value == null) {
            return false;
        }
        for (String word : words) {
            if (value.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeClueStatusFilter(String status) {
        return status == null || status.isBlank() ? null : status;
    }

    private String normalizeUrgencyFilter(String urgencyLevel) {
        return urgencyLevel == null || urgencyLevel.isBlank() ? null : normalizeUrgency(urgencyLevel);
    }

    private String normalizeUrgency(String urgencyLevel) {
        if (urgencyLevel == null || urgencyLevel.isBlank()) {
            return "NORMAL";
        }
        String value = urgencyLevel.trim().toUpperCase();
        return switch (value) {
            case "URGENT", "紧急" -> "URGENT";
            case "HIGH", "较急" -> "HIGH";
            default -> "NORMAL";
        };
    }

    private boolean isPendingClueStatus(String status) {
        return ClueStatus.PENDING_VERIFY.name().equals(status)
                || "待核实".equals(status)
                || "待紧急核实".equals(status);
    }

    private double rate(long numerator, long denominator) {
        if (denominator == 0) {
            return 0;
        }
        return BigDecimal.valueOf(numerator * 100.0 / denominator).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private void log(String operator, String action, String targetType, String targetId, String detail) {
        String prefix = "LG" + LocalDate.now().format(SHORT_DATE);
        String logId = codeGenerator.nextAfter("LG", auditLogMapper.maxSuffixByPrefix(prefix));
        auditLogMapper.insert(new AuditLog(logId, operator, action, targetType, targetId, detail, LocalDateTime.now()));
    }

    private void logOperation(User operator, String action, String targetType, String targetId,
                              String beforeData, String afterData, String remark) {
        // 操作日志统一记录关键业务变更，便于后台审计和毕业设计演示流程追踪。
        LocalDateTime now = LocalDateTime.now();
        String operatorName = operator == null ? "系统" : operator.userName();
        String operatorId = operator == null ? null : operator.userId();
        log(operatorName, action, targetType, targetId,
                "before=" + valueOrDefault(beforeData, "-") + "; after=" + valueOrDefault(afterData, "-") + "; " + valueOrDefault(remark, ""));
        operationLogMapper.insert(operatorId, operatorName, action, targetType, targetId, beforeData, afterData, remark, now);
    }
}
