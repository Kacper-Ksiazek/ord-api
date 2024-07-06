package com.backend.ord.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAIProperties {
    private String apiKey;
    private String apiUrl;
    private String gptModel;
    private int maxTokens;
    private float temperature;

    public String getAuthenticationHeaderValue() {
        return "Bearer " + apiKey;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public String getApiUrl() {
        return this.apiUrl;
    }

    public String getGptModel() {
        return this.gptModel;
    }

    public int getMaxTokens() {
        return this.maxTokens;
    }

    public float getTemperature() {
        return this.temperature;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setGptModel(String gptModel) {
        this.gptModel = gptModel;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
