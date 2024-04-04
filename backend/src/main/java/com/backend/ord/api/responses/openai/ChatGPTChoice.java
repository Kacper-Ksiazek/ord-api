package com.backend.ord.api.responses.openai;

import com.backend.ord.api.requests.openai.ChatGPTMessage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatGPTChoice {
    private int index;
    private ChatGPTMessage message;
    private Float logprobs;
    private String finish_reason;
}
