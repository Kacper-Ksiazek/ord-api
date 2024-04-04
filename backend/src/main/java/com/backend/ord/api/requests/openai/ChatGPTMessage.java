package com.backend.ord.api.requests.openai;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChatGPTMessage {
    private ChatGPTRole role;
    private String content;
}
