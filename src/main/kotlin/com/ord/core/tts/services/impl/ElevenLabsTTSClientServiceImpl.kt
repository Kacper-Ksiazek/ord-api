package com.ord.core.tts.services.impl

import com.ord.config.properties.ElevenLabsProperties
import com.ord.core.tts.dto.ElevenLabsTTSRequest
import com.ord.core.tts.services.ElevenLabsTTSClientService
import com.ord.exceptions.REST.BadGatewayException
import com.ord.shared.utils.Console
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.io.IOException
import java.net.ConnectException
import java.time.Duration
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean

@Service
@Profile("!e2e")
class ElevenLabsTTSClientServiceImpl(
    private val elevenLabsProperties: ElevenLabsProperties,
    @Qualifier("elevenLabsWebClient")
    private val webClient: WebClient,
    private val env: Environment,
) : ElevenLabsTTSClientService {
    private val bufferFactory = DefaultDataBufferFactory()

    private val isTestingEnv: Boolean
        get() = env.activeProfiles.contains("test")

    override fun streamSpeech(text: String): Flux<DataBuffer> {
        if (shouldUseMockResponse()) {
            return mockSpeechFlux()
        }

        if (!elevenLabsProperties.isConfigured) {
            return Flux.error(
                BadGatewayException(
                    "ElevenLabs TTS is not configured. Set ELEVENLABS_API_KEY and ELEVENLABS_VOICE_ID environment variables."
                )
            )
        }

        return streamSpeechFromElevenLabs(text)
    }

    private fun shouldUseMockResponse(): Boolean = isTestingEnv

    private fun mockSpeechFlux(): Flux<DataBuffer> =
        Flux.just(
            bufferFactory.wrap(
                byteArrayOf(
                    0xFF.toByte(),
                    0xFB.toByte(),
                    0x90.toByte(),
                    0x00.toByte(),
                )
            )
        )

    private fun streamSpeechFromElevenLabs(text: String): Flux<DataBuffer> {
        val hasEmitted = AtomicBoolean(false)

        return Flux.defer {
            webClient.post()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/text-to-speech/{voiceId}/stream")
                        .queryParam("optimize_streaming_latency", elevenLabsProperties.optimizeStreamingLatency)
                        .queryParam("output_format", elevenLabsProperties.outputFormat)
                        .build(elevenLabsProperties.voiceId)
                }
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.parseMediaType("audio/mpeg"))
                .bodyValue(
                    ElevenLabsTTSRequest(
                        text = text,
                        modelId = elevenLabsProperties.modelId,
                    )
                )
                .retrieve()
                .onStatus({ it.isError }) { response ->
                    response.bodyToMono(String::class.java)
                        .defaultIfEmpty("Unknown ElevenLabs error")
                        .flatMap { body ->
                            Mono.error(
                                BadGatewayException(
                                    "ElevenLabs TTS request failed with status ${response.statusCode().value()}: $body"
                                )
                            )
                        }
                }
                .bodyToFlux(DataBuffer::class.java)
        }
            .doOnNext { hasEmitted.set(true) }
            .retryWhen(
                Retry.backoff(
                    elevenLabsProperties.retryMaxAttempts.toLong(),
                    Duration.ofSeconds(elevenLabsProperties.retryBackoffSeconds)
                )
                    .filter { throwable ->
                        if (hasEmitted.get()) {
                            return@filter false
                        }

                        val isRetryable = throwable is ConnectException ||
                            throwable is TimeoutException ||
                            throwable is IOException ||
                            throwable is WebClientRequestException

                        if (isRetryable) {
                            Console.printYellow("\n⚠️ [ELEVENLABS TTS RETRY]")
                            Console.printYellow("Retrying due to: ${throwable::class.simpleName} - ${throwable.message}")
                        }

                        isRetryable
                    }
                    .doBeforeRetry { retrySignal ->
                        Console.printYellow(
                            "ElevenLabs TTS retry attempt ${retrySignal.totalRetries() + 1}/${elevenLabsProperties.retryMaxAttempts}"
                        )
                    }
            )
            .onErrorMap { throwable ->
                when (throwable) {
                    is BadGatewayException -> throwable
                    is WebClientResponseException -> BadGatewayException(
                        "ElevenLabs TTS request failed with status ${throwable.statusCode.value()}: ${throwable.responseBodyAsString}"
                    )

                    else -> BadGatewayException(
                        "ElevenLabs TTS request failed: ${throwable.message ?: throwable::class.simpleName}"
                    )
                }
            }
    }
}
