package com.backend.ord.api.responses.openai.embedded

data class OpenAIResponseTokensUsage(
    val input_tokens: Int,
    val output_tokens: Int,
    val total_tokens: Int
)

data class OpenAIResponseOutputContent(
    val type: String,
    val text: String,
)

data class OpenAIResponseOutput(
    val id: String,
    val type: String,
    val status: String,
    val role: String,
    val content: List<OpenAIResponseOutputContent>,
)

class OpenAIResponse(
    val id: String?,

    val `object`: String,
    val status: String,
    val usage: OpenAIResponseTokensUsage,
    val output: List<OpenAIResponseOutput>,

    val created_at: Int
) {
    val data: String
        get() = output[0].content[0].text
}
