const { createApp } = Vue;

const ROLE_LABELS = {
    STUDENT: "学生用户",
    VOLUNTEER: "志愿者",
    HOSPITAL: "合作医院 / 医疗协作用户",
    HOSPITAL_USER: "合作医院 / 医疗协作用户",
    MEDICAL: "合作医院 / 医疗协作用户",
    DOCTOR: "合作医院 / 医疗协作用户",
    PARTNER_HOSPITAL: "合作医院 / 医疗协作用户",
    ADMIN: "管理员"
};

function normalizeRole(role) {
    const value = String(role || "").trim().toUpperCase();
    if (["HOSPITAL", "HOSPITAL_USER", "MEDICAL", "DOCTOR", "PARTNER_HOSPITAL"].includes(value)
            || role === "合作医院" || role === "医疗协作用户" || role === "医院用户") {
        return "HOSPITAL";
    }
    return value || "GUEST";
}

createApp({
    data() {
        const savedAuth = JSON.parse(localStorage.getItem("hfut-cat-auth") || "null");
        return {
            auth: savedAuth,
            authMode: "login",
            authForm: {
                account: "",
                password: "",
                userName: "",
                schoolNo: "",
                phone: "",
                idCard: "",
                college: "",
                petExperience: "",
                role: "STUDENT"
            },
            profileForm: {
                userName: "",
                phone: "",
                college: "",
                petExperience: ""
            },
            passwordForm: {
                oldPassword: "",
                newPassword: ""
            },
            keyword: "",
            statusFilter: "",
            recognitionFile: null,
            recognitionPreview: "",
            recognitionCandidates: [],
            selectedPhotos: [],
            stats: {},
            cats: [],
            allCats: [],
            reports: [],
            applications: [],
            myApplications: [],
            myFavorites: [],
            medicalRecords: [],
            medicalCatFilter: "",
            followups: [],
            followupResultFilter: "",
            notices: [],
            noticeDetail: null,
            logs: [],
            locations: [],
            products: [],
            adminProducts: [],
            orders: [],
            donations: [],
            donationChannel: null,
            donationForm: {
                amount: 20,
                donorMessage: "愿校园猫咪都被好好照顾。"
            },
            productForm: {
                productId: "",
                productName: "",
                category: "文创",
                price: 19.9,
                imageUrl: "/uploads/catalog/cat-postcard.jpg",
                description: "",
                payUrl: "",
                stock: 20,
                status: true
            },
            communityKeyword: "",
            articles: [],
            posts: [],
            comments: [],
            collects: [],
            selectedArticle: null,
            articleForm: {
                articleId: "",
                title: "",
                typeName: "科普",
                coverUrl: "/uploads/cats/cat_03_01.jpg",
                summary: "",
                content: "",
                source: "",
                sourceUrl: "",
                tags: "校园猫咪",
                published: true,
                pinned: false
            },
            postForm: {
                typeName: "照护交流",
                title: "",
                content: "",
                coverUrl: ""
            },
            commentForm: {
                sourceType: "ARTICLE",
                sourceId: "",
                replyToId: null,
                content: ""
            },
            toastMessage: "",
            applicationForm: {
                catId: "",
                housingInfo: "校外稳定租住，房东允许养猫，窗户已安装纱窗。",
                familyAttitude: "家人和室友均已沟通同意。",
                petExperience: "有照顾猫咪经验，了解疫苗、驱虫和绝育要求。",
                economicAbility: "可承担猫粮、猫砂、医疗与假期寄养费用。",
                promiseAccepted: true
            },
            reportForm: {
                reporterName: "",
                reporterPhone: "",
                foundPlace: "",
                color: "",
                gender: "U",
                healthDescription: "",
                photoUrl: "/uploads/cats/no-photo.svg",
                urgent: false
            },
            catForm: {
                catId: "",
                catName: "",
                foundPlace: "",
                foundDate: "",
                gender: "U",
                color: "",
                ageEstimate: "",
                personality: "",
                healthLevel: "A",
                sterilized: false,
                vaccinated: false,
                status: "OBSERVING",
                coverUrl: "/uploads/cats/no-photo.svg",
                tagsText: "",
                description: ""
            },
            photoForm: {
                catId: "",
                photoUrl: "/uploads/cats/no-photo.svg",
                angleCode: "FRONT",
                photoScene: "正脸",
                cover: false,
                recognitionWeight: 1,
                featureNote: ""
            },
            medicalForm: {
                catId: "",
                checkDate: "",
                hospital: "合肥合作动物医院",
                healthLevel: "A",
                vaccinated: true,
                sterilized: false,
                treatment: "常规体检、驱虫。",
                doctorNote: "状态稳定，建议继续观察。"
            },
            followupForm: {
                applicationId: "",
                method: "线上",
                catCondition: "精神、食欲正常。",
                environmentDescription: "居住环境干净，门窗防护到位。",
                result: "NORMAL",
                photoUrl: "",
                suggestion: "按月继续回访。",
                operatorName: ""
            },
            noticeForm: {
                title: "",
                content: "",
                publisher: "",
                pinned: false,
                enabled: true
            },
            catStatusFilters: [
                { value: "", label: "全部" },
                { value: "ADOPTABLE", label: "可认养" },
                { value: "RESERVED", label: "待交接" },
                { value: "OBSERVING", label: "观察中" },
                { value: "MEDICAL", label: "医疗中" },
                { value: "ADOPTED", label: "已认养" }
            ]
        };
    },
    computed: {
        token() {
            return this.auth?.token || "";
        },
        user() {
            return this.auth?.user || null;
        },
        isLoggedIn() {
            return Boolean(this.token && this.user);
        },
        role() {
            return normalizeRole(this.user?.role);
        },
        roleLabel() {
            return ROLE_LABELS[this.role] || "访客";
        },
        canManageCats() {
            return this.role === "VOLUNTEER" || this.role === "ADMIN";
        },
        canMedical() {
            return this.role === "HOSPITAL" || this.role === "ADMIN";
        },
        canAdmin() {
            return this.role === "ADMIN";
        }
    },
    mounted() {
        this.prefillUserForms();
        this.refreshPublic();
        this.loadShop();
        this.loadCommunity();
        if (this.isLoggedIn) this.refreshPrivate();
    },
    methods: {
        async api(path, options = {}) {
            const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
            if (this.token) headers.Authorization = `Bearer ${this.token}`;
            const response = await fetch(path, { ...options, headers });
            const payload = await response.json();
            if (!response.ok || !payload.success) {
                if ([401, 403].includes(response.status)) this.clearAuth();
                throw new Error(payload.message || "操作失败");
            }
            return payload.data;
        },
        saveAuth(result) {
            this.auth = result;
            localStorage.setItem("hfut-cat-auth", JSON.stringify(result));
            this.prefillUserForms();
        },
        clearAuth() {
            this.auth = null;
            localStorage.removeItem("hfut-cat-auth");
            this.reports = [];
            this.applications = [];
            this.myApplications = [];
            this.myFavorites = [];
            this.orders = [];
            this.donations = [];
            this.collects = [];
            this.logs = [];
        },
        prefillUserForms() {
            if (!this.user) return;
            this.profileForm = {
                userName: this.user.userName || "",
                phone: this.user.phone || "",
                college: this.user.college || "",
                petExperience: this.user.petExperience || ""
            };
            this.reportForm.reporterName = this.user.userName || "";
            this.reportForm.reporterPhone = this.user.phone || "";
            this.followupForm.operatorName = this.user.userName || "";
            this.noticeForm.publisher = this.user.userName || "";
        },
        async login() {
            try {
                const result = await this.api("/api/users/login", {
                    method: "POST",
                    body: JSON.stringify({ account: this.authForm.account, password: this.authForm.password })
                });
                this.saveAuth(result);
                await this.refreshPrivate();
                this.showToast(`欢迎回来，${result.user.userName}`);
                location.hash = "#user-center";
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async register() {
            try {
                await this.api("/api/users", {
                    method: "POST",
                    body: JSON.stringify({
                        userName: this.authForm.userName,
                        schoolNo: this.authForm.schoolNo,
                        password: this.authForm.password,
                        phone: this.authForm.phone,
                        idCard: this.authForm.idCard,
                        college: this.authForm.college,
                        petExperience: this.authForm.petExperience,
                        role: this.authForm.role
                    })
                });
                this.authForm.account = this.authForm.schoolNo;
                this.authMode = "login";
                this.showToast("注册成功，请登录");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async logout(show = true) {
            try {
                if (this.isLoggedIn) await this.api("/api/users/logout", { method: "POST" });
            } catch (error) {
                // Local cleanup still happens if the token has already expired.
            }
            this.clearAuth();
            if (show) this.showToast("已安全退出");
        },
        async loadMe() {
            const user = await this.api("/api/users/me");
            this.auth = { ...this.auth, user };
            localStorage.setItem("hfut-cat-auth", JSON.stringify(this.auth));
            this.prefillUserForms();
        },
        async updateProfile() {
            try {
                const user = await this.api("/api/users/me", {
                    method: "PATCH",
                    body: JSON.stringify(this.profileForm)
                });
                this.auth = { ...this.auth, user };
                localStorage.setItem("hfut-cat-auth", JSON.stringify(this.auth));
                this.prefillUserForms();
                this.showToast("个人资料已更新");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async updatePassword() {
            try {
                await this.api("/api/users/me/password", {
                    method: "PATCH",
                    body: JSON.stringify(this.passwordForm)
                });
                this.passwordForm.oldPassword = "";
                this.passwordForm.newPassword = "";
                this.clearAuth();
                this.showToast("密码已更新，请重新登录");
                location.hash = "#/login";
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async loadMyApplications() {
            this.myApplications = await this.api("/api/users/me/applications");
        },
        async loadMyFavorites() {
            this.myFavorites = await this.api("/api/users/me/favorites");
        },
        async withdrawApplication(applicationId) {
            try {
                await this.api(`/api/applications/${applicationId}/withdraw`, { method: "PATCH" });
                await Promise.all([this.loadMyApplications(), this.loadCats()]);
                this.showToast("申请已撤回");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async refreshPublic() {
            await Promise.all([this.loadStats(), this.loadCats(), this.loadNotices(), this.loadLocations(), this.loadCommunity()]);
        },
        async refreshPrivate() {
            const tasks = [this.loadMe(), this.loadMyApplications(), this.loadMyFavorites(), this.loadOrders(), this.loadDonations(), this.loadCollects()];
            if (this.canManageCats) tasks.push(this.loadReports(), this.loadApplications(), this.loadFollowups());
            if (this.canMedical) tasks.push(this.loadMedicalRecords());
            if (this.canAdmin) tasks.push(this.loadLogs(), this.loadAdminProducts(), this.loadAllOrders(), this.loadAllDonations());
            await Promise.all(tasks);
        },
        async refreshAll() {
            await this.refreshPublic();
            if (this.isLoggedIn) await this.refreshPrivate();
        },
        async loadStats() {
            this.stats = await this.api("/api/dashboard/stats");
        },
        async loadCats() {
            const params = new URLSearchParams();
            if (this.statusFilter) params.set("status", this.statusFilter);
            if (this.keyword.trim()) params.set("keyword", this.keyword.trim());
            this.cats = await this.api(`/api/cats?${params.toString()}`);
            if (!this.statusFilter && !this.keyword.trim()) this.allCats = this.cats;
        },
        async loadReports() {
            this.reports = await this.api("/api/reports");
        },
        async loadApplications() {
            this.applications = await this.api("/api/applications/details");
        },
        async loadMedicalRecords() {
            const params = new URLSearchParams();
            if (this.medicalCatFilter.trim()) params.set("catId", this.medicalCatFilter.trim());
            this.medicalRecords = await this.api(`/api/medical-records?${params.toString()}`);
        },
        async loadFollowups() {
            const params = new URLSearchParams();
            if (this.followupForm.applicationId.trim()) params.set("applicationId", this.followupForm.applicationId.trim());
            if (this.followupResultFilter) params.set("result", this.followupResultFilter);
            this.followups = await this.api(`/api/followups?${params.toString()}`);
        },
        async loadNotices() {
            this.notices = await this.api("/api/notices");
        },
        async loadAdminNotices() {
            this.notices = await this.api("/api/notices?includeDisabled=true");
        },
        async viewNotice(noticeId) {
            this.noticeDetail = await this.api(`/api/notices/${noticeId}`);
        },
        async loadLogs() {
            this.logs = await this.api("/api/dashboard/logs");
        },
        async loadLocations() {
            this.locations = await this.api("/api/dashboard/locations");
        },
        async loadShop() {
            this.products = await this.api("/api/shop/products");
            this.donationChannel = await this.api("/api/shop/donation-channel");
        },
        async loadAdminProducts() {
            this.adminProducts = await this.api("/api/shop/admin/products");
        },
        async loadOrders() {
            if (this.isLoggedIn) this.orders = await this.api("/api/shop/orders");
        },
        async loadAllOrders() {
            this.orders = await this.api("/api/shop/admin/orders");
        },
        async loadDonations() {
            if (this.isLoggedIn) this.donations = await this.api("/api/shop/donations");
        },
        async loadAllDonations() {
            this.donations = await this.api("/api/shop/admin/donations");
        },
        async loadCommunity() {
            const params = new URLSearchParams();
            if (this.communityKeyword.trim()) params.set("keyword", this.communityKeyword.trim());
            const query = params.toString();
            this.articles = await this.api(`/api/community/articles${query ? `?${query}` : ""}`);
            this.posts = await this.api(`/api/community/posts${query ? `?${query}` : ""}`);
        },
        async loadCollects() {
            if (this.isLoggedIn) this.collects = await this.api("/api/community/collects");
        },
        async setStatus(value) {
            this.statusFilter = value;
            await this.loadCats();
        },
        requireLogin() {
            if (!this.isLoggedIn) {
                this.showToast("请先登录");
                location.hash = "#/login";
                return false;
            }
            return true;
        },
        async submitApplication() {
            if (!this.requireLogin()) return;
            try {
                await this.api("/api/applications", {
                    method: "POST",
                    body: JSON.stringify(this.applicationForm)
                });
                await Promise.all([this.refreshAll(), this.loadMyApplications()]);
                this.showToast("认养申请已提交");
                location.hash = "#user-center";
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async submitReport() {
            if (!this.requireLogin()) return;
            try {
                await this.api("/api/reports", { method: "POST", body: JSON.stringify(this.reportForm) });
                await this.refreshAll();
                this.showToast("发现上报已提交");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async saveCat() {
            try {
                const body = {
                    catName: this.catForm.catName,
                    foundPlace: this.catForm.foundPlace,
                    foundDate: this.catForm.foundDate || null,
                    gender: this.catForm.gender,
                    color: this.catForm.color,
                    ageEstimate: this.catForm.ageEstimate,
                    personality: this.catForm.personality,
                    healthLevel: this.catForm.healthLevel,
                    sterilized: this.catForm.sterilized,
                    vaccinated: this.catForm.vaccinated,
                    status: this.catForm.status,
                    coverUrl: this.catForm.coverUrl,
                    tags: this.catForm.tagsText.split(",").map(item => item.trim()).filter(Boolean),
                    description: this.catForm.description
                };
                const catId = this.catForm.catId.trim();
                await this.api(catId ? `/api/cats/${catId}` : "/api/cats", {
                    method: catId ? "PUT" : "POST",
                    body: JSON.stringify(body)
                });
                await this.refreshAll();
                this.showToast("猫咪档案已保存");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        editCat(cat) {
            this.catForm = {
                catId: cat.catId,
                catName: cat.catName || "",
                foundPlace: cat.foundPlace || "",
                foundDate: cat.foundDate || "",
                gender: cat.gender || "U",
                color: cat.color || "",
                ageEstimate: cat.ageEstimate || "",
                personality: cat.personality || "",
                healthLevel: cat.healthLevel || "A",
                sterilized: Boolean(cat.sterilized),
                vaccinated: Boolean(cat.vaccinated),
                status: cat.status || "OBSERVING",
                coverUrl: cat.coverUrl || "/uploads/cats/no-photo.svg",
                tagsText: (cat.tags || []).join(","),
                description: cat.description || ""
            };
            location.hash = "#workbench";
        },
        async deleteCat(catId) {
            try {
                await this.api(`/api/cats/${catId}`, { method: "DELETE" });
                await this.refreshAll();
                this.showToast("猫咪档案已删除");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async addPhoto() {
            try {
                await this.api("/api/cats/photos", {
                    method: "POST",
                    body: JSON.stringify({ ...this.photoForm, recognitionWeight: Number(this.photoForm.recognitionWeight) })
                });
                this.selectedPhotos = await this.api(`/api/cats/${this.photoForm.catId}/photos`);
                await this.loadCats();
                this.showToast("识别照片样本已保存");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async deletePhoto(photoId) {
            try {
                await this.api(`/api/cats/photos/${photoId}`, { method: "DELETE" });
                if (this.photoForm.catId) this.selectedPhotos = await this.api(`/api/cats/${this.photoForm.catId}/photos`);
                this.showToast("照片样本已删除");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async addMedical() {
            try {
                await this.api("/api/medical-records", {
                    method: "POST",
                    body: JSON.stringify({ ...this.medicalForm, checkDate: this.medicalForm.checkDate || null })
                });
                await this.refreshAll();
                await this.loadMedicalRecords();
                this.showToast("医疗记录已保存");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async addFollowup() {
            try {
                await this.api("/api/followups", { method: "POST", body: JSON.stringify(this.followupForm) });
                await this.refreshAll();
                await this.loadFollowups();
                this.showToast("回访记录已保存");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async createNotice() {
            try {
                await this.api("/api/notices", { method: "POST", body: JSON.stringify(this.noticeForm) });
                this.noticeForm.title = "";
                this.noticeForm.content = "";
                await this.loadAdminNotices();
                this.showToast("公告已发布");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        editNotice(notice) {
            this.noticeForm = {
                title: notice.title,
                content: notice.content,
                publisher: notice.publisher,
                pinned: Boolean(notice.pinned),
                enabled: Boolean(notice.enabled)
            };
            this.noticeForm.noticeId = notice.noticeId;
        },
        async updateNotice() {
            try {
                if (!this.noticeForm.noticeId) {
                    this.showToast("请选择要编辑的公告");
                    return;
                }
                await this.api(`/api/notices/${this.noticeForm.noticeId}`, {
                    method: "PUT",
                    body: JSON.stringify(this.noticeForm)
                });
                await this.loadAdminNotices();
                this.showToast("公告已更新");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async toggleNoticeEnabled(notice) {
            try {
                await this.api(`/api/notices/${notice.noticeId}/enabled?enabled=${!notice.enabled}`, { method: "PATCH" });
                await this.loadAdminNotices();
                this.showToast(notice.enabled ? "公告已下架" : "公告已上架");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async toggleNoticePinned(notice) {
            try {
                await this.api(`/api/notices/${notice.noticeId}/pinned?pinned=${!notice.pinned}`, { method: "PATCH" });
                await this.loadAdminNotices();
                this.showToast(notice.pinned ? "已取消置顶" : "公告已置顶");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async deleteNotice(noticeId) {
            try {
                await this.api(`/api/notices/${noticeId}`, { method: "DELETE" });
                await this.loadAdminNotices();
                this.showToast("公告已删除");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        editProduct(product) {
            this.productForm = { ...product };
        },
        async saveProduct() {
            try {
                const body = {
                    ...this.productForm,
                    price: Number(this.productForm.price),
                    stock: Number(this.productForm.stock)
                };
                await this.api(this.productForm.productId ? `/api/shop/admin/products/${this.productForm.productId}` : "/api/shop/admin/products", {
                    method: this.productForm.productId ? "PUT" : "POST",
                    body: JSON.stringify(body)
                });
                await Promise.all([this.loadShop(), this.loadAdminProducts()]);
                this.showToast("商品已保存");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async toggleProductStatus(product) {
            try {
                await this.api(`/api/shop/admin/products/${product.productId}/status?status=${!product.status}`, { method: "PATCH" });
                await Promise.all([this.loadShop(), this.loadAdminProducts()]);
                this.showToast(product.status ? "商品已下架" : "商品已上架");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async deleteProduct(productId) {
            try {
                await this.api(`/api/shop/admin/products/${productId}`, { method: "DELETE" });
                await Promise.all([this.loadShop(), this.loadAdminProducts()]);
                this.showToast("商品已删除");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async buyProduct(product) {
            if (!this.requireLogin()) return;
            try {
                const order = await this.api("/api/shop/orders", {
                    method: "POST",
                    body: JSON.stringify({ productId: product.productId, quantity: 1 })
                });
                await Promise.all([this.loadShop(), this.loadOrders()]);
                this.showToast(order.payUrl ? "订单已创建，请按支付链接完成支付" : "订单已创建，请联系管理员确认支付");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async updateOrderStatus(order, status) {
            try {
                await this.api(`/api/shop/admin/orders/${order.orderId}/status?status=${status}`, { method: "PATCH" });
                await this.loadAllOrders();
                this.showToast("订单状态已更新");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async createDonation() {
            if (!this.requireLogin()) return;
            try {
                await this.api("/api/shop/donations", {
                    method: "POST",
                    body: JSON.stringify({ ...this.donationForm, amount: Number(this.donationForm.amount) })
                });
                await this.loadDonations();
                this.showToast("捐赠记录已登记");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async updateDonationStatus(record, status) {
            try {
                await this.api(`/api/shop/admin/donations/${record.donationId}/status?status=${status}`, { method: "PATCH" });
                await this.loadAllDonations();
                this.showToast("捐赠状态已更新");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async saveArticle() {
            try {
                const path = this.articleForm.articleId ? `/api/community/articles/${this.articleForm.articleId}` : "/api/community/articles";
                await this.api(path, {
                    method: this.articleForm.articleId ? "PUT" : "POST",
                    body: JSON.stringify(this.articleForm)
                });
                await this.loadCommunity();
                this.showToast("文章已保存");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        editArticle(article) {
            this.articleForm = { ...article };
        },
        async openArticle(article) {
            this.selectedArticle = await this.api(`/api/community/articles/${article.articleId}`);
            this.commentForm.sourceType = "ARTICLE";
            this.commentForm.sourceId = this.selectedArticle.articleId;
            await this.loadComments("ARTICLE", this.selectedArticle.articleId);
        },
        async deleteArticle(articleId) {
            try {
                await this.api(`/api/community/articles/${articleId}`, { method: "DELETE" });
                await this.loadCommunity();
                this.showToast("文章已删除");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async createPost() {
            if (!this.requireLogin()) return;
            try {
                await this.api("/api/community/posts", { method: "POST", body: JSON.stringify(this.postForm) });
                this.postForm.title = "";
                this.postForm.content = "";
                await this.loadCommunity();
                this.showToast("帖子已发布");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async closePost(post) {
            try {
                await this.api(`/api/community/posts/${post.postId}/status?status=CLOSED`, { method: "PATCH" });
                await this.loadCommunity();
                this.showToast("帖子已关闭");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async loadComments(sourceType, sourceId) {
            this.comments = await this.api(`/api/community/comments?sourceType=${sourceType}&sourceId=${sourceId}`);
        },
        async createComment() {
            if (!this.requireLogin()) return;
            try {
                await this.api("/api/community/comments", { method: "POST", body: JSON.stringify(this.commentForm) });
                await this.loadComments(this.commentForm.sourceType, this.commentForm.sourceId);
                this.commentForm.content = "";
                this.showToast("评论已发布");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async toggleCollect(sourceType, sourceId, title, imageUrl) {
            if (!this.requireLogin()) return;
            try {
                const params = new URLSearchParams({ sourceType, sourceId, title });
                if (imageUrl) params.set("imageUrl", imageUrl);
                const active = await this.api(`/api/community/collects?${params.toString()}`, { method: "POST" });
                await this.loadCollects();
                this.showToast(active ? "已收藏内容" : "已取消收藏");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async reviewReport(reportId, approved) {
            try {
                await this.api(`/api/reports/${reportId}/review`, {
                    method: "PATCH",
                    body: JSON.stringify({ approved, operatorName: this.user.userName, cat: null })
                });
                await this.refreshAll();
                this.showToast(approved ? "已核实并建档" : "已驳回上报");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async reviewApplication(applicationId, approved) {
            try {
                await this.api(`/api/applications/${applicationId}/review`, {
                    method: "PATCH",
                    body: JSON.stringify({
                        approved,
                        reviewNote: approved ? "材料完整，认养条件符合要求。" : "当前照护条件不满足认养要求。",
                        interviewNote: approved ? "已确认住所、假期照护和回访配合。" : "建议补充长期照护方案。"
                    })
                });
                await this.refreshAll();
                this.showToast("审核结果已保存");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async handover(applicationId) {
            try {
                await this.api(`/api/applications/${applicationId}/handover`, { method: "PATCH" });
                await this.refreshAll();
                this.showToast("已完成协议交接登记");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async favorite(catId) {
            if (!this.requireLogin()) return;
            try {
                const active = await this.api(`/api/cats/${catId}/favorite`, { method: "POST" });
                await this.loadMyFavorites();
                this.showToast(active ? "已收藏" : "已取消收藏");
            } catch (error) {
                this.showToast(error.message);
            }
        },
        async selectCat(cat) {
            if (!this.requireLogin()) return;
            this.applicationForm.catId = cat.catId;
            await this.loadPhotoWall(cat);
            location.hash = "#forms";
            this.showToast(`已选择 ${cat.catName}`);
        },
        async loadPhotoWall(cat) {
            this.photoForm.catId = cat.catId;
            this.photoForm.photoUrl = cat.coverUrl || "/uploads/cats/no-photo.svg";
            this.selectedPhotos = await this.api(`/api/cats/${cat.catId}/photos`);
            location.hash = "#recognition";
        },
        previewRecognition(event) {
            const file = event.target.files[0];
            this.recognitionFile = file;
            this.recognitionPreview = file ? URL.createObjectURL(file) : "";
        },
        async recognize() {
            if (!this.recognitionFile) {
                this.showToast("请先选择一张猫咪照片");
                return;
            }
            try {
                const formData = new FormData();
                formData.append("image", this.recognitionFile);
                const response = await fetch("/api/recognition/recognize", { method: "POST", body: formData });
                const payload = await response.json();
                if (!response.ok || !payload.success) {
                    throw new Error(payload.message || "识别失败");
                }
                this.recognitionCandidates = payload.data.candidates;
                this.showToast(`已完成识别，使用 ${payload.data.sampleCount} 张样本比对`);
            } catch (error) {
                this.showToast(error.message);
            }
        },
        safeImage(url) {
            return url && url.trim() ? url : "/uploads/cats/no-photo.svg";
        },
        statusLabel(status) {
            return {
                OBSERVING: "观察中",
                ADOPTABLE: "可认养",
                RESERVED: "待交接",
                ADOPTED: "已认养",
                MEDICAL: "医疗中",
                MISSING: "失联",
                PENDING: "待审核",
                APPROVED: "审核通过，待交接",
                REJECTED: "已拒绝",
                WITHDRAWN: "已撤回",
                HANDED_OVER: "已交接",
                NORMAL: "正常",
                ATTENTION: "需关注",
                RETURNED: "退养",
                CREATED: "待支付",
                PAID: "已支付",
                CANCELLED: "已取消",
                RECORDED: "已登记",
                CONFIRMED: "已确认",
                VOID: "已作废",
                PUBLISHED: "已发布",
                CLOSED: "已关闭"
            }[status] || status;
        },
        angleLabel(angle) {
            return {
                FRONT: "正脸",
                LEFT: "左侧脸",
                RIGHT: "右侧脸",
                FULL_BODY: "全身照",
                BACK: "背部"
            }[angle] || angle;
        },
        maskPhone(phone) {
            return phone ? phone.replace(/^(\d{3})\d{4}(\d+)/, "$1****$2") : "";
        },
        showToast(message) {
            this.toastMessage = message;
            window.clearTimeout(this.toastTimer);
            this.toastTimer = window.setTimeout(() => {
                this.toastMessage = "";
            }, 2600);
        }
    }
}).mount("#app");
