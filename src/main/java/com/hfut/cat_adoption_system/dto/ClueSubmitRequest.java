package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record ClueSubmitRequest(
        @NotBlank String foundLocation,
        String foundArea,
        LocalDateTime foundTime,
        @NotBlank String photoUrl,
        @NotBlank String description,
        @NotBlank String urgencyLevel
) {
}
