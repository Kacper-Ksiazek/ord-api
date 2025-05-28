package com.backend.ord.features.gpt_tokens_usage_log.variants.shared.service

import com.backend.ord.api.responses.gpt_tokens_usage.TokensUsageStatistics
import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.features.gpt_tokens_usage_log.variants.shared.repository.TokensUsageRepository
import com.backend.ord.shared.models.IdentifiableUserResource
import java.math.BigDecimal
import java.util.*

interface TokensUsageServiceBase<
        RepositoryType : IdentifiableUserResource,
        ConsumptionType
        > {
    val repository: TokensUsageRepository<RepositoryType, ConsumptionType>
    val openAIProperties: OpenAIProperties

    fun computeCost(inputTokens: Int, outputTokens: Int): BigDecimal

    fun getDetailedConsumption(
        userId: UUID,
        month: Int,
        year: Int
    ): List<RepositoryType>

    fun getConsumptionStatistics(
        userId: UUID,
        month: Int,
        year: Int
    ): List<TokensUsageStatistics<ConsumptionType>>
}