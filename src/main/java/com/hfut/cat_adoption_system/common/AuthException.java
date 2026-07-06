package com.hfut.cat_adoption_system.common;

import org.springframework.http.HttpStatus;

/**
 * 认证异常类
 * 
 * 用于封装认证和授权相关的异常，包含 HTTP 状态码信息。
 * 配合全局异常处理器返回统一格式的错误响应。
 * 
 * 常见使用场景：
 * - 未登录访问需要认证的接口（401 Unauthorized）
 * - 权限不足访问接口（403 Forbidden）
 * - Token 过期或无效（401 Unauthorized）
 */
public class AuthException extends RuntimeException {

    /** HTTP 状态码 */
    private final HttpStatus status;

    /**
     * 构造函数
     * 
     * @param status  HTTP 状态码（如 401、403）
     * @param message 异常消息
     */
    public AuthException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * 获取 HTTP 状态码
     * 
     * @return HTTP 状态码
     */
    public HttpStatus status() {
        return status;
    }
}
