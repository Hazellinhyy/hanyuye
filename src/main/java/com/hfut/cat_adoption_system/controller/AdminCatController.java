package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.CatRequest;
import com.hfut.cat_adoption_system.dto.CatStatusChangeRequest;
import com.hfut.cat_adoption_system.dto.CatTimelineEvent;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.CatStatus;
import com.hfut.cat_adoption_system.model.HealthLevel;
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

@RestController
@RequestMapping("/api/admin/cats")
public class AdminCatController {
    private final CatAdoptionService service;

    public AdminCatController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    @RequireRole({Role.VOLUNTEER, Role.HOSPITAL, Role.ADMIN})
    public ApiResponse<List<Cat>> listCats(@RequestParam(required = false) CatStatus status,
                                           @RequestParam(required = false) HealthLevel healthLevel,
                                           @RequestParam(required = false) String gender,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(service.listAdminCats(status, healthLevel, gender, keyword, page, size));
    }

    @GetMapping("/{id}")
    @RequireRole({Role.VOLUNTEER, Role.HOSPITAL, Role.ADMIN})
    public ApiResponse<Cat> getCat(@PathVariable String id) {
        return ApiResponse.ok(service.getCat(id));
    }

    @GetMapping("/{id}/timeline")
    @RequireRole({Role.VOLUNTEER, Role.HOSPITAL, Role.ADMIN})
    public ApiResponse<List<CatTimelineEvent>> timeline(@PathVariable String id) {
        return ApiResponse.ok(service.catTimeline(id));
    }

    @PostMapping
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<Cat> createCat(@Valid @RequestBody CatRequest request) {
        return ApiResponse.created(service.saveCat(request));
    }

    @PutMapping("/{id}")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<Cat> updateCat(@PathVariable String id, @Valid @RequestBody CatRequest request) {
        return ApiResponse.ok(service.updateCat(id, request));
    }

    @PutMapping("/{id}/status")
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<Cat> changeStatus(@PathVariable String id,
                                         @Valid @RequestBody CatStatusChangeRequest request) {
        return ApiResponse.ok(service.changeCatStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> deleteCat(@PathVariable String id) {
        service.deleteCat(id);
        return ApiResponse.ok(true);
    }
}
