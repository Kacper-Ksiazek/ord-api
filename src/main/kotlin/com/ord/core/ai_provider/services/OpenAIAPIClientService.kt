package com.ord.core.ai_provider.services

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate
import com.ord.core.ai_provider.dto.OpenAIResponse
import com.ord.core.ai_provider.dto.helpers.StreamCompletedPayload
import com.ord.shared.prompts.Prompt
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.util.UUID

typealias Emitter = Sinks.Many<String>

interface OpenAIAPIClientService {
    companion object {
        const val STREAMING_CONTENT_SEPARATOR: String = "[[BREAK]]"
    }

    fun <T> makeRequest(
        aiResponseType: TypeReference<T>,

        prompt: String,

        userId: UUID,
        gptTokensUsageLogKey: String,
        structuredOutput: StructuredOutputTemplate? = null,

        saveLog: (openAIResponse: OpenAIResponse) -> Unit = {},
        validateResponseBody: (parsedResponseBody: T?) -> Boolean = { it != null },
        parseResponseBody: (responseBody: T) -> T = { it }
    ): Mono<T>

    fun <T> makeRequest(
        aiResponseType: TypeReference<T>,

        prompt: Prompt,

        userId: UUID,
        gptTokensUsageLogKey: String,

        saveLog: (openAIResponse: OpenAIResponse) -> Unit = {},
        validateResponseBody: (parsedResponseBody: T?) -> Boolean = { it != null },
        parseResponseBody: (responseBody: T) -> T = { it }
    ): Mono<T>

    fun openSimpleStringStream(
        prompt: String,

        userId: UUID,
        gptTokensUsageLogKey: String,

        onChunkReceived: (String) -> Unit = {},
        onError: (Throwable) -> Unit = { throw it },
        onComplete: (Pair<StreamCompletedPayload<String>, Emitter>) -> Unit,
    ): Flux<String>

    fun <TStreamedItem> openStructuredArrayStream(
        prompt: String,
        streamedItemType: TypeReference<TStreamedItem>,

        userId: UUID,
        gptTokensUsageLogKey: String,

        onItemReceived: (TStreamedItem) -> Unit = {},
        onError: (Throwable) -> Unit = { throw it },
        onComplete: (() -> Unit)? = null
    ): Flux<String>
}
