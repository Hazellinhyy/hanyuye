package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.PublicApi;
import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ApplicationDetail;
import com.hfut.cat_adoption_system.dto.LoginRequest;
import com.hfut.cat_adoption_system.dto.LoginResult;
import com.hfut.cat_adoption_system.dto.PasswordUpdateRequest;
import com.hfut.cat_adoption_system.dto.ProfileUpdateRequest;
import com.hfut.cat_adoption_system.dto.RegisterRequest;
import com.hfut.cat_adoption_system.model.ApplicationStatus;
import com.hfut.cat_adoption_system.model.Cat;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.model.User;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final CatAdoptionService service;

    public UserController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    @RequireRole(Role.ADMIN)
    public ApiResponse<List<User>> listUsers() {
        return ApiResponse.ok(service.listUsers());
    }

    @PostMapping
    @PublicApi
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.created(service.register(request));
    }

    @PostMapping("/login")
    @PublicApi
    public ApiResponse<LoginResult> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(service.login(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Boolean> logout() {
        service.logout();
        return ApiResponse.ok(true);
    }

    @GetMapping("/me")
    public ApiResponse<User> me() {
        return ApiResponse.ok(service.currentUser());
    }

    @GetMapping("/me/applications")
    public ApiResponse<List<ApplicationDetail>> myApplications(@RequestParam(required = false) ApplicationStatus status) {
        return ApiResponse.ok(service.listMyApplicationDetails(status));
    }

    @GetMapping("/me/favorites")
    public ApiResponse<List<Cat>> myFavorites() {
        return ApiResponse.ok(service.listMyFavoriteCats());
    }

    @PatchMapping("/me")
    public ApiResponse<User> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ApiResponse.ok(service.updateProfile(request));
    }

    @PatchMapping("/me/password")
    public ApiResponse<Boolean> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        service.updatePassword(request);
        return ApiResponse.ok(true);
    }
}
