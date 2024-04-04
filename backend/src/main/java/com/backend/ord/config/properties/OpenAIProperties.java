package com.backend.ord.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "openai")
@Getter
@Setter
public class OpenAIProperties {
    private String apiKey;
    private String apiUrl;
    private String gptModel;
    private int maxTokens;
    private float temperature;

    public String getAuthenticationHeaderValue() {
        return "Bearer " + apiKey;
    }
}
