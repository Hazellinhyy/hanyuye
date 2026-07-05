package com.hfut.cat_adoption_system.dto;

import java.util.List;

public record AdminSearchGroup(
        String type,
        String label,
        List<AdminSearchResult> items
) {
}
