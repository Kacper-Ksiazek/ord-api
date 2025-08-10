package com.ord.core.ai_provider.services.impl

import com.ord.config.RestClientConfig
import com.ord.config.properties.OpenAIProperties
import com.ord.core.ai_provider.dto.OpenAIResponse
import com.ord.core.ai_provider.dto.factories.OpenAIRequestFactory
import com.ord.core.ai_provider.services.OpenAIAPIClientService
import com.ord.exceptions.REST.BadGatewayException
import com.ord.shared.utils.Console
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.ai_provider.dto.helpers.StreamCompletedPayload
import com.ord.core.ai_provider.enums.StreamedOpenAIResponseType
import com.ord.core.ai_provider.services.Emitter
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Sinks

@Service
class OpenAIAPIClientServiceImpl(
    private val openAIRequestFactory: OpenAIRequestFactory,
    private val restClientConfig: RestClientConfig,
    private val openAIProperties: OpenAIProperties,
    private val webClient: WebClient,
) : OpenAIAPIClientService {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)


    override fun <T> makeRequest(
        aiResponseTypeReference: TypeReference<T>,

        prompt: String,

        saveLog: (openAIResponse: OpenAIResponse) -> Unit,
        validateResponseBody: (parsedResponseBody: T?) -> Boolean,
        parseResponseBody: (responseBody: T) -> T
    ): T {
        val openAIRequest = openAIRequestFactory.createRequest(prompt)

        var response: OpenAIResponse
        var parsedResponseBody: T?

        var numberOfAttempts = 0

        do {
            trackOpenAIAPIRequestAttempt(numberOfAttempts++)

            response = restClientConfig
                .makeOpenAIPostRequest(openAIRequest)
                .also { saveLog(it) }

            parsedResponseBody = try {
                parseResponseBody(objectMapper.readValue(response.data, aiResponseTypeReference))
            } catch (e: Exception) {
                // TODO: Hide behind "debug" feature flag
                Console.printRed("\n\uD83D\uDEA8 [OPENAI REQUEST PARSING ERROR] Exception: ${e.message}")
                println(e)
                null
            }
        } while (parsedResponseBody === null || !validateResponseBody(parsedResponseBody))

        return parsedResponseBody
    }

    override fun openSimpleStringStream(
        prompt: String,
        onChunkReceived: (String) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: (Pair<StreamCompletedPayload<String>, Emitter>) -> Unit,
    ): Emitter {
        return makeStreamedRequest<String>(
            prompt = prompt,
            onError = onError,
            onComplete = onComplete,
            onDeltaReceived = { (delta, emitter) ->
                emitter.tryEmitNext(delta)
            }
        )
    }

    override fun <TStreamedItem> openStructuredArrayStream(
        prompt: String,
        streamedItemTypeReference: TypeReference<TStreamedItem>,
        onItemReceived: (TStreamedItem) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: (Pair<StreamCompletedPayload<List<TStreamedItem>>, Emitter>) -> Unit,
    ): Emitter {
        var parsingItemBuffer: String = "";

        val resultItems: MutableList<TStreamedItem> = mutableListOf()

        val separator = OpenAIAPIClientService.STREAMING_CONTENT_SEPARATOR
        val separatorRegex = Regex(Regex.escape(separator))

        return makeStreamedRequest<String>(
            prompt = prompt,
            onError = onError,
            onComplete = { (payload, emitter) ->
                val result = StreamCompletedPayload<List<TStreamedItem>>(
                    finalContent = resultItems,
                    inputTokens = payload.inputTokens,
                    outputTokens = payload.outputTokens
                )

                onComplete(Pair(result, emitter))
            },
            onDeltaReceived = { (delta, emitter) ->
                parsingItemBuffer += delta

                if (separatorRegex.containsMatchIn(parsingItemBuffer)) {
                    val parsedItem = objectMapper.readValue(
                        parsingItemBuffer
                            .replace(separator, "")
                            .replace("\n", "")
                            .trim(),
                        streamedItemTypeReference
                    )


                    if (parsedItem != null) {
                        onItemReceived(parsedItem)
                        resultItems.add(parsedItem)

                        emitter.tryEmitNext(objectMapper.writeValueAsString(parsedItem))
                    }

                    parsingItemBuffer = ""
                }
            }
        )
    }

    private fun trackOpenAIAPIRequestAttempt(attempt: Int) {
        if (attempt > openAIProperties.maximumNumberOfOpenAIAPIRequestAttempts) {
            throw BadGatewayException("AI service could not generate a valid response after $attempt attempts. Please try again.")
        }
    }

    private fun <TStreamedChunk> makeStreamedRequest(
        prompt: String,
        onError: (Throwable) -> Unit,
        onDeltaReceived: (Pair<String, Emitter>) -> Unit,
        onComplete: (Pair<StreamCompletedPayload<String>, Emitter>) -> Unit
    ): Sinks.Many<String> {
        val emitter: Emitter = Sinks.many().unicast().onBackpressureBuffer<String>()

        val request = openAIRequestFactory.createRequest(
            prompt = prompt,
            stream = true,
            context = "Return a plain text"
        )

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

        return emitter
    }
}