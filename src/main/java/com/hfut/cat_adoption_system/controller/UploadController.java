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
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传控制器
 * 
 * 提供图片上传功能，支持以下上传场景：
 * - 线索图片上传（公开接口）
 * - 公告图片上传（管理员权限）
 * 
 * 上传文件存储规则：
 * - 按日期创建目录（格式：yyyyMMdd）
 * - 文件名使用UUID重命名，避免重复
 * - 支持格式：JPG、PNG、WEBP、GIF
 * - 最大文件大小：5MB
 */
@RestController
public class UploadController {

    /** 最大图片大小：5MB */
    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;

    /** 允许的图片类型 */
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    /** 日期目录格式化器：yyyyMMdd */
    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyyMMdd");

    /** 线索图片上传根目录 */
    private final Path clueUploadRoot = Path.of("uploads", "clues").toAbsolutePath().normalize();

    /** 公告图片上传根目录 */
    private final Path noticeUploadRoot = Path.of("uploads", "notices").toAbsolutePath().normalize();

    /**
     * 上传线索图片（公开接口）
     * 用于上传流浪猫线索相关的图片
     * 
     * @param file 图片文件
     */
    @PostMapping(value = "/api/uploads/clues", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UploadResult> uploadCluePhoto(@RequestPart("file") MultipartFile file) {
        return uploadImage(file, clueUploadRoot, "/uploads/clues/");
    }

    /**
     * 上传公告图片（管理员权限）
     * 用于上传公告相关的图片
     * 
     * @param file 图片文件
     */
    @PostMapping(value = "/api/uploads/notices", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequireRole(Role.ADMIN)
    public ApiResponse<UploadResult> uploadNoticeImage(@RequestPart("file") MultipartFile file) {
        return uploadImage(file, noticeUploadRoot, "/uploads/notices/");
    }

    /**
     * 图片上传核心方法
     * 处理文件验证、存储路径生成和文件保存
     * 
     * @param file      上传的文件
     * @param root      存储根目录
     * @param urlPrefix 返回URL前缀
     * @return UploadResult 上传结果（包含URL、原文件名、文件大小）
     */
    private ApiResponse<UploadResult> uploadImage(MultipartFile file, Path root, String urlPrefix) {
        // 文件非空校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的照片");
        }

        // 文件大小校验（最大5MB）
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException("照片不能超过 5MB");
        }

        // 文件类型校验
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        String extension = extensionFor(contentType, file.getOriginalFilename());
        if (extension == null) {
            throw new BusinessException("仅支持 JPG、PNG、WEBP 或 GIF 图片");
        }

        // 生成存储路径：按日期目录 + UUID文件名
        String dateDir = LocalDateTime.now().format(DATE_DIR);
        String storedName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path targetDir = root.resolve(dateDir).normalize();
        Path target = targetDir.resolve(storedName).normalize();

        // 路径安全校验：防止路径穿越攻击
        if (!target.startsWith(root)) {
            throw new BusinessException("上传路径非法");
        }

        // 创建目录并保存文件
        try {
            Files.createDirectories(targetDir);
            file.transferTo(target);
        } catch (IOException error) {
            throw new BusinessException("照片保存失败，请稍后重试");
        }

        // 返回上传结果
        String url = urlPrefix + dateDir + "/" + storedName;
        return ApiResponse.created(new UploadResult(url, cleanName(file.getOriginalFilename()), file.getSize()));
    }

    /**
     * 根据内容类型或文件名获取文件扩展名
     * 优先根据contentType判断，其次根据文件名后缀判断
     * 
     * @param contentType MIME类型
     * @param filename    原文件名
     * @return 文件扩展名（如.jpg、.png），不支持的类型返回null
     */
    private String extensionFor(String contentType, String filename) {
        if (ALLOWED_TYPES.contains(contentType)) {
            return switch (contentType) {
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                case "image/gif" -> ".gif";
                default -> ".jpg";
            };
        }
        String name = filename == null ? "" : filename.toLowerCase(Locale.ROOT);
        if (name.endsWith(".jpg") || name.endsWith(".jpeg"))
            return ".jpg";
        if (name.endsWith(".png"))
            return ".png";
        if (name.endsWith(".webp"))
            return ".webp";
        if (name.endsWith(".gif"))
            return ".gif";
        return null;
    }

    /**
     * 清理文件名，提取纯文件名（去除路径部分）
     * 
     * @param filename 原文件名
     * @return 清理后的纯文件名
     */
    private String cleanName(String filename) {
        if (filename == null || filename.isBlank()) {
            return "image";
        }
        return Path.of(filename).getFileName().toString();
    }
}