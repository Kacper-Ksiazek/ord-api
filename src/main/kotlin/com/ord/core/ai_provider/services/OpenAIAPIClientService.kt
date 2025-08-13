package com.ord.core.ai_provider.services

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.dto.OpenAIResponse
import com.ord.core.ai_provider.dto.helpers.StreamCompletedPayload
import reactor.core.publisher.Sinks

typealias Emitter = Sinks.Many<String>

interface OpenAIAPIClientService {
    companion object {
        const val STREAMING_CONTENT_SEPARATOR: String = "[[BREAK]]"
    }

    fun <T> makeRequest(
        aiResponseTypeReference: TypeReference<T>,

        prompt: String,

        saveLog: (openAIResponse: OpenAIResponse) -> Unit,
        validateResponseBody: (parsedResponseBody: T?) -> Boolean = { it != null },
        parseResponseBody: (responseBody: T) -> T = { it }
    ): T

    fun openSimpleStringStream(
        prompt: String,
        onChunkReceived: (String) -> Unit = {},
        onError: (Throwable) -> Unit = { throw it },
        onComplete: (Pair<StreamCompletedPayload<String>, Emitter>) -> Unit,
    ): Emitter

    fun <TStreamedItem> openStructuredArrayStream(
        prompt: String,
        streamedItemTypeReference: TypeReference<TStreamedItem>,
        onItemReceived: (TStreamedItem) -> Unit = {},
        onError: (Throwable) -> Unit = { throw it },
        onComplete: (Pair<StreamCompletedPayload<List<TStreamedItem>>, Emitter>) -> Unit,
    ): Emitter
}
