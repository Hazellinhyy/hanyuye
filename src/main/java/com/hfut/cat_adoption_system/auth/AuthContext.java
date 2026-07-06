package com.hfut.cat_adoption_system.auth;

import com.hfut.cat_adoption_system.model.Role;

/**
 * 认证上下文工具类
 * 
 * 采用 ThreadLocal 模式实现线程级别的认证信息隔离，
 * 确保每个请求线程拥有独立的认证主体信息，
 * 用于在请求处理过程中方便地获取当前用户的认证信息。
 */
public final class AuthContext {

    /**
     * ThreadLocal 变量，存储当前线程的认证主体信息
     * ThreadLocal 保证了每个线程拥有独立的副本，避免多线程并发问题
     */
    private static final ThreadLocal<AuthPrincipal> CURRENT = new ThreadLocal<>();

    /**
     * 私有构造函数，防止类被实例化
     * 该类所有方法均为静态方法，无需创建实例
     */
    private AuthContext() {
    }

    /**
     * 设置当前线程的认证主体
     * 
     * @param principal 认证主体对象，包含用户ID和角色信息
     */
    public static void set(AuthPrincipal principal) {
        CURRENT.set(principal);
    }

    /**
     * 获取当前线程的认证主体
     * 
     * @return 认证主体对象，如果未设置则返回null
     */
    public static AuthPrincipal get() {
        return CURRENT.get();
    }

    /**
     * 获取当前用户ID（便捷方法）
     * 
     * @return 当前用户ID，如果未认证则返回null
     */
    public static String userId() {
        AuthPrincipal principal = get();
        return principal == null ? null : principal.userId();
    }

    /**
     * 获取当前用户角色（便捷方法）
     * 
     * @return 当前用户角色枚举，如果未认证则返回null
     */
    public static Role role() {
        AuthPrincipal principal = get();
        return principal == null ? null : principal.role();
    }

    /**
     * 清除当前线程的认证信息
     * 建议在请求处理完毕后调用，避免内存泄漏
     */
    public static void clear() {
        CURRENT.remove();
    }
}

