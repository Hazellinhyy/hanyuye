package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.PublicApi;
import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.CatRequest;
import com.hfut.cat_adoption_system.dto.CatPhotoRequest;
import com.hfut.cat_adoption_system.dto.CatTimelineEvent;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.CatPhoto;
import com.hfut.cat_adoption_system.model.CatStatus;
import com.hfut.cat_adoption_system.model.HealthLevel;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 猫咪管理控制器
 * 
 * 提供猫咪档案的管理功能，支持公开访问和权限控制：
 * - 公开接口：查询猫咪列表、查看猫咪详情、查看猫咪时间线、查看猫咪照片
 * - 权限接口：创建猫咪、更新猫咪信息、删除猫咪、管理照片、变更状态
 * - 用户功能：收藏/取消收藏猫咪、查看我的收藏列表
 */
@RestController
@RequestMapping("/api/cats")
public class CatController {

    /** 认养服务：处理猫咪相关的业务逻辑 */
    private final CatAdoptionService service;

    /**
     * 构造函数：注入认养服务
     */
    public CatController(CatAdoptionService service) {
        this.service = service;
    }

    /**
     * 查询猫咪列表（公开接口）
     * 
     * @param status  猫咪状态筛选（可选）
     * @param keyword 关键词搜索（可选）
     */
    @GetMapping
    @PublicApi
    public ApiResponse<List<Cat>> listCats(@RequestParam(required = false) CatStatus status,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.listCats(status, keyword));
    }

    /**
     * 查询公开猫咪列表（公开接口）
     * 供首页展示可认养的猫咪，支持多条件筛选
     * 
     * @param keyword     关键词搜索（可选）
     * @param gender      性别筛选（可选）
     * @param healthLevel 健康等级筛选（可选）
     * @param sterilized  是否已绝育筛选（可选）
     * @param vaccinated  是否已接种疫苗筛选（可选）
     * @param tag         标签筛选（可选）
     */
    @GetMapping("/public")
    @PublicApi
    public ApiResponse<List<Cat>> publicCats(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) HealthLevel healthLevel,
            @RequestParam(required = false) Boolean sterilized,
            @RequestParam(required = false) Boolean vaccinated,
            @RequestParam(required = false) String tag) {
        return ApiResponse.ok(service.listPublicCats(keyword, gender, healthLevel, sterilized, vaccinated, tag));
    }

    /**
     * 查询公开猫咪详情（公开接口）
     * 
     * @param catId 猫咪ID
     */
    @GetMapping("/public/{catId}")
    @PublicApi
    public ApiResponse<Cat> publicCat(@PathVariable String catId) {
        return ApiResponse.ok(service.getPublicCat(catId));
    }

    /**
     * 查询猫咪时间线（公开接口）
     * 获取猫咪的重要事件记录，包括入院、医疗、认养、回访等关键节点
     * 
     * @param catId 猫咪ID
     */
    @GetMapping("/{catId}/timeline")
    @PublicApi
    public ApiResponse<List<CatTimelineEvent>> timeline(@PathVariable String catId) {
        return ApiResponse.ok(service.catTimeline(catId));
    }

    /**
     * 查询猫咪详情（公开接口）
     * 
     * @param catId 猫咪ID
     */
    @GetMapping("/{catId}")
    @PublicApi
    public ApiResponse<Cat> getCat(@PathVariable String catId) {
        return ApiResponse.ok(service.getCat(catId));
    }

    /**
     * 查询猫咪照片列表（公开接口）
     * 
     * @param catId 猫咪ID
     */
    @GetMapping("/{catId}/photos")
    @PublicApi
    public ApiResponse<List<CatPhoto>> listCatPhotos(@PathVariable String catId) {
        return ApiResponse.ok(service.listCatPhotos(catId));
    }

    /**
     * 查询所有猫咪照片（公开接口）
     */
    @GetMapping("/photos")
    @PublicApi
    public ApiResponse<List<CatPhoto>> listAllCatPhotos() {
        return ApiResponse.ok(service.listAllCatPhotos());
    }

    /**
     * 添加猫咪照片（志愿者/管理员权限）
     * 
     * @param request 照片请求（包含猫咪ID和照片URL）
     */
    @PostMapping("/photos")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<CatPhoto> addCatPhoto(@Valid @RequestBody CatPhotoRequest request) {
        return ApiResponse.created(service.addCatPhoto(request));
    }

    /**
     * 删除猫咪照片（志愿者/管理员权限）
     * 
     * @param photoId 照片ID
     */
    @DeleteMapping("/photos/{photoId}")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<Boolean> deleteCatPhoto(@PathVariable String photoId) {
        service.deleteCatPhoto(photoId);
        return ApiResponse.ok(true);
    }

    /**
     * 创建猫咪档案（志愿者/管理员权限）
     * 
     * @param request 创建请求（包含猫咪基本信息）
     */
    @PostMapping
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<Cat> createCat(@Valid @RequestBody CatRequest request) {
        return ApiResponse.created(service.saveCat(request));
    }

    /**
     * 更新猫咪信息（志愿者/管理员权限）
     * 
     * @param catId   猫咪ID
     * @param request 更新请求（包含猫咪基本信息）
     */
    @PutMapping("/{catId}")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<Cat> updateCat(@PathVariable String catId, @Valid @RequestBody CatRequest request) {
        return ApiResponse.ok(service.updateCat(catId, request));
    }

    /**
     * 删除猫咪档案（管理员权限）
     * 
     * @param catId 猫咪ID
     */
    @DeleteMapping("/{catId}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> deleteCat(@PathVariable String catId) {
        service.deleteCat(catId);
        return ApiResponse.ok(true);
    }

    /**
     * 更新猫咪状态（志愿者/管理员权限）
     * 
     * @param catId  猫咪ID
     * @param status 目标状态
     */
    @PatchMapping("/{catId}/status")
    @RequireRole({ Role.VOLUNTEER, Role.ADMIN })
    public ApiResponse<Cat> updateStatus(@PathVariable String catId, @RequestParam CatStatus status) {
        return ApiResponse.ok(service.updateCatStatus(catId, status));
    }

    /**
     * 收藏/取消收藏猫咪
     * 已登录用户可收藏喜欢的猫咪，再次调用则取消收藏
     * 
     * @param catId 猫咪ID
     */
    @PostMapping("/{catId}/favorite")
    public ApiResponse<Boolean> toggleFavorite(@PathVariable String catId) {
        return ApiResponse.ok(service.toggleMyFavorite(catId));
    }

    /**
     * 查询我的收藏猫咪列表
     * 返回当前用户收藏的所有猫咪
     */
    @GetMapping("/favorites")
    public ApiResponse<List<Cat>> favoriteCats() {
        return ApiResponse.ok(service.listMyFavoriteCats());
    }
}