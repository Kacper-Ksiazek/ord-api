package com.backend.ord.config;

import com.backend.ord.config.properties.OpenAIProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private final OpenAIProperties openAIProperties;

    public RestTemplate openAITemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("Authorization", openAIProperties.getAuthenticationHeaderValue());
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
