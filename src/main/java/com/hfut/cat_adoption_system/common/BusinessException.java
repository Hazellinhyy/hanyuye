package com.hfut.cat_adoption_system.common;

/**
 * 业务异常类
 * 
 * 用于封装业务逻辑中的错误情况，配合全局异常处理器返回统一格式的错误响应。
 * 与 AuthException 不同，此类用于非认证相关的业务逻辑错误。
 * 
 * 常见使用场景：
 * - 数据校验失败（如参数格式错误、必填字段缺失）
 * - 业务规则冲突（如重复提交、资源不存在、操作不允许）
 * - 业务逻辑错误（如余额不足、库存不足等）
 */
public class BusinessException extends RuntimeException {

    /**
     * 构造函数
     * 
     * @param message 业务错误消息，将返回给前端展示
     */
    public BusinessException(String message) {
        super(message);
    }
}
