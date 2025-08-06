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
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.core.ai_provider.enums.StreamedOpenAIResponseType
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

    override fun openStream(
        prompt: String,
        onComplete: (String) -> Unit,
        onChunkReceived: (String) -> Unit,
        onError: (Throwable) -> Unit
    ): Sinks.Many<String> {
        val emitter = Sinks.many().unicast().onBackpressureBuffer<String>()

        val request = openAIRequestFactory.createRequest(
            prompt = prompt,
            stream = true,
            context = "Return a plain text"
        )

        var test: String = "";

        webClient.post()
            .uri(openAIProperties.apiUrl)
            .bodyValue(request)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(String::class.java)
            .doOnError { error ->
                onError(error)
                emitter.tryEmitError(error)
            }
            .doOnNext { chunk ->
                try {
                    val jsonNode = objectMapper.readTree(chunk)
                    val type = StreamedOpenAIResponseType.fromRawType(jsonNode["type"]?.asText()!!)

                    when (type) {
                        StreamedOpenAIResponseType.RESPONSE_OUTPUT_TEXT_DELTA -> {
                            val delta = jsonNode.get("delta")?.asText()

                            if (delta != null) {
                                emitter.tryEmitNext(delta)
                            }
                        }

                        StreamedOpenAIResponseType.RESPONSE_COMPLETED -> {
                            val finalContent =
                                jsonNode
                                    .get("response")
                                    .get("output")
                                    .firstOrNull()
                                    ?.get("content")
                                    ?.firstOrNull()
                                    ?.get("text")
                                    ?.textValue()

                            println("Full OpenAI response")
                            println(finalContent)
                        }

                        StreamedOpenAIResponseType.RESPONSE_ERROR -> throw Exception()

                        else -> {}

                    }
                } catch (_: Exception) {
                    println("Failed to parse OpenAI stream chunk: $chunk")
                    emitter.tryEmitError(BadGatewayException("Failed to parse OpenAI stream chunk: $chunk"))
                    return@doOnNext
                }

            }
            .doOnComplete {
                emitter.tryEmitComplete()
            }
            .subscribe()

        return emitter
    }

    private fun trackOpenAIAPIRequestAttempt(attempt: Int) {
        if (attempt > openAIProperties.maximumNumberOfOpenAIAPIRequestAttempts) {
            throw BadGatewayException("AI service could not generate a valid response after $attempt attempts. Please try again.")
        }
    }
}