package com.hfut.cat_adoption_system.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CatPhoto(
        String photoId,
        String catId,
        String photoUrl,
        String angleCode,
        String photoScene,
        boolean cover,
        BigDecimal recognitionWeight,
        String featureVector,
        String featureNote,
        LocalDateTime uploadedAt
) {
}
