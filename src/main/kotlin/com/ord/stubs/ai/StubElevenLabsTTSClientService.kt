package com.ord.stubs.ai

import com.ord.core.ai_provider_usage.services.AiProviderUsageService
import com.ord.core.tts.services.ElevenLabsTTSClientService
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import reactor.core.publisher.Flux
import java.util.UUID

/**
 * Stub TTS client used in the `e2e` runtime profile.
 * Returns a minimal MP3-like byte sequence without calling ElevenLabs.
 */
class StubElevenLabsTTSClientService(
    private val aiProviderUsageService: AiProviderUsageService,
) : ElevenLabsTTSClientService {
    private val bufferFactory = DefaultDataBufferFactory()

    override fun streamSpeech(
        text: String,
        voiceId: String,
        modelId: String,
        userId: UUID,
        operationType: String,
    ): Flux<DataBuffer> =
        Flux.just<DataBuffer>(
            bufferFactory.wrap(
                byteArrayOf(
                    0xFF.toByte(),
                    0xFB.toByte(),
                    0x90.toByte(),
                    0x00.toByte(),
                )
            )
        ).doOnComplete {
            aiProviderUsageService.saveElevenLabsUsage(
                userId = userId,
                operationType = operationType,
                model = modelId,
                voiceId = voiceId,
                characterCount = text.length,
            ).subscribe()
        }
}
