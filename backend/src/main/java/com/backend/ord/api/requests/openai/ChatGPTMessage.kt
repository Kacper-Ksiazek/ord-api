package com.backend.ord.api.requests.openai

class ChatGPTMessage(
    private var role: ChatGPTRole,
    private var content: String
) {
}
