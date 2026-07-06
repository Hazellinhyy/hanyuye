package com.hfut.cat_adoption_system.auth;

import com.hfut.cat_adoption_system.model.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限要求注解
 * 
 * 用于标注接口所需的用户角色权限，配合认证拦截器实现基于角色的访问控制。
 * 只有拥有指定角色之一的用户才能访问标注了此注解的接口。
 * 
 * 使用示例：
 * - 类级别：@RequireRole(Role.ADMIN) 在 Controller 类上，整个类的所有方法都要求管理员权限
 * - 方法级别：@RequireRole({Role.ADMIN, Role.USER}) 在特定方法上，管理员或普通用户均可访问
 * 
 * 优先级：方法级别注解优先于类级别注解
 */
@Target({ ElementType.METHOD, ElementType.TYPE }) // 可标注在方法或类上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，便于反射获取
public @interface RequireRole {

    /**
     * 允许访问的角色列表
     * 用户只需拥有列表中的任意一个角色即可访问
     */
    Role[] value();
}
