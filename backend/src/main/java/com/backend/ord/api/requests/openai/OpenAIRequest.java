package com.backend.ord.api.requests.openai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAIRequest {
    private String model;
    private ChatGPTMessage[] messages;
    private float temperature;
    private int max_tokens;

    private final int top_p = 1;
    private final int frequency_penalty = 0;
    private final int presence_penalty = 0;

}
