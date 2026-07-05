package com.hfut.cat_adoption_system.dto;

public record UploadResult(
        String url,
        String originalFilename,
        long size
) {
}
