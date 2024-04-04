package com.backend.ord.api.requests.openai;

import com.backend.ord.config.properties.OpenAIProperties;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Builder
@Component
@RequiredArgsConstructor
public class OpenAIRequestFactory {
    private final OpenAIProperties openAIProperties;

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
}


