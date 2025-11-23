package com.ord.core.ai_provider.services.impl

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.config.properties.OpenAIProperties
import com.ord.core.ai_provider.dto.OpenAIResponse
import com.ord.core.ai_provider.dto.factories.OpenAIRequestFactory
import com.ord.core.ai_provider.dto.helpers.StreamCompletedPayload
import com.ord.core.ai_provider.enums.StreamedOpenAIResponseType
import com.ord.core.ai_provider.services.Emitter
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.core.gpt_tokens_usage.services.GptTokensUsageService
import com.ord.exceptions.REST.BadGatewayException
import com.ord.shared.utils.Console
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import java.util.UUID

@Service
class OpenAIAPIClientServiceImpl(
    private val openAIRequestFactory: OpenAIRequestFactory,
    private val openAIProperties: OpenAIProperties,
    private val webClient: WebClient,
    private val env: Environment,
    private val gptTokensUsageService: GptTokensUsageService
) : OpenAIAPIClientService {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)

    val isTestingEnv: Boolean = env.activeProfiles.contains("test")

    override fun <T> makeRequest(
        aiResponseType: TypeReference<T>,

        prompt: String,

        userId: UUID,
        gptTokensUsageLogKey: String,

        saveLog: (openAIResponse: OpenAIResponse) -> Unit,
        validateResponseBody: (parsedResponseBody: T?) -> Boolean,
        parseResponseBody: (responseBody: T) -> T
    ): Mono<T> {
        val openAIRequest = openAIRequestFactory.createRequest(prompt)

        val enhancedSaveLog: (OpenAIResponse) -> Unit = { openAIResponse ->
            saveLog(openAIResponse)

            gptTokensUsageService.saveTokensUsage(
                userId = userId,
                operationType = gptTokensUsageLogKey,
                inputTokens = openAIResponse.usage.input_tokens,
                outputTokens = openAIResponse.usage.output_tokens
            ).subscribe()
        }

        return attemptRequest(openAIRequest, aiResponseType, enhancedSaveLog, validateResponseBody, parseResponseBody, 0)
    }

    private fun <T> attemptRequest(
        openAIRequest: Any,
        aiResponseType: TypeReference<T>,
        saveLog: (openAIResponse: OpenAIResponse) -> Unit,
        validateResponseBody: (parsedResponseBody: T?) -> Boolean,
        parseResponseBody: (responseBody: T) -> T,
        attemptNumber: Int
    ): Mono<T> {
        if (attemptNumber > openAIProperties.maximumNumberOfOpenAIAPIRequestAttempts) {
            return Mono.error(BadGatewayException("AI service could not generate a valid response after $attemptNumber attempts. Please try again."))
        }

        return webClient.post()
            .uri(openAIProperties.apiUrl)
            .bodyValue(openAIRequest)
            .retrieve()
            .bodyToMono(OpenAIResponse::class.java)
            .doOnNext { response -> saveLog(response) }
            .flatMap { response ->
                val parsedResponseBody = try {
                    parseResponseBody(objectMapper.readValue(response.data, aiResponseType))
                } catch (e: Exception) {
                    Console.printRed("\n\uD83D\uDEA8 [OPENAI REQUEST PARSING ERROR] Exception: ${e.message}")
                    println(e)
                    null
                }

                if (parsedResponseBody != null && validateResponseBody(parsedResponseBody)) {
                    Mono.just(parsedResponseBody)
                } else {
                    attemptRequest(openAIRequest, aiResponseType, saveLog, validateResponseBody, parseResponseBody, attemptNumber + 1)
                }
            }
    }

    override fun openSimpleStringStream(
        prompt: String,

        userId: UUID,
        gptTokensUsageLogKey: String,

        onChunkReceived: (String) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: (Pair<StreamCompletedPayload<String>, Emitter>) -> Unit,
    ): Flux<String> {
        return makeStreamedRequest<String>(
            prompt = prompt,
            onError = onError,
            onComplete = { (payload, emitter) ->
                onComplete(Pair(payload, emitter))

                gptTokensUsageService.saveTokensUsage(
                    userId = userId,
                    operationType = gptTokensUsageLogKey,
                    inputTokens = payload.inputTokens,
                    outputTokens = payload.outputTokens
                ).subscribe()
            },
            onDeltaReceived = { (delta, emitter) ->
                emitter.tryEmitNext(delta)
            }
        )
    }

    override fun <TStreamedItem> openStructuredArrayStream(
        prompt: String,
        streamedItemType: TypeReference<TStreamedItem>,

        userId: UUID,
        gptTokensUsageLogKey: String,

        onItemReceived: (TStreamedItem) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: (() -> Unit)?
    ): Flux<String> {
        var parsingItemBuffer: String = "";

        val separator = OpenAIAPIClientService.STREAMING_CONTENT_SEPARATOR
        val separatorRegex = Regex(Regex.escape(separator))

        return makeStreamedRequest<String>(
            prompt = prompt,
            onError = onError,
            onComplete = { (payload, emitter) ->
                // Parse any remaining content in the buffer (handles the last item without trailing separator)
                if (parsingItemBuffer.isNotBlank()) {
                    try {
                        val parsedItem = parseStreamedItem(parsingItemBuffer, separator, streamedItemType)

                        if (parsedItem != null) {
                            onItemReceived(parsedItem)
                            emitter.tryEmitNext(objectMapper.writeValueAsString(parsedItem))
                        }
                    } catch (e: Exception) {
                        // Log or ignore parsing errors for incomplete buffer
                        Console.printRed("\n⚠️ [STREAMING PARSER] Failed to parse remaining buffer: ${e.message}")
                    }
                }

                // Save token usage but don't emit final content to client
                gptTokensUsageService.saveTokensUsage(
                    userId = userId,
                    operationType = gptTokensUsageLogKey,
                    inputTokens = payload.inputTokens,
                    outputTokens = payload.outputTokens
                ).subscribe()

                // Allow custom onComplete logic if provided
                onComplete?.invoke()
            },
            onDeltaReceived = { (delta, emitter) ->
                parsingItemBuffer += delta

                if (separatorRegex.containsMatchIn(parsingItemBuffer)) {
                    val parsedItem = parseStreamedItem(parsingItemBuffer, separator, streamedItemType)

                    if (parsedItem != null) {
                        onItemReceived(parsedItem)

                        emitter.tryEmitNext(objectMapper.writeValueAsString(parsedItem))
                    }

                    parsingItemBuffer = ""
                }
            }
        )
    }

    // ----
    // Utils
    // ----

    private fun <TStreamedItem> parseStreamedItem(
        buffer: String,
        separator: String,
        streamedItemType: TypeReference<TStreamedItem>
    ): TStreamedItem? {
        return objectMapper.readValue(
            buffer
                .replace(separator, "")
                .replace("\n", "")
                .trim(),
            streamedItemType
        )
    }

    private fun <TStreamedChunk> makeStreamedRequest(
        prompt: String,
        onError: (Throwable) -> Unit,
        onDeltaReceived: (Pair<String, Emitter>) -> Unit,
        onComplete: (Pair<StreamCompletedPayload<String>, Emitter>) -> Unit
    ): Flux<String> {
        val emitter: Emitter = Sinks.many().unicast().onBackpressureBuffer<String>()

        val request = openAIRequestFactory.createRequest(
            prompt = prompt,
            stream = true,
            context = "Return a plain text"
        )

        @Suppress("CallingSubscribeInNonBlockingScope")
        webClient.post()
            .uri(openAIProperties.apiUrl)
            .bodyValue(request)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(String::class.java)
            .doOnError { error ->
                emitter.tryEmitError(error)
                emitter.tryEmitComplete()
                onError(error)
            }
            .doOnNext { chunk ->
                try {
                    val jsonNode = objectMapper.readTree(chunk)
                    val type = StreamedOpenAIResponseType.fromRawType(jsonNode["type"]?.asText()!!)

                    when (type) {
                        StreamedOpenAIResponseType.RESPONSE_OUTPUT_TEXT_DELTA -> {
                            val delta = type.extractRelevantValue(jsonNode)

                            if (delta != null) {
                                onDeltaReceived(Pair(delta, emitter))
                            }
                        }

                        StreamedOpenAIResponseType.RESPONSE_COMPLETED -> {
                            val usage = jsonNode.get("response")?.get("usage")

                            onComplete(
                                Pair(
                                    StreamCompletedPayload(
                                        finalContent = type.extractRelevantValue(jsonNode) ?: "",
                                        inputTokens = usage?.get("input_tokens")?.asInt() ?: 0,
                                        outputTokens = usage?.get("output_tokens")?.asInt() ?: 0
                                    ),
                                    emitter
                                )
                            )
                        }

                        StreamedOpenAIResponseType.RESPONSE_ERROR -> throw Exception()

                        else -> {}

                    }
                } catch (exception: Exception) {
                    println(exception)
                    emitter.tryEmitError(exception)
                    return@doOnNext
                }
            }
            .doOnComplete {
                emitter.tryEmitComplete()
            }
            .subscribe()

        val flux = emitter.asFlux()

        return if (isTestingEnv) {
            // For tests: collect all items on a blocking-friendly scheduler, then emit as Flux
            Flux.defer {
                flux.collectList()
                    .subscribeOn(Schedulers.boundedElastic())
                    .map { list -> list ?: emptyList() }
                    .flatMapMany { Flux.fromIterable(it) }
            }
        } else {
            // For production: stream each chunk
            flux
        }

    }
}