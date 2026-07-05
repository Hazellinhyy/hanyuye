package com.hfut.cat_adoption_system.auth;

import com.hfut.cat_adoption_system.model.Role;

public record AuthPrincipal(
        String userId,
        String userName,
        Role role,
        int tokenVersion
) {
}
