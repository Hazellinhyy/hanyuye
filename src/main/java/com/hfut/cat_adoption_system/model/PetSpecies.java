package com.hfut.cat_adoption_system.model;

import java.time.LocalDateTime;

public record PetSpecies(
        Integer speciesId,
        String speciesName,
        String description,
        LocalDateTime createdAt
) {
}
