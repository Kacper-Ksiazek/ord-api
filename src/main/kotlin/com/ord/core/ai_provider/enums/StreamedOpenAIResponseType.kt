package com.ord.core.ai_provider.enums

import com.fasterxml.jackson.databind.JsonNode

/**
 * Represents the possible response types returned by the OpenAI streamed `/v1/responses` endpoint.
 *
 * This enum is intended solely for use with **streamed responses** and maps to the `"type"` field
 * in each server-sent event (SSE) payload.
 */
enum class StreamedOpenAIResponseType {
    /**
     * Indicates a partial update to the generated output text.
     * Typically emitted as the model generates the response word-by-word or chunk-by-chunk.
     */
    RESPONSE_OUTPUT_TEXT_DELTA,

    /**
     * Signals that the streamed response has finished and no more chunks will follow.
     */
    RESPONSE_COMPLETED,

    /**
     * Signals that an error occurred during the streaming process.
     * The accompanying payload typically contains details under an `error` field.
     */
    RESPONSE_ERROR,

    /**
     * Fallback type used when an unrecognized or unsupported response type is received.
     */
    UNKNOWN;

    /**
     * Extracts the relevant data from the JSON payload depending on the response type.
     *
     * @param node the JSON object for the current streamed event
     * @return extracted content, or null if nothing relevant is found
     */
    fun extractRelevantValue(node: JsonNode): String? = when (this) {
        RESPONSE_OUTPUT_TEXT_DELTA ->
            node.get("delta")?.asText()

        RESPONSE_COMPLETED -> try {
            node.path("response")
                .path("output")
                .firstOrNull()
                ?.path("content")
                ?.firstOrNull()
                ?.path("text")
                ?.asText()
        } catch (e: Exception) {
            null
        }

        else -> null
    }

    companion object {
        /**
         * Maps a raw string received from the OpenAI stream to its corresponding enum constant.
         *
         * @param rawType the raw `type` string from the stream payload
         * @return the matching [StreamedOpenAIResponseType], or [UNKNOWN] if the type is not recognized
         */
        fun fromRawType(rawType: String): StreamedOpenAIResponseType = when (rawType) {
            "response.output_text.delta" -> RESPONSE_OUTPUT_TEXT_DELTA
            "response.completed" -> RESPONSE_COMPLETED
            "response.error" -> RESPONSE_ERROR
            else -> UNKNOWN
        }
    }
}


