package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ClueSubmitRequest;
import com.hfut.cat_adoption_system.model.Clue;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学生线索管理控制器
 * 
 * 提供学生用户的线索提交和查询功能：
 * - 提交猫咪线索（发现流浪猫或待认养猫咪时上报）
 * - 查询我提交的线索列表
 * 
 * 所有接口均需要学生角色权限
 */
@RestController
public class ClueController {

    /** 认养服务：处理线索相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public ClueController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 提交猫咪线索（学生权限）
     * 学生发现流浪猫或待认养猫咪时，可通过此接口上报线索信息
     * 
     * @param request 线索提交请求（包含猫咪位置、描述、照片等）
     */
    @PostMapping("/api/clues")
    @RequireRole(Role.STUDENT)
    public ApiResponse<Clue> submitClue(@Valid @RequestBody ClueSubmitRequest request) {
        return ApiResponse.created(service.submitClue(request));
    }

    /**
     * 查询我提交的线索列表（学生权限）
     * 
     * @param status 线索状态筛选（可选）
     */
    @GetMapping("/api/my/clues")
    @RequireRole(Role.STUDENT)
    public ApiResponse<List<Clue>> myClues(@RequestParam(required = false) String status) {
        return ApiResponse.ok(service.listMyClues(status));
    }
}