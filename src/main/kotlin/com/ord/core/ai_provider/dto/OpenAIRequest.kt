package com.ord.core.ai_provider.dto

enum class OpenAIReasoningEffort(val value: String) {
    NONE("none"),
    MINIMAL("minimal"),
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    XHIGH("xhigh")
}

data class OpenAIRequest(
    val model: String,
    val input: String,
    val instructions: String? = null,
    val temperature: Float,
    val stream: Boolean = false,
//    val reasoning: Map<String, String> = mapOf(
//        "effort" to "low"
//    ),
    val max_output_tokens: Int,

    /** Structured output */
    val text: Map<String, Any>? = null,
) {
    val reasoning = mapOf(
        "effort" to OpenAIReasoningEffort.MINIMAL.value
    )
}
