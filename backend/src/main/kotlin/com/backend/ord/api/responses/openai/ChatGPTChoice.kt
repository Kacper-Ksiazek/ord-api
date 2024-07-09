package com.backend.ord.api.responses.openai

import com.backend.ord.api.requests.openai.ChatGPTMessage

class ChatGPTChoice(
    var index: Int,
    var message: ChatGPTMessage,
    var logprobs: Float,
    var finish_reason: String
) {
}
