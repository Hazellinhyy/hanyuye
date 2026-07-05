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

@RestController
@RequestMapping("/api/admin/clues")
@RequireRole({Role.VOLUNTEER, Role.ADMIN})
public class AdminClueController {
    private final CatAdoptionService service;

    public AdminClueController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<Clue>> listClues(@RequestParam(required = false) String status,
                                             @RequestParam(required = false) String urgencyLevel,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(service.listAdminClues(status, urgencyLevel, keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Clue> getClue(@PathVariable String id) {
        return ApiResponse.ok(service.getAdminClue(id));
    }

    @PutMapping("/{id}/verify")
    public ApiResponse<Clue> verifyClue(@PathVariable String id,
                                        @Valid @RequestBody ClueVerifyRequest request) {
        return ApiResponse.ok(service.verifyClue(id, request));
    }

    @PostMapping("/{id}/create-cat")
    public ApiResponse<Cat> createCat(@PathVariable String id,
                                      @RequestBody CreateCatFromClueRequest request) {
        return ApiResponse.created(service.createCatFromClue(id, request));
    }

    @PutMapping("/{id}/invalid")
    public ApiResponse<Clue> invalid(@PathVariable String id,
                                     @Valid @RequestBody ActionReasonRequest request) {
        return ApiResponse.ok(service.markClueInvalid(id, request));
    }

    @DeleteMapping("/{id}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> delete(@PathVariable String id,
                                       @Valid @RequestBody ActionReasonRequest request) {
        service.deleteAdminClue(id, request);
        return ApiResponse.ok(true);
    }
}
