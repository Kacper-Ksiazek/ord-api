package com.ord.features.tts.api.facades.impl

import com.ord.core.tts.services.ElevenLabsTTSClientService
import com.ord.core.user.model.UserDTO
import com.ord.features.tts.api.facades.TtsFacade
import com.ord.features.tts.api.requests.SpeakRequest
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class TtsFacadeImpl(
    private val elevenLabsTTSClientService: ElevenLabsTTSClientService,
) : TtsFacade {
    override fun speak(
        body: SpeakRequest,
        user: UserDTO,
    ): Flux<DataBuffer> =
        elevenLabsTTSClientService.streamSpeech(body.text)
}
