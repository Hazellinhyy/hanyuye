package com.hfut.cat_adoption_system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 图像识别模型配置属性类
 * 
 * 通过 @ConfigurationProperties 注解自动绑定配置文件中以 "recognition.model" 为前缀的配置项。
 * 用于配置猫咪图像识别服务的相关参数，支持远程模型服务和本地降级两种模式。
 */
@Component
@ConfigurationProperties(prefix = "recognition.model")
public class RecognitionModelProperties {

    /** 是否启用图像识别功能 */
    private boolean enabled = true;

    /** 识别模型服务的远程端点地址 */
    private String endpoint;

    /** 访问远程模型服务所需的API密钥 */
    private String apiKey;

    /** 模型名称，默认为 OpenAI 的 CLIP-ViT-B/32 模型 */
    private String modelName = "openai/clip-vit-base-patch32";

    /** 连接超时时间（毫秒），默认2500ms */
    private int connectTimeoutMillis = 2500;

    /** 读取超时时间（毫秒），默认8000ms */
    private int readTimeoutMillis = 8000;

    /** 远程服务不可用时是否降级到本地处理 */
    private boolean fallbackToLocal = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public boolean isFallbackToLocal() {
        return fallbackToLocal;
    }

    public void setFallbackToLocal(boolean fallbackToLocal) {
        this.fallbackToLocal = fallbackToLocal;
    }
}