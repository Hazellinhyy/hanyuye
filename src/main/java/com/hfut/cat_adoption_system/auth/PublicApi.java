package com.hfut.cat_adoption_system.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 公开接口注解
 * 
 * 标注此注解的 Controller 类或方法将被认证拦截器放行，
 * 无需登录即可访问。适用于登录、注册、首页等公开接口。
 * 
 * 使用示例：
 * - 类级别：@PublicApi 在 Controller 类上，整个类的所有方法都公开
 * - 方法级别：@PublicApi 在特定方法上，仅该方法公开
 */
@Target({ ElementType.METHOD, ElementType.TYPE }) // 可标注在方法或类上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，便于反射获取
public @interface PublicApi {
}
