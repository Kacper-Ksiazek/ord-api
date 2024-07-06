package com.backend.ord.config;

import com.backend.ord.api.requests.openai.OpenAIRequest;
import com.backend.ord.api.responses.openai.OpenAIResponse;
import com.backend.ord.config.properties.OpenAIProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {
    private final OpenAIProperties openAIProperties;

    public RestClientConfig(OpenAIProperties openAIProperties) {
        this.openAIProperties = openAIProperties;
    }

    public RestTemplate openAITemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("Authorization", openAIProperties.getAuthenticationHeaderValue());
            return execution.execute(request, body);
        });

        return restTemplate;
    }

    public OpenAIResponse makeOpenAIPostRequest(OpenAIRequest request) {
        return openAITemplate().postForObject(
                openAIProperties.getApiUrl(),
                request,
                OpenAIResponse.class
        );
    }

}
