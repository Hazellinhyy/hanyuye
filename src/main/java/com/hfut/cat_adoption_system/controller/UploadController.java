package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.common.BusinessException;
import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.dto.UploadResult;
import com.hfut.cat_adoption_system.model.Role;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@RestController
public class UploadController {
    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final Path clueUploadRoot = Path.of("uploads", "clues").toAbsolutePath().normalize();
    private final Path noticeUploadRoot = Path.of("uploads", "notices").toAbsolutePath().normalize();

    @PostMapping("/api/uploads/clues")
    public ApiResponse<UploadResult> uploadCluePhoto(@RequestPart("file") MultipartFile file) {
        return uploadImage(file, clueUploadRoot, "/uploads/clues/");
    }

    @PostMapping("/api/uploads/notices")
    @RequireRole(Role.ADMIN)
    public ApiResponse<UploadResult> uploadNoticeImage(@RequestPart("file") MultipartFile file) {
        return uploadImage(file, noticeUploadRoot, "/uploads/notices/");
    }

    private ApiResponse<UploadResult> uploadImage(MultipartFile file, Path root, String urlPrefix) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的照片");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException("照片不能超过 5MB");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("仅支持 JPG、PNG、WEBP 或 GIF 图片");
        }
        String dateDir = LocalDateTime.now().format(DATE_DIR);
        String extension = extensionFor(contentType);
        String storedName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path targetDir = root.resolve(dateDir).normalize();
        Path target = targetDir.resolve(storedName).normalize();
        if (!target.startsWith(root)) {
            throw new BusinessException("上传路径非法");
        }
        try {
            Files.createDirectories(targetDir);
            file.transferTo(target);
        } catch (IOException error) {
            throw new BusinessException("照片保存失败，请稍后重试");
        }
        String url = urlPrefix + dateDir + "/" + storedName;
        return ApiResponse.created(new UploadResult(url, cleanName(file.getOriginalFilename()), file.getSize()));
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }

    private String cleanName(String filename) {
        if (filename == null || filename.isBlank()) {
            return "image";
        }
        return Path.of(filename).getFileName().toString();
    }
}
