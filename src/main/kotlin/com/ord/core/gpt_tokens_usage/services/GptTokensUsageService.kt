package com.ord.core.gpt_tokens_usage.services

import com.ord.core.gpt_tokens_usage.models.GptTokensUsageEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface GptTokensUsageService {
    fun saveTokensUsage(
        userId: UUID,
        operationType: String,
        inputTokens: Int,
        outputTokens: Int
    ): Mono<GptTokensUsageEntity>


    fun saveTokensUsage(
        userId: UUID,
        operationType: String,
        model: String,
        inputTokens: Int,
        outputTokens: Int
    ): Mono<GptTokensUsageEntity>


    fun getTokensUsageByUserId(
        userId: UUID
    ): Flux<GptTokensUsageEntity>
}
