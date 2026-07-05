package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record User(
        String userId,
        String userName,
        String schoolNo,
        String phone,
        String idCard,
        String college,
        String petExperience,
        Role role,
        boolean enabled,
        LocalDateTime createdAt
) {
    public String maskedPhone() {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    public String maskedIdCard() {
        if (idCard == null || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
    }
}
