package com.ord.core.ai_provider.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenAIResponseTokensUsage(
    val input_tokens: Int,
    val output_tokens: Int,
    val total_tokens: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenAIResponseOutputContent(
    val type: String,
    val text: String,
    val annotations: List<Any>? = null
)

// Sealed interface for polymorphic deserialization
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ReasoningOutput::class, name = "reasoning"),
    JsonSubTypes.Type(value = MessageOutput::class, name = "message")
)
sealed interface OpenAIOutputItem {
    val id: String
    val type: String
}

// Reasoning output (no status field)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ReasoningOutput(
    override val id: String,
    override val type: String,
    val summary: List<Any>? = null
) : OpenAIOutputItem

// Message output (has status field and content)
@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageOutput(
    override val id: String,
    override val type: String,
    val status: String,
    val role: String,
    val content: List<OpenAIResponseOutputContent>
) : OpenAIOutputItem

@JsonIgnoreProperties(ignoreUnknown = true)
class OpenAIResponse(
    val id: String?,
    val `object`: String,
    val status: String,
    val usage: OpenAIResponseTokensUsage,
    val output: List<OpenAIOutputItem>,
    val error: Any? = null,
    val model: String? = null,
    val text: Map<String, Any?>? = null,
) {
    val data: String
        get() {
            // Find the first MessageOutput in the output list
            val messageOutput = output.filterIsInstance<MessageOutput>().firstOrNull()
                ?: throw IllegalStateException("No MessageOutput found in response")
            return messageOutput.content[0].text
        }
}
