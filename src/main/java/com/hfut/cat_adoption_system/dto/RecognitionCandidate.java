package com.hfut.cat_adoption_system.dto;

public record RecognitionCandidate(
        String catId,
        String catName,
        String coverUrl,
        String matchedPhotoId,
        String matchedPhotoUrl,
        String angleCode,
        double confidence,
        String reason
) {
}
