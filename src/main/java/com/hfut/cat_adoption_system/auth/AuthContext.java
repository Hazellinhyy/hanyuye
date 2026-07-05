package com.hfut.cat_adoption_system.auth;

import com.hfut.cat_adoption_system.model.Role;

public final class AuthContext {
    private static final ThreadLocal<AuthPrincipal> CURRENT = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(AuthPrincipal principal) {
        CURRENT.set(principal);
    }

    public static AuthPrincipal get() {
        return CURRENT.get();
    }

    public static String userId() {
        AuthPrincipal principal = get();
        return principal == null ? null : principal.userId();
    }

    public static Role role() {
        AuthPrincipal principal = get();
        return principal == null ? null : principal.role();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
