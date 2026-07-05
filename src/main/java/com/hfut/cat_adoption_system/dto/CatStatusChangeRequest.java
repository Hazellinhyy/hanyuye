package com.hfut.cat_adoption_system.dto;

import com.hfut.cat_adoption_system.model.CatStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CatStatusChangeRequest(
        @NotNull CatStatus targetStatus,
        @NotBlank String reason
) {
}
