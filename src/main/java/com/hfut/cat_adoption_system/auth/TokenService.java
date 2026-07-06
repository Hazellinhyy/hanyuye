package com.hfut.cat_adoption_system.auth;

import com.hfut.cat_adoption_system.common.AuthException;
import com.hfut.cat_adoption_system.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

/**
 * JWT Token 服务类
 * 
 * 负责生成和验证基于 HMAC-SHA256 签名的身份认证 Token，
 * Token 格式：Base64(payload).HMAC-SHA256(body+secret)
 * payload 结构：userId|role|tokenVersion|expiresAt
 */
@Component
public class TokenService {

    /** Token 有效期：7天（秒） */
    private static final long TOKEN_TTL_SECONDS = 60L * 60 * 24 * 7;

    /** 签名密钥（从配置文件读取） */
    private final byte[] secret;

    /**
     * 构造函数：注入签名密钥
     * 
     * @param secret 签名密钥，默认值用于开发环境
     */
    public TokenService(@Value("${app.auth.secret:cat-adoption-local-dev-secret}") String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 签发 Token
     * 
     * @param principal 认证主体信息
     * @return 完整的 Token 字符串
     */
    public String issue(AuthPrincipal principal) {
        // 计算过期时间戳（当前时间 + 有效期）
        long expiresAt = Instant.now().getEpochSecond() + TOKEN_TTL_SECONDS;

        // 构建 payload：userId|role|tokenVersion|expiresAt
        String payload = principal.userId() + "|" + principal.role().name() + "|" + principal.tokenVersion() + "|"
                + expiresAt;

        // Base64 编码 payload
        String body = base64(payload.getBytes(StandardCharsets.UTF_8));

        // 返回格式：body.signature
        return body + "." + sign(body);
    }

    /**
     * 解析并验证 Token
     * 
     * @param token Token 字符串
     * @return 解析后的 Token 信息
     * @throws AuthException 验证失败时抛出异常
     */
    public ParsedToken parse(String token) {
        // 分割 Token：body 和 signature
        String[] parts = token == null ? new String[0] : token.split("\\.", 2);

        // 验证格式和签名
        if (parts.length != 2 || !sign(parts[0]).equals(parts[1])) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
        }

        // 解码 payload
        String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        String[] fields = payload.split("\\|", 4);

        // 验证 payload 格式
        if (fields.length != 4) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
        }

        // 验证过期时间
        long expiresAt = Long.parseLong(fields[3]);
        if (Instant.now().getEpochSecond() > expiresAt) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "登录已过期，请重新登录");
        }

        // 返回解析结果
        return new ParsedToken(fields[0], parseRole(fields[1]), Integer.parseInt(fields[2]), expiresAt);
    }

    /**
     * 角色名称解析（兼容多种角色命名）
     * 处理历史遗留的角色命名不一致问题
     */
    private Role parseRole(String roleName) {
        return switch (roleName) {
            // 兼容各种历史角色名称，统一映射为 HOSPITAL
            case "HOSPITAL_USER", "MEDICAL", "DOCTOR", "PARTNER_HOSPITAL", "合作医院", "医疗协作用户", "医院用户" -> Role.HOSPITAL;
            default -> Role.valueOf(roleName);
        };
    }

    /**
     * 使用 HMAC-SHA256 对数据进行签名
     * 
     * @param body 要签名的数据
     * @return 签名结果（Base64 URL安全编码）
     */
    private String sign(String body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return base64(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Token signing is unavailable", exception);
        }
    }

    /**
     * Base64 URL安全编码（无填充）
     */
    private String base64(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * 解析后的 Token 记录类
     * 
     * @param userId       用户ID
     * @param role         用户角色
     * @param tokenVersion Token版本号（用于Token失效机制）
     * @param expiresAt    过期时间戳
     */
    public record ParsedToken(String userId, Role role, int tokenVersion, long expiresAt) {
    }
}
