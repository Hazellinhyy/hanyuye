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

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final TokenService tokenService;
    private final UserMapper userMapper;

    public AuthInterceptor(TokenService tokenService, UserMapper userMapper) {
        this.tokenService = tokenService;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        if (isPublic(handlerMethod)) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "请先登录");
        }

        TokenService.ParsedToken token = tokenService.parse(header.substring(7).trim());
        User user = userMapper.findById(token.userId());
        if (user == null || !user.enabled() || user.role() != token.role()) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "账号不可用，请重新登录");
        }
        int tokenVersion = userMapper.findTokenVersion(user.userId());
        if (tokenVersion != token.tokenVersion()) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "登录状态已更新，请重新登录");
        }

        AuthContext.set(new AuthPrincipal(user.userId(), user.userName(), user.role(), tokenVersion));
        RequireRole required = findRequireRole(handlerMethod);
        if (required != null && Arrays.stream(required.value()).noneMatch(role -> role == user.role())) {
            throw new AuthException(HttpStatus.FORBIDDEN, "当前账号无权执行此操作");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private boolean isPublic(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(PublicApi.class)
                || handlerMethod.getBeanType().isAnnotationPresent(PublicApi.class);
    }

    private RequireRole findRequireRole(HandlerMethod handlerMethod) {
        RequireRole methodRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        return methodRole == null ? handlerMethod.getBeanType().getAnnotation(RequireRole.class) : methodRole;
    }
}
