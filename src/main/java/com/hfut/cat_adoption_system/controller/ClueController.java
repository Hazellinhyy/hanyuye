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

@RestController
public class ClueController {
    private final CatAdoptionService service;

    public ClueController(CatAdoptionService service) {
        this.service = service;
    }

    @PostMapping("/api/clues")
    @RequireRole(Role.STUDENT)
    public ApiResponse<Clue> submitClue(@Valid @RequestBody ClueSubmitRequest request) {
        return ApiResponse.created(service.submitClue(request));
    }

    @GetMapping("/api/my/clues")
    @RequireRole(Role.STUDENT)
    public ApiResponse<List<Clue>> myClues(@RequestParam(required = false) String status) {
        return ApiResponse.ok(service.listMyClues(status));
    }
}
