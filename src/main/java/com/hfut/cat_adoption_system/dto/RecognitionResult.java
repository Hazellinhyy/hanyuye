package com.hfut.cat_adoption_system.dto;

import java.util.List;

public record RecognitionResult(
        int sampleCount,
        String featureVersion,
        List<RecognitionCandidate> candidates
) {
}
