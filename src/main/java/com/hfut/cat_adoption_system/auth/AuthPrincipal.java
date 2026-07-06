package com.hfut.cat_adoption_system.auth;

import com.hfut.cat_adoption_system.model.Role;

/**
 * 认证主体记录类
 * 
 * 用于存储当前登录用户的认证信息，作为 ThreadLocal 上下文传递。
 * 采用 Java Record 实现，具备不可变性和简洁性。
 */
public record AuthPrincipal(
                /** 用户唯一标识（UUID） */
                String userId,
                /** 用户名（用于展示） */
                String userName,
                /** 用户角色（ADMIN/USER） */
                Role role,
                /** Token版本号（用于实现登出和密码修改后Token失效） */
                int tokenVersion) {
}
