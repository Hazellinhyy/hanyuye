package com.hfut.cat_adoption_system.dto;

/**
 * AI助手聊天响应DTO
 * 
 * 封装AI助手的对话响应结果
 */
public record AgentChatResponse(
                /** AI生成的回答内容 */
                String answer,
                /** AI服务提供商（如OpenAI、文心一言等） */
                String provider,
                /** 使用的AI模型名称 */
                String model,
                /** 是否使用降级方案（主服务不可用时启用备用方案） */
                boolean fallback) {
}