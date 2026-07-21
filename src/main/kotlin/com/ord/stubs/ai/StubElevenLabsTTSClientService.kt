package com.ord.stubs.ai

import com.ord.core.tts.services.ElevenLabsTTSClientService
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import reactor.core.publisher.Flux

/**
 * Stub TTS client used in the `e2e` runtime profile.
 * Returns a minimal MP3-like byte sequence without calling ElevenLabs.
 */
class StubElevenLabsTTSClientService : ElevenLabsTTSClientService {
    private val bufferFactory = DefaultDataBufferFactory()

    override fun streamSpeech(text: String): Flux<DataBuffer> =
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
}
