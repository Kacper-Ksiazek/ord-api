package com.ord.core.tts.services

import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux

interface ElevenLabsTTSClientService {
    fun streamSpeech(text: String): Flux<DataBuffer>
}
