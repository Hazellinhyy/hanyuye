package com.hfut.cat_adoption_system.config;

import com.hfut.cat_adoption_system.auth.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * Web MVC 配置类
 * 
 * 实现 WebMvcConfigurer 接口，用于配置 Spring MVC 的各项功能，包括：
 * - 注册认证拦截器，保护 API 接口
 * - 配置静态资源映射，允许前端访问上传的文件
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** 认证拦截器：用于对 API 请求进行身份认证和权限校验 */
    private final AuthInterceptor authInterceptor;

    /**
     * 构造函数：注入认证拦截器
     */
    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    /**
     * 注册拦截器
     * 将认证拦截器应用到所有 /api/** 路径，实现对 API 的统一认证保护
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**");
    }

    /**
     * 配置资源处理器
     * 将上传文件目录映射为可访问的静态资源路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射线索图片上传目录
        String clueUploadLocation = Path.of("uploads", "clues").toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/clues/**")
                .addResourceLocations(clueUploadLocation);

        // 映射通知公告附件上传目录
        String noticeUploadLocation = Path.of("uploads", "notices").toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/notices/**")
                .addResourceLocations(noticeUploadLocation);
    }
}
