package com.backend.ord.api.responses.openai;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
class Usage {
    private int total_tokens;
    private int prompt_tokens;
    private int completion_tokens;
}

@Data
@Builder
public class OpenAIResponse {
    private String id;
    private String object;
    private int created;
    private String model;

    private List<ChatGPTChoice> choices;

    private Usage usage;

    private String system_fingerprint;
}
