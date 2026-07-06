package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ActionReasonRequest;
import com.hfut.cat_adoption_system.dto.ClueVerifyRequest;
import com.hfut.cat_adoption_system.dto.CreateCatFromClueRequest;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.Clue;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员线索管理控制器
 * 
 * 提供猫咪线索的审核管理功能，包括：
 * - 查询线索列表（支持状态、紧急程度、关键词筛选）
 * - 查看线索详情
 * - 验证线索（核实线索真实性）
 * - 从线索创建猫咪档案
 * - 标记线索无效
 * - 删除线索（管理员权限）
 * 
 * 类级别权限：志愿者( VOLUNTEER )和管理员( ADMIN )均可访问
 */
@RestController
@RequestMapping("/api/admin/clues")
@RequireRole({ Role.VOLUNTEER, Role.ADMIN })
public class AdminClueController {

    /** 认养服务：处理线索相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public AdminClueController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询线索列表
     * 
     * @param status       线索状态筛选（可选）
     * @param urgencyLevel 紧急程度筛选（可选）
     * @param keyword      关键词搜索（可选）
     * @param page         页码（可选）
     * @param size         每页数量（可选）
     */
    @GetMapping
    public ApiResponse<List<Clue>> listClues(@RequestParam(required = false) String status,
            @RequestParam(required = false) String urgencyLevel,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(service.listAdminClues(status, urgencyLevel, keyword, page, size));
    }

    /**
     * 查询线索详情
     * 
     * @param id 线索ID
     */
    @GetMapping("/{id}")
    public ApiResponse<Clue> getClue(@PathVariable String id) {
        return ApiResponse.ok(service.getAdminClue(id));
    }

    /**
     * 验证线索
     * 志愿者对上报的线索进行核实，确认线索的真实性和有效性
     * 
     * @param id      线索ID
     * @param request 验证请求（包含验证结果和备注）
     */
    @PutMapping("/{id}/verify")
    public ApiResponse<Clue> verifyClue(@PathVariable String id,
            @Valid @RequestBody ClueVerifyRequest request) {
        return ApiResponse.ok(service.verifyClue(id, request));
    }

    /**
     * 从线索创建猫咪档案
     * 将有效线索转换为正式的猫咪档案，进入领养流程
     * 
     * @param id      线索ID
     * @param request 创建请求（包含猫咪基本信息）
     */
    @PostMapping("/{id}/create-cat")
    public ApiResponse<Cat> createCat(@PathVariable String id,
            @RequestBody CreateCatFromClueRequest request) {
        return ApiResponse.created(service.createCatFromClue(id, request));
    }

    /**
     * 标记线索无效
     * 将线索标记为无效状态（如重复上报、虚假信息等），需要填写原因
     * 
     * @param id      线索ID
     * @param request 操作请求（包含无效原因）
     */
    @PutMapping("/{id}/invalid")
    public ApiResponse<Clue> invalid(@PathVariable String id,
            @Valid @RequestBody ActionReasonRequest request) {
        return ApiResponse.ok(service.markClueInvalid(id, request));
    }

    /**
     * 删除线索（管理员权限）
     * 物理删除线索记录，需要填写删除原因（谨慎使用）
     * 
     * @param id      线索ID
     * @param request 操作请求（包含删除原因）
     */
    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> delete(@PathVariable String id,
            @Valid @RequestBody ActionReasonRequest request) {
        service.deleteAdminClue(id, request);
        return ApiResponse.ok(true);
    }
}