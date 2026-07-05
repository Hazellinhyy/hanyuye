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

@Component
public class TokenService {
    private static final long TOKEN_TTL_SECONDS = 60L * 60 * 24 * 7;

    private final byte[] secret;

    public TokenService(@Value("${app.auth.secret:cat-adoption-local-dev-secret}") String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String issue(AuthPrincipal principal) {
        long expiresAt = Instant.now().getEpochSecond() + TOKEN_TTL_SECONDS;
        String payload = principal.userId() + "|" + principal.role().name() + "|" + principal.tokenVersion() + "|" + expiresAt;
        String body = base64(payload.getBytes(StandardCharsets.UTF_8));
        return body + "." + sign(body);
    }

    public ParsedToken parse(String token) {
        String[] parts = token == null ? new String[0] : token.split("\\.", 2);
        if (parts.length != 2 || !sign(parts[0]).equals(parts[1])) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        String[] fields = payload.split("\\|", 4);
        if (fields.length != 4) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
        }
        long expiresAt = Long.parseLong(fields[3]);
        if (Instant.now().getEpochSecond() > expiresAt) {
            throw new AuthException(HttpStatus.UNAUTHORIZED, "登录已过期，请重新登录");
        }
        return new ParsedToken(fields[0], parseRole(fields[1]), Integer.parseInt(fields[2]), expiresAt);
    }

    private Role parseRole(String roleName) {
        return switch (roleName) {
            case "HOSPITAL_USER", "MEDICAL", "DOCTOR", "PARTNER_HOSPITAL", "合作医院", "医疗协作用户", "医院用户" -> Role.HOSPITAL;
            default -> Role.valueOf(roleName);
        };
    }

    private String sign(String body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return base64(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Token signing is unavailable", exception);
        }
    }

    private String base64(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public record ParsedToken(String userId, Role role, int tokenVersion, long expiresAt) {
    }
}
