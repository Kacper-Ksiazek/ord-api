package com.backend.ord.api.responses.openai


class OpenAIResponse(
    val id: String?,
    val `object`: String,
    val created: Int,
    val model: String,
    val choices: List<ChatGPTChoice>,
    val usage: Usage,
    val system_fingerprint: String?
) {
    val actualResponse: String
        get() = choices[0].message.content;

    class Usage(
        val total_tokens: Int,
        val prompt_tokens: Int,
        val completion_tokens: Int
    )
}
