package com.hfut.cat_adoption_system.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * 使用 @RestControllerAdvice 注解实现全局异常捕获，
 * 将各类异常统一转换为 ApiResponse 格式返回给前端，
 * 确保前端能统一处理错误响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理认证异常（AuthException）
     * 认证失败时根据异常中携带的 HTTP 状态码返回响应
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuth(AuthException exception) {
        return ResponseEntity.status(exception.status()).body(ApiResponse.fail(exception.getMessage()));
    }

    /**
     * 处理业务异常（BusinessException）
     * 业务逻辑错误返回 400 Bad Request 状态码
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBusiness(BusinessException exception) {
        return ApiResponse.fail(exception.getMessage());
    }

    /**
     * 处理参数校验异常（MethodArgumentNotValidException）
     * Spring Validation 校验失败时自动触发，收集所有字段错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException exception) {
        // 收集所有字段错误并拼接成可读的错误消息
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("；"));
        return ApiResponse.fail(message);
    }

    /**
     * 格式化字段错误信息
     * 
     * @param error 字段错误对象
     * @return 格式化后的错误消息（字段名 + 错误描述）
     */
    private String formatFieldError(FieldError error) {
        return error.getField() + " " + error.getDefaultMessage();
    }
}
