package com.ord.stubs.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.ord.core.ai_provider.dto.MessageOutput
import com.ord.core.ai_provider.dto.OpenAIResponse
import com.ord.core.ai_provider.dto.OpenAIResponseOutputContent
import com.ord.core.ai_provider.dto.OpenAIResponseTokensUsage
import com.ord.core.ai_provider.dto.helpers.StreamCompletedPayload
import com.ord.core.ai_provider.services.Emitter
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.gpt_tokens_usage.services.GptTokensUsageService
import com.ord.exceptions.REST.BadGatewayException
import com.ord.shared.prompts.Prompt
import com.ord.shared.prompts.structured_outputs.base.StructuredOutputTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import java.util.UUID

/**
 * Fixture-driven OpenAI client used in smoke tests and the `e2e` runtime profile.
 * Never calls the real OpenAI HTTP API.
 */
class StubOpenAIAPIClientService(
    private val gptTokensUsageService: GptTokensUsageService,
    private val fixtureLoader: AIFixtureLoader,
    private val dynamicBuilder: AIFixtureDynamicBuilder,
) : OpenAIAPIClientService {

    override fun <T> makeRequest(
        aiResponseType: TypeReference<T>,
        prompt: String,
        userId: UUID,
        gptTokensUsageLogKey: String,
        structuredOutput: StructuredOutputTemplate?,
        saveLog: (OpenAIResponse) -> Unit,
        validateResponseBody: (parsedResponseBody: T?) -> Boolean,
        parseResponseBody: (responseBody: T) -> T,
    ): Mono<T> {
        val rawBody = resolveStructuredBody(gptTokensUsageLogKey, prompt, aiResponseType)
        @Suppress("UNCHECKED_CAST")
        val parsedBody = parseResponseBody(rawBody as T)

        if (!validateResponseBody(parsedBody)) {
            return Mono.error(
                BadGatewayException("Stub AI fixture failed validation for operation: $gptTokensUsageLogKey")
            )
        }

        saveLog(createStubOpenAIResponse())
        saveTokenUsage(userId, gptTokensUsageLogKey)

        return Mono.just(parsedBody)
    }

    override fun <T> makeRequest(
        aiResponseType: TypeReference<T>,
        prompt: Prompt,
        userId: UUID,
        gptTokensUsageLogKey: String,
        saveLog: (OpenAIResponse) -> Unit,
        validateResponseBody: (T?) -> Boolean,
        parseResponseBody: (T) -> T,
    ): Mono<T> {
        return makeRequest(
            aiResponseType = aiResponseType,
            prompt = prompt.toString(),
            userId = userId,
            gptTokensUsageLogKey = gptTokensUsageLogKey,
            structuredOutput = prompt.variant.structuredOutput,
            saveLog = saveLog,
            validateResponseBody = validateResponseBody,
            parseResponseBody = parseResponseBody,
        )
    }

    override fun openSimpleStringStream(
        prompt: String,
        userId: UUID,
        gptTokensUsageLogKey: String,
        onChunkReceived: (String) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: (Pair<StreamCompletedPayload<String>, Emitter>) -> Unit,
    ): Flux<String> {
        val fixture = fixtureLoader.loadStringStream(gptTokensUsageLogKey)
        val emitter: Emitter = Sinks.many().unicast().onBackpressureBuffer<String>()

        fixture.chunks.forEach { chunk ->
            onChunkReceived(chunk)
            emitter.tryEmitNext(chunk)
        }

        val payload = StreamCompletedPayload(
            finalContent = fixture.chunks.joinToString(""),
            inputTokens = fixture.inputTokens,
            outputTokens = fixture.outputTokens,
        )

        onComplete(Pair(payload, emitter))
        saveTokenUsage(userId, gptTokensUsageLogKey, fixture.inputTokens, fixture.outputTokens)
        emitter.tryEmitComplete()

        return collectForTests(emitter.asFlux())
    }

    override fun <TStreamedItem> openStructuredArrayStream(
        prompt: String,
        streamedItemType: TypeReference<TStreamedItem>,
        userId: UUID,
        gptTokensUsageLogKey: String,
        onItemReceived: (TStreamedItem) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: (() -> Unit)?,
    ): Flux<String> {
        val fixture = dynamicBuilder.buildArrayStream(gptTokensUsageLogKey, prompt)
            ?: fixtureLoader.loadArrayStream(gptTokensUsageLogKey)
        val emitter: Emitter = Sinks.many().unicast().onBackpressureBuffer<String>()

        fixture.items.forEach { itemNode ->
            val serialized = fixtureLoader.serializeItem(itemNode)
            @Suppress("UNCHECKED_CAST")
            val parsedItem = fixtureLoader.loadStructuredFromJson(serialized, streamedItemType)
            onItemReceived(parsedItem)
            emitter.tryEmitNext(serialized)
        }

        saveTokenUsage(userId, gptTokensUsageLogKey, fixture.inputTokens, fixture.outputTokens)
        onComplete?.invoke()
        emitter.tryEmitComplete()

        return collectForTests(emitter.asFlux())
    }

    private fun <T> resolveStructuredBody(
        operationKey: String,
        prompt: String,
        typeReference: TypeReference<T>,
    ): T {
        val dynamicBody = dynamicBuilder.buildStructured(operationKey, prompt, typeReference)
        if (dynamicBody != null) {
            @Suppress("UNCHECKED_CAST")
            return dynamicBody as T
        }

        return fixtureLoader.loadStructured(operationKey, typeReference)
    }

    private fun saveTokenUsage(
        userId: UUID,
        operationType: String,
        inputTokens: Int = 42,
        outputTokens: Int = 18,
    ) {
        gptTokensUsageService.saveTokensUsage(
            userId = userId,
            operationType = operationType,
            inputTokens = inputTokens,
            outputTokens = outputTokens,
        ).subscribe()
    }

    private fun createStubOpenAIResponse(): OpenAIResponse {
        return OpenAIResponse(
            id = "stub-openai-response",
            `object` = "response",
            status = "completed",
            usage = OpenAIResponseTokensUsage(
                input_tokens = 42,
                output_tokens = 18,
                total_tokens = 60,
            ),
            output = listOf(
                MessageOutput(
                    id = "msg_stub",
                    type = "message",
                    status = "completed",
                    role = "assistant",
                    content = listOf(
                        OpenAIResponseOutputContent(
                            type = "output_text",
                            text = "{}",
                        )
                    ),
                )
            ),
        )
    }

    private fun collectForTests(flux: Flux<String>): Flux<String> {
        return Flux.defer {
            flux.collectList()
                .subscribeOn(Schedulers.boundedElastic())
                .map { list -> list ?: emptyList() }
                .flatMapMany { Flux.fromIterable(it) }
        }
    }
}
