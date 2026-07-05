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

@RestController
@RequestMapping("/api/cats")
public class CatController {
    private final CatAdoptionService service;

    public CatController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    @PublicApi
    public ApiResponse<List<Cat>> listCats(@RequestParam(required = false) CatStatus status,
                                           @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.listCats(status, keyword));
    }

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

    @GetMapping("/public/{catId}")
    @PublicApi
    public ApiResponse<Cat> publicCat(@PathVariable String catId) {
        return ApiResponse.ok(service.getPublicCat(catId));
    }

    @GetMapping("/{catId}/timeline")
    @PublicApi
    public ApiResponse<List<CatTimelineEvent>> timeline(@PathVariable String catId) {
        return ApiResponse.ok(service.catTimeline(catId));
    }

    @GetMapping("/{catId}")
    @PublicApi
    public ApiResponse<Cat> getCat(@PathVariable String catId) {
        return ApiResponse.ok(service.getCat(catId));
    }

    @GetMapping("/{catId}/photos")
    @PublicApi
    public ApiResponse<List<CatPhoto>> listCatPhotos(@PathVariable String catId) {
        return ApiResponse.ok(service.listCatPhotos(catId));
    }

    @GetMapping("/photos")
    @PublicApi
    public ApiResponse<List<CatPhoto>> listAllCatPhotos() {
        return ApiResponse.ok(service.listAllCatPhotos());
    }

    @PostMapping("/photos")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<CatPhoto> addCatPhoto(@Valid @RequestBody CatPhotoRequest request) {
        return ApiResponse.created(service.addCatPhoto(request));
    }

    @DeleteMapping("/photos/{photoId}")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<Boolean> deleteCatPhoto(@PathVariable String photoId) {
        service.deleteCatPhoto(photoId);
        return ApiResponse.ok(true);
    }

    @PostMapping
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<Cat> createCat(@Valid @RequestBody CatRequest request) {
        return ApiResponse.created(service.saveCat(request));
    }

    @PutMapping("/{catId}")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<Cat> updateCat(@PathVariable String catId, @Valid @RequestBody CatRequest request) {
        return ApiResponse.ok(service.updateCat(catId, request));
    }

    @DeleteMapping("/{catId}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> deleteCat(@PathVariable String catId) {
        service.deleteCat(catId);
        return ApiResponse.ok(true);
    }

    @PatchMapping("/{catId}/status")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<Cat> updateStatus(@PathVariable String catId, @RequestParam CatStatus status) {
        return ApiResponse.ok(service.updateCatStatus(catId, status));
    }

    @PostMapping("/{catId}/favorite")
    public ApiResponse<Boolean> toggleFavorite(@PathVariable String catId) {
        return ApiResponse.ok(service.toggleMyFavorite(catId));
    }

    @GetMapping("/favorites")
    public ApiResponse<List<Cat>> favoriteCats() {
        return ApiResponse.ok(service.listMyFavoriteCats());
    }
}
