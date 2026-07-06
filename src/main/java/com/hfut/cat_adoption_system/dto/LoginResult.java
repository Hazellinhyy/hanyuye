package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.User;

/**
 * 用户登录响应DTO
 * 
 * 封装用户登录请求的响应数据结构，包含登录成功后的访问令牌和用户信息。
 * 使用Java Record实现，自动生成getter、equals、hashCode等方法。
 */
public record LoginResult(
                /** JWT访问令牌，用于后续API请求的身份认证 */
                String token,
                /** 用户信息对象，包含用户基本资料 */
                User user) {
}