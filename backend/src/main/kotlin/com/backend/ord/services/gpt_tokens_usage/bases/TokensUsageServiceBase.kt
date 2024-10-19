package com.backend.ord.services.gpt_tokens_usage.bases

import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.domain.entities.interfaces.IdentifiableUserResource
import com.backend.ord.repositories.gpt_tokens_usage.bases.GPTTokensUsageRepository
import java.math.BigDecimal
import java.time.Month
import java.util.UUID

interface TokensUsageServiceBase<T : IdentifiableUserResource> {
    val repository: GPTTokensUsageRepository<T>
    val openAIProperties: OpenAIProperties

    fun computeCost(inputTokens: Int, outputTokens: Int): BigDecimal

    fun getTokensConsumptionForUserInMonth(
        userId: UUID,
        month: Month,
        year: Int
    ): List<T>
}