package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * 管理员数据导出控制器
 * 
 * 提供系统数据的CSV导出功能，支持按条件筛选后导出，包括：
 * - 猫咪档案数据导出
 * - 认养申请数据导出
 * - 随访任务数据导出
 * - 告警记录数据导出
 * 
 * 类级别权限：仅管理员( ADMIN )可访问
 */
@RestController
@RequestMapping("/api/admin/export")
@RequireRole(Role.ADMIN)
public class AdminExportController {

    /** 认养服务：处理数据导出的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdminExportController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 导出猫咪档案数据
     * 
     * @param status      猫咪状态筛选（可选）
     * @param healthLevel 健康等级筛选（可选）
     * @param gender      性别筛选（可选）
     * @param keyword     关键词搜索（可选）
     */
    @GetMapping("/cats")
    public ResponseEntity<byte[]> cats(@RequestParam(required = false) String status,
            @RequestParam(required = false) String healthLevel,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String keyword) {
        return csv("cats.csv", service.exportCatsCsv(status, healthLevel, gender, keyword));
    }

    /**
     * 导出认养申请数据
     * 
     * @param status    申请状态筛选（可选）
     * @param riskLevel 风险等级筛选（可选）
     * @param keyword   关键词搜索（可选）
     */
    @GetMapping("/applications")
    public ResponseEntity<byte[]> applications(@RequestParam(required = false) String status,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String keyword) {
        return csv("applications.csv", service.exportApplicationsCsv(status, riskLevel, keyword));
    }

    /**
     * 导出随访任务数据
     * 
     * @param status   任务状态筛选（可选）
     * @param taskType 任务类型筛选（可选）
     * @param planDate 计划日期筛选（可选）
     * @param keyword  关键词搜索（可选）
     */
    @GetMapping("/followups")
    public ResponseEntity<byte[]> followups(@RequestParam(required = false) String status,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) String planDate,
            @RequestParam(required = false) String keyword) {
        return csv("followups.csv", service.exportFollowupsCsv(status, taskType, planDate, keyword));
    }

    /**
     * 导出告警记录数据
     * 
     * @param warningType  告警类型筛选（可选）
     * @param warningLevel 告警级别筛选（可选）
     * @param status       处理状态筛选（可选）
     * @param keyword      关键词搜索（可选）
     */
    @GetMapping("/warnings")
    public ResponseEntity<byte[]> warnings(@RequestParam(required = false) String warningType,
            @RequestParam(required = false) String warningLevel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return csv("warnings.csv", service.exportWarningsCsv(warningType, warningLevel, status, keyword));
    }

    /**
     * 生成CSV文件下载响应
     * 
     * @param filename 下载文件名
     * @param content  CSV内容（已格式化的字符串）
     * @return ResponseEntity 包含CSV文件的下载响应
     */
    private ResponseEntity<byte[]> csv(String filename, String content) {
        // 添加BOM标记，确保Excel正确识别UTF-8编码
        byte[] body = ("\uFEFF" + content).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString())
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(body);
    }
}
