package com.hfut.cat_adoption_system.auth;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PasswordHasher {
    private final SecureRandom random = new SecureRandom();

    public String hash(String password) {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        byte[] digest = digest(salt, password);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(salt)
                + ":"
                + Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    public boolean matches(String password, String storedHash) {
        if (storedHash == null || storedHash.isBlank()) {
            return "123456".equals(password);
        }
        String[] parts = storedHash.split(":", 2);
        if (parts.length != 2) {
            return false;
        }
        byte[] salt = Base64.getUrlDecoder().decode(parts[0]);
        byte[] expected = Base64.getUrlDecoder().decode(parts[1]);
        return MessageDigest.isEqual(expected, digest(salt, password));
    }

    private byte[] digest(byte[] salt, String password) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(salt);
            return sha256.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("Password hashing is unavailable", exception);
        }
    }
}
