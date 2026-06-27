package com.ord.features.tts.api.facades

import com.ord.core.user.model.UserDTO
import com.ord.features.tts.api.requests.SpeakRequest
import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux

interface TtsFacade {
    fun speak(
        body: SpeakRequest,
        user: UserDTO,
    ): Flux<DataBuffer>
}
