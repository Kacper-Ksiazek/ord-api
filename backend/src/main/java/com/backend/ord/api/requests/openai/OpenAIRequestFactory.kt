package com.backend.ord.api.requests.openai;

import com.backend.ord.config.properties.OpenAIProperties;
import org.springframework.stereotype.Component;

@Component
public class OpenAIRequestFactory {
    private final OpenAIProperties openAIProperties;

    public OpenAIRequestFactory(OpenAIProperties openAIProperties) {
        this.openAIProperties = openAIProperties;
    }

    public static OpenAIRequestFactoryBuilder builder() {
        return new OpenAIRequestFactoryBuilder();
    }

    public OpenAIRequest createRequest(final String prompt, final String context) {
        return OpenAIRequest.builder()
                .model(openAIProperties.getGptModel())
                .temperature(openAIProperties.getTemperature())
                .max_tokens(openAIProperties.getMaxTokens())
                .messages(new ChatGPTMessage[]{
                        // The user message
                        ChatGPTMessage.builder()
                                .role(ChatGPTRole.user)
                                .content(prompt)
                                .build(),
                        // The assistant message
                        ChatGPTMessage.builder()
                                .role(ChatGPTRole.assistant)
                                .content(context)
                                .build()
                })
                .build();
    }

    public OpenAIRequest createRequest(final String prompt) {
        return OpenAIRequest.builder()
                .model(openAIProperties.getGptModel())
                .temperature(openAIProperties.getTemperature())
                .max_tokens(openAIProperties.getMaxTokens())
                .messages(new ChatGPTMessage[]{
                        // The user message
                        ChatGPTMessage.builder()
                                .role(ChatGPTRole.user)
                                .content(prompt)
                                .build(),
                })
                .build();
    }

    public static class OpenAIRequestFactoryBuilder {
        private OpenAIProperties openAIProperties;

        OpenAIRequestFactoryBuilder() {
        }

        public OpenAIRequestFactoryBuilder openAIProperties(OpenAIProperties openAIProperties) {
            this.openAIProperties = openAIProperties;
            return this;
        }

        public OpenAIRequestFactory build() {
            return new OpenAIRequestFactory(this.openAIProperties);
        }

        public String toString() {
            return "OpenAIRequestFactory.OpenAIRequestFactoryBuilder(openAIProperties=" + this.openAIProperties + ")";
        }
    }
}


