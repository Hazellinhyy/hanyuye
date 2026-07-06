package com.hfut.cat_adoption_system.common;

/**
 * 统一 API 响应包装类
 * 
 * 用于封装所有 REST API 的响应数据，确保前端能统一处理响应格式。
 * 采用泛型设计，支持任意类型的数据返回。
 * 
 * @param <T> 响应数据的类型
 */
public record ApiResponse<T>(
        /** 操作是否成功 */
        boolean success,
        /** 响应消息（成功时为成功提示，失败时为错误描述） */
        String message,
        /** 响应数据（成功时返回业务数据，失败时通常为null） */
        T data) {

    /**
     * 创建成功响应（通用操作成功）
     * 
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "操作成功", data);
    }

    /**
     * 创建成功响应（资源创建成功）
     * 
     * @param data 新创建的资源数据
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, "创建成功", data);
    }

    /**
     * 创建失败响应
     * 
     * @param message 错误消息
     * @return 失败响应对象
     */
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
