package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.FollowupRequest;
import com.hfut.cat_adoption_system.model.FollowupRecord;
import com.hfut.cat_adoption_system.model.FollowupResult;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/followups")
public class FollowupController {
    private final CatAdoptionService service;

    public FollowupController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<List<FollowupRecord>> listFollowups(@org.springframework.web.bind.annotation.RequestParam(required = false) String applicationId,
                                                           @org.springframework.web.bind.annotation.RequestParam(required = false) FollowupResult result) {
        return ApiResponse.ok(service.listFollowups(applicationId, result));
    }

    @PostMapping
    @RequireRole({Role.VOLUNTEER, Role.ADMIN})
    public ApiResponse<FollowupRecord> addFollowup(@Valid @RequestBody FollowupRequest request) {
        return ApiResponse.created(service.addFollowup(request));
    }
}
