package com.backend.ord.services.gpt_tokens_usage.bases

import com.backend.ord.api.responses.gpt_tokens_usage.TokensUsageStatistics
import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.domain.persistence.entities.interfaces.IdentifiableUserResource
import com.backend.ord.repositories.gpt_tokens_usage.bases.GPTTokensUsageRepository
import java.math.BigDecimal
import java.util.*

interface TokensUsageServiceBase<
        RepositoryType : IdentifiableUserResource,
        ConsumptionType
        > {
    val repository: GPTTokensUsageRepository<RepositoryType, ConsumptionType>
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