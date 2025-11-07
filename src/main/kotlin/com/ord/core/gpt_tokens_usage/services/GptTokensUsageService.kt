package com.ord.core.gpt_tokens_usage.services

import com.ord.core.gpt_tokens_usage.models.GptTokensUsageEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface GptTokensUsageService {
    /**
     * Saves a GPT token usage log entry.
     *
     * @param userId The ID of the user who consumed the tokens
     * @param operationType The type of operation that caused token consumption
     * @param model The GPT model used
     * @param inputTokens Number of input tokens consumed
     * @param outputTokens Number of output tokens consumed
     * @return Mono of the saved entity
     */
    fun saveTokensUsage(
        userId: UUID,
        operationType: String,
        model: String,
        inputTokens: Int,
        outputTokens: Int
    ): Mono<GptTokensUsageEntity>

    /**
     * Retrieves all token usage logs for a specific user.
     *
     * @param userId The ID of the user
     * @return Flux of token usage entities
     */
    fun getTokensUsageByUserId(userId: UUID): Flux<GptTokensUsageEntity>
}
