package com.ord.core.ai_provider_usage.services.impl

import com.ord.core.ai_provider_usage.models.AiProvider
import com.ord.core.ai_provider_usage.models.AiProviderUsageEntity
import com.ord.core.ai_provider_usage.models.AiUsageUnitType
import com.ord.core.ai_provider_usage.repositories.AiProviderUsageRepository
import com.ord.core.ai_provider_usage.services.AiProviderUsageService
import com.ord.core.ai_provider_usage.services.AiUsagePriceCalculator
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class AiProviderUsageServiceImpl(
    private val aiProviderUsageRepository: AiProviderUsageRepository,
) : AiProviderUsageService {
    override fun saveOpenAiUsage(
        userId: UUID,
        operationType: String,
        model: String,
        inputTokens: Int,
        outputTokens: Int,
    ): Mono<AiProviderUsageEntity> {
        val entity = AiProviderUsageEntity(
            userId = userId,
            provider = AiProvider.OPENAI,
            operationType = operationType,
            model = model,
            inputUnits = inputTokens,
            outputUnits = outputTokens,
            unitType = AiUsageUnitType.TOKENS,
            estimatedPrice = AiUsagePriceCalculator.forOpenAi(model, inputTokens, outputTokens),
        )

        return aiProviderUsageRepository.save(entity)
    }

    override fun saveElevenLabsUsage(
        userId: UUID,
        operationType: String,
        model: String,
        voiceId: String,
        characterCount: Int,
    ): Mono<AiProviderUsageEntity> {
        val entity = AiProviderUsageEntity(
            userId = userId,
            provider = AiProvider.ELEVENLABS,
            operationType = operationType,
            model = model,
            voiceId = voiceId,
            inputUnits = characterCount,
            outputUnits = 0,
            unitType = AiUsageUnitType.CHARACTERS,
            estimatedPrice = AiUsagePriceCalculator.forElevenLabs(voiceId, characterCount),
        )

        return aiProviderUsageRepository.save(entity)
    }

    override fun getUsageByUserId(userId: UUID): Flux<AiProviderUsageEntity> {
        return aiProviderUsageRepository.findAllByUserId(userId)
    }
}
