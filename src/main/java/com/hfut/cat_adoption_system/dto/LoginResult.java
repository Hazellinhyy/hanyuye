package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.User;

public record LoginResult(
        String token,
        User user
) {
}
