package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * AI助手聊天请求DTO
 * 
 * 用于与AI助手进行对话的请求参数，支持上下文对话
 */
public record AgentChatRequest(
                /** 用户消息内容，必填字段，最大800字符 */
                @NotBlank @Size(max = 800) String message,
                /** 聊天历史记录（用于上下文对话） */
                List<AgentChatTurn> history) {
}