package com.hfut.cat_adoption_system.controller;

import com.hfut.cat_adoption_system.auth.PublicApi;
import com.hfut.cat_adoption_system.common.ApiResponse;
import com.hfut.cat_adoption_system.dto.AgentChatRequest;
import com.hfut.cat_adoption_system.dto.AgentChatResponse;
import com.hfut.cat_adoption_system.service.AdoptionAgentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * 认养AI助手控制器
 * 
 * 提供AI智能助手的聊天功能，支持普通响应和流式响应两种模式：
 * - 普通聊天：一次性返回完整回答
 * - 流式聊天：逐字返回回答，提升用户体验
 * 
 * 所有接口均为公开接口，无需登录即可访问
 */
@RestController
@RequestMapping("/api/agent")
public class AdoptionAgentController {

    /** AI助手服务：处理聊天逻辑和对话生成 */
    private final AdoptionAgentService service;

    /**
     * 构造函数：注入AI助手服务
     */
    public AdoptionAgentController(AdoptionAgentService service) {
        this.service = service;
    }

    /**
     * AI助手普通聊天接口
     * 用户发送问题，AI一次性返回完整回答
     * 
     * @param request 聊天请求（包含对话历史和当前问题）
     */
    @PostMapping("/adoption-chat")
    @PublicApi
    public ApiResponse<AgentChatResponse> adoptionChat(@Valid @RequestBody AgentChatRequest request) {
        return ApiResponse.ok(service.chat(request));
    }

    /**
     * AI助手流式聊天接口
     * 用户发送问题，AI逐字返回回答，实现打字机效果
     * 
     * @param request 聊天请求（包含对话历史和当前问题）
     * @return StreamingResponseBody 流式响应体
     */
    @PostMapping(value = "/adoption-chat-stream", produces = "text/plain;charset=UTF-8")
    @PublicApi
    public StreamingResponseBody adoptionChatStream(@Valid @RequestBody AgentChatRequest request) {
        return outputStream -> {
            try {
                // 调用流式聊天服务，将结果写入输出流
                service.streamChat(request, outputStream);
            } catch (Exception error) {
                // 发生异常时刷新输出流，确保客户端能收到完整响应
                outputStream.flush();
            }
        };
    }
}