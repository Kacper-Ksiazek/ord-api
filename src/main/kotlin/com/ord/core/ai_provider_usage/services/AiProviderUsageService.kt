package com.ord.core.ai_provider_usage.services

import com.ord.core.ai_provider_usage.models.AiProviderUsageEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface AiProviderUsageService {
    fun saveOpenAiUsage(
        userId: UUID,
        operationType: String,
        model: String,
        inputTokens: Int,
        outputTokens: Int,
    ): Mono<AiProviderUsageEntity>

    fun saveElevenLabsUsage(
        userId: UUID,
        operationType: String,
        model: String,
        voiceId: String,
        characterCount: Int,
    ): Mono<AiProviderUsageEntity>

    fun getUsageByUserId(userId: UUID): Flux<AiProviderUsageEntity>
}
