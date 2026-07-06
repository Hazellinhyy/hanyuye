package com.hfut.cat_adoption_system.dto;

import jakarta.validation.constraints.Size;

/**
 * AI助手对话轮次DTO
 * 
 * 表示对话中的单轮消息，用于构建上下文对话历史
 */
public record AgentChatTurn(
                /** 角色标识（如：user表示用户，assistant表示AI助手） */
                String role,
                /** 消息内容，最大1000字符 */
                @Size(max = 1000) String content) {
}