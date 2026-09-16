package com.ord.core.tts.services

import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux
import java.util.UUID

interface ElevenLabsTTSClientService {
    fun streamSpeech(
        text: String,
        voiceId: String,
        modelId: String,
        userId: UUID,
        operationType: String,
    ): Flux<DataBuffer>
}
