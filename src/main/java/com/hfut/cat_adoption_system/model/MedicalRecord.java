package com.hfut.cat_adoption_system.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MedicalRecord(
                String medicalId,
                String catId,
                LocalDate checkDate,
                String hospital,
                HealthLevel healthLevel,
                boolean vaccinated,
                boolean sterilized,
                String treatment,
                String doctorNote,
                String attachmentUrl,
                LocalDateTime createdAt) {
}