package com.hfut.cat_adoption_system.auth;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码哈希工具类
 * 
 * 采用 SHA-256 + Salt 机制实现安全的密码存储和验证，
 * 每个密码使用独立的随机盐值，防止彩虹表攻击。
 * 存储格式：Base64(salt):Base64(digest)
 */
@Component
public class PasswordHasher {

    /** 安全随机数生成器，用于生成随机盐值 */
    private final SecureRandom random = new SecureRandom();

    /**
     * 对密码进行哈希处理
     * 
     * @param password 原始密码
     * @return 存储格式：Base64(盐值):Base64(哈希值)
     */
    public String hash(String password) {
        // 生成16字节的随机盐值
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // 使用盐值对密码进行哈希计算
        byte[] digest = digest(salt, password);

        // 组合盐值和哈希值，使用Base64 URL安全编码
        return Base64.getUrlEncoder().withoutPadding().encodeToString(salt)
                + ":"
                + Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    /**
     * 验证密码是否与存储的哈希值匹配
     * 
     * @param password   用户输入的密码
     * @param storedHash 存储的哈希值
     * @return 是否匹配
     */
    public boolean matches(String password, String storedHash) {
        // 特殊处理：空哈希值时使用默认密码"123456"（演示账户）
        if (storedHash == null || storedHash.isBlank()) {
            return "123456".equals(password);
        }

        // 解析存储的哈希值：分割盐值和哈希部分
        String[] parts = storedHash.split(":", 2);
        if (parts.length != 2) {
            return false;
        }

        // 解码盐值和期望的哈希值
        byte[] salt = Base64.getUrlDecoder().decode(parts[0]);
        byte[] expected = Base64.getUrlDecoder().decode(parts[1]);

        // 使用常量时间比较防止时序攻击
        return MessageDigest.isEqual(expected, digest(salt, password));
    }

    /**
     * 内部方法：执行 SHA-256 哈希计算
     * 
     * @param salt     盐值
     * @param password 密码
     * @return 哈希结果
     */
    private byte[] digest(byte[] salt, String password) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(salt); // 先更新盐值
            return sha256.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("Password hashing is unavailable", exception);
        }
    }
}
