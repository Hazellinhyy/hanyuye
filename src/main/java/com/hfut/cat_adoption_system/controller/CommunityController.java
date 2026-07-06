package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.PublicApi;
import com.hfut.cat_adoption_system.auth.RequireRole;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.ArticleRequest;
import com.hfut.cat_adoption_system.dto.ForumPostRequest;
import com.hfut.cat_adoption_system.model.Article;
import com.hfut.cat_adoption_system.model.ForumPost;
import com.hfut.cat_adoption_system.model.Role;
import com.hfut.cat_adoption_system.service.CatAdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CatAdoptionService service;

    public CommunityController(CatAdoptionService service) {
        this.service = service;
    }

    @GetMapping("/articles")
    @PublicApi
    public ApiResponse<List<Article>> articles(@RequestParam(defaultValue = "false") boolean includeUnpublished,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.listArticles(includeUnpublished, keyword));
    }

    @GetMapping("/articles/{articleId}")
    @PublicApi
    public ApiResponse<Article> article(@PathVariable Integer articleId) {
        return ApiResponse.ok(service.getArticle(articleId));
    }

    @PostMapping("/articles")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Article> createArticle(@Valid @RequestBody ArticleRequest request) {
        return ApiResponse.created(service.createArticle(request));
    }

    @PutMapping("/articles/{articleId}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Article> updateArticle(@PathVariable Integer articleId,
            @Valid @RequestBody ArticleRequest request) {
        return ApiResponse.ok(service.updateArticle(articleId, request));
    }

    @DeleteMapping("/articles/{articleId}")
    @RequireRole(Role.ADMIN)
    public ApiResponse<Boolean> deleteArticle(@PathVariable Integer articleId) {
        service.deleteArticle(articleId);
        return ApiResponse.ok(true);
    }

    @GetMapping("/posts")
    @PublicApi
    public ApiResponse<List<ForumPost>> posts(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.listPosts(keyword));
    }

    @PostMapping("/posts")
    public ApiResponse<ForumPost> createPost(@Valid @RequestBody ForumPostRequest request) {
        return ApiResponse.created(service.createPost(request));
    }

    @PatchMapping("/posts/{postId}/status")
    public ApiResponse<ForumPost> updatePostStatus(@PathVariable Integer postId, @RequestParam String status) {
        return ApiResponse.ok(service.updatePostStatus(postId, status));
    }
}
