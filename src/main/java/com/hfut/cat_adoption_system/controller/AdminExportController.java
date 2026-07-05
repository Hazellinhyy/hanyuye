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

@RestController
@RequestMapping("/api/admin/export")
@RequireRole(Role.ADMIN)
public class AdminExportController {
    private final CatAdoptionService service;

    public AdminExportController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping("/cats")
    public ResponseEntity<byte[]> cats(@RequestParam(required = false) String status,
                                       @RequestParam(required = false) String healthLevel,
                                       @RequestParam(required = false) String gender,
                                       @RequestParam(required = false) String keyword) {
        return csv("cats.csv", service.exportCatsCsv(status, healthLevel, gender, keyword));
    }

    @GetMapping("/applications")
    public ResponseEntity<byte[]> applications(@RequestParam(required = false) String status,
                                               @RequestParam(required = false) String riskLevel,
                                               @RequestParam(required = false) String keyword) {
        return csv("applications.csv", service.exportApplicationsCsv(status, riskLevel, keyword));
    }

    @GetMapping("/followups")
    public ResponseEntity<byte[]> followups(@RequestParam(required = false) String status,
                                            @RequestParam(required = false) String taskType,
                                            @RequestParam(required = false) String planDate,
                                            @RequestParam(required = false) String keyword) {
        return csv("followups.csv", service.exportFollowupsCsv(status, taskType, planDate, keyword));
    }

    @GetMapping("/warnings")
    public ResponseEntity<byte[]> warnings(@RequestParam(required = false) String warningType,
                                           @RequestParam(required = false) String warningLevel,
                                           @RequestParam(required = false) String status,
                                           @RequestParam(required = false) String keyword) {
        return csv("warnings.csv", service.exportWarningsCsv(warningType, warningLevel, status, keyword));
    }

    private ResponseEntity<byte[]> csv(String filename, String content) {
        byte[] body = ("\uFEFF" + content).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString())
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(body);
    }
}
