package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;

public record DictItemRequest(
        @NotBlank String dictType,
        @NotBlank String dictLabel,
        @NotBlank String dictValue,
        Integer sortOrder,
        Boolean enabled,
        String remark
) {
}
