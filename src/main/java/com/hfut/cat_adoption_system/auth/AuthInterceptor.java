package com.hfut.cat_adoption_system.auth;

import com.hfut.cat_adoption_system.common.AuthException;
import com.hfut.cat_adoption_system.mapper.UserMapper;
import com.hfut.cat_adoption_system.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * Spring MVC 认证拦截器
 * 
 * 负责在请求处理前进行身份认证和权限校验，实现以下功能：
 * 1. 识别公开接口（无需认证）
 * 2. 验证 JWT Token 的有效性
 * 3. 检查用户状态和角色权限
 * 4. 将认证信息存入 ThreadLocal 供后续使用
 * 5. 请求结束后清理认证上下文
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /** Token服务：负责JWT的解析和验证 */
    private final TokenService tokenService;

    /** 用户Mapper：用于查询用户信息和Token版本 */
    private final UserMapper userMapper;

    /**
     * 构造函数：依赖注入 TokenService 和 UserMapper
     */
    public AuthInterceptor(TokenService tokenService, UserMapper userMapper) {
        this.tokenService = tokenService;
        this.userMapper = userMapper;
    }

    /**
     * 请求预处理方法：在Controller方法执行前进行认证校验
     * 
     * @return true表示继续执行，false表示中断请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 非Controller方法（如静态资源）直接放行
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 公开接口直接放行（标注@PublicApi的方法或类）
        if (isPublic(handlerMethod)) {
            return true;
        }

        // 1. 提取Authorization请求头
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "请先登录");
        }

        // 2. 解析JWT Token
        TokenService.ParsedToken token = tokenService.parse(header.substring(7).trim());

        // 3. 查询用户信息并验证状态
        User user = userMapper.findById(token.userId());
        if (user == null || !user.enabled() || user.role() != token.role()) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "账号不可用，请重新登录");
        }

        // 4. Token版本校验（用于实现登出/密码修改后Token失效）
        int tokenVersion = userMapper.findTokenVersion(user.userId());
        if (tokenVersion != token.tokenVersion()) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "登录状态已更新，请重新登录");
        }

        // 5. 将认证信息存入ThreadLocal，供后续业务代码使用
        AuthContext.set(new AuthPrincipal(user.userId(), user.userName(), user.role(), tokenVersion));

        // 6. 角色权限校验
        RequireRole required = findRequireRole(handlerMethod);
        if (required != null && Arrays.stream(required.value()).noneMatch(role -> role == user.role())) {
            throw new AuthException(HttpStatus.FORBIDDEN, "当前账号无权执行此操作");
        }

        return true;
    }

    /**
     * 请求完成后清理认证上下文，防止ThreadLocal内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        AuthContext.clear();
    }

    /**
     * 判断接口是否为公开接口
     * 检查方法或类上是否标注了@PublicApi注解
     */
    private boolean isPublic(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(PublicApi.class)
                || handlerMethod.getBeanType().isAnnotationPresent(PublicApi.class);
    }

    /**
     * 查找接口要求的角色权限
     * 优先检查方法级别的@RequireRole注解，其次检查类级别
     */
    private RequireRole findRequireRole(HandlerMethod handlerMethod) {
        RequireRole methodRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        return methodRole == null ? handlerMethod.getBeanType().getAnnotation(RequireRole.class) : methodRole;
    }
}
