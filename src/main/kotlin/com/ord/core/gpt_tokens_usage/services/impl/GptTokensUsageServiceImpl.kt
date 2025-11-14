package com.ord.core.gpt_tokens_usage.services.impl

import com.ord.core.gpt_tokens_usage.models.GptTokensUsageEntity
import com.ord.core.gpt_tokens_usage.repositories.GptTokensUsageRepository
import com.ord.core.gpt_tokens_usage.services.GptTokensUsageService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class GptTokensUsageServiceImpl(
    private val gptTokensUsageRepository: GptTokensUsageRepository
) : GptTokensUsageService {
    override fun saveTokensUsage(
        userId: UUID,
        operationType: String,
        inputTokens: Int,
        outputTokens: Int
    ): Mono<GptTokensUsageEntity> {
        return saveTokensUsage(
            userId = userId,
            operationType = operationType,
            model = GptTokensUsageEntity.DEFAULT_MODEL,
            inputTokens = inputTokens,
            outputTokens = outputTokens,
        )
    }

    override fun saveTokensUsage(
        userId: UUID,
        operationType: String,
        model: String,
        inputTokens: Int,
        outputTokens: Int
    ): Mono<GptTokensUsageEntity> {
        val entity = GptTokensUsageEntity(
            userId = userId,
            operationType = operationType,
            model = model,
            inputTokens = inputTokens,
            outputTokens = outputTokens
        )

        return gptTokensUsageRepository.save(entity)
    }

    override fun getTokensUsageByUserId(userId: UUID): Flux<GptTokensUsageEntity> {
        return gptTokensUsageRepository.findAllByUserId(userId)
    }
}
