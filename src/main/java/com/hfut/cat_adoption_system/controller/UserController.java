package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.PublicApi;
import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ApplicationDetail;
import com.hfut.cat_adoption_system.dto.ForgotPasswordRequest;
import com.hfut.cat_adoption_system.dto.LoginRequest;
import com.hfut.cat_adoption_system.dto.LoginResult;
import com.hfut.cat_adoption_system.dto.PasswordUpdateRequest;
import com.hfut.cat_adoption_system.dto.ProfileUpdateRequest;
import com.hfut.cat_adoption_system.dto.RegisterRequest;
import com.hfut.cat_adoption_system.model.ApplicationStatus;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.model.User;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户管理控制器
 * 
 * 提供用户相关的核心功能：
 * - 用户注册（公开接口）
 * - 用户登录（公开接口）
 * - 用户登出（登录用户）
 * - 查询当前用户信息（登录用户）
 * - 查询我的认养申请（登录用户）
 * - 查询我的收藏（登录用户）
 * - 更新个人资料（登录用户）
 * - 修改密码（登录用户）
 * - 查询所有用户（管理员权限）
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /** 认养服务：处理用户相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public UserController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询所有用户（管理员权限）
     * 返回系统所有用户列表
     */
    @GetMapping
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<User>> listUsers() {
        return ApiResponse.ok(service.listUsers());
    }

    /**
     * 用户注册（公开接口）
     * 新用户注册账号，注册成功后需登录才能使用系统
     * 
     * @param request 注册请求（包含用户名、密码、邮箱等）
     */
    @PostMapping
    @PublicApi
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.created(service.register(request));
    }

    /**
     * 用户登录（公开接口）
     * 用户通过账号密码登录系统
     * 
     * @param request 登录请求（包含用户名、密码）
     * @return LoginResult 登录结果（包含用户信息和Token）
     */
    @PostMapping("/login")
    @PublicApi
    public ApiResponse<LoginResult> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(service.login(request));
    }

    /**
     * 忘记密码（公开接口）
     * 用户通过账号、手机号和身份证号校验身份后重置密码
     *
     * @param request 忘记密码请求
     */
    @PostMapping("/password/forgot")
    @PublicApi
    public ApiResponse<Boolean> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        service.resetPassword(request);
        return ApiResponse.ok(true);
    }

    /**
     * 用户登出（登录用户）
     * 清除当前用户的登录状态
     */
    @PostMapping("/logout")
    public ApiResponse<Boolean> logout() {
        service.logout();
        return ApiResponse.ok(true);
    }

    /**
     * 查询当前用户信息（登录用户）
     * 返回当前登录用户的详细信息
     */
    @GetMapping("/me")
    public ApiResponse<User> me() {
        return ApiResponse.ok(service.currentUser());
    }

    /**
     * 查询我的认养申请（登录用户）
     * 返回当前用户提交的认养申请列表
     * 
     * @param status 申请状态筛选（可选）
     */
    @GetMapping("/me/applications")
    public ApiResponse<List<ApplicationDetail>> myApplications(
            @RequestParam(required = false) ApplicationStatus status) {
        return ApiResponse.ok(service.listMyApplicationDetails(status));
    }

    /**
     * 查询我的收藏（登录用户）
     * 返回当前用户收藏的猫咪列表
     */
    @GetMapping("/me/favorites")
    public ApiResponse<List<Cat>> myFavorites() {
        return ApiResponse.ok(service.listMyFavoriteCats());
    }

    /**
     * 更新个人资料（登录用户）
     * 更新当前用户的个人信息
     * 
     * @param request 资料更新请求（包含昵称、头像、联系方式等）
     */
    @PatchMapping("/me")
    public ApiResponse<User> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ApiResponse.ok(service.updateProfile(request));
    }

    /**
     * 修改密码（登录用户）
     * 更新当前用户的登录密码
     * 
     * @param request 密码更新请求（包含旧密码、新密码）
     */
    @PatchMapping("/me/password")
    public ApiResponse<Boolean> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        service.updatePassword(request);
        return ApiResponse.ok(true);
    }
}
