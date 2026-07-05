package com.hfut.cat_adoption_system.dto;

public record AdminSearchResult(
        String id,
        String title,
        String subtitle,
        String targetHash,
        String status
) {
}
