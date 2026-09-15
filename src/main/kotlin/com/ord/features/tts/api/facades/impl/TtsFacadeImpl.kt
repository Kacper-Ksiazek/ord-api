package com.ord.features.tts.api.facades.impl

import com.ord.core.ai_provider_usage.models.AiProviderUsageOperationType
import com.ord.core.tts.services.ElevenLabsTTSClientService
import com.ord.core.user.model.UserDTO
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.tts.api.facades.TtsFacade
import com.ord.features.tts.api.requests.SpeakRequest
import com.ord.shared.tts.AvailableTTSVoices
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
    ): Flux<DataBuffer> {
        val language = body.language ?: user.selectedLearningLanguage
            ?: throw BadRequestException("Language is required for TTS")

        val voice = AvailableTTSVoices.forLanguage(language)
            ?: throw BadRequestException("TTS is not available for language: $language")

        return elevenLabsTTSClientService.streamSpeech(
            text = body.text,
            voiceId = voice.voiceId,
            modelId = voice.modelId,
            userId = user.id,
            operationType = AiProviderUsageOperationType.Tts.SPEAK,
        )
    }
}
