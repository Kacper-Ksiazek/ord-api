package com.backend.ord.features.gpt_tokens_usage_log.variants.shared.service.impl

import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.features.gpt_tokens_usage_log.api.dto.responses.dto.TokensUsageStatistics
import com.backend.ord.features.gpt_tokens_usage_log.variants.shared.repository.TokensUsageRepository
import com.backend.ord.features.gpt_tokens_usage_log.variants.shared.service.TokensUsageServiceBase
import com.backend.ord.shared.models.IdentifiableUserResource
import java.math.BigDecimal
import java.util.*

abstract class TokensUsageServiceBaseImpl<
        RepositoryType : IdentifiableUserResource,
        ConsumptionType
        >(
    override val openAIProperties: OpenAIProperties,
    override val repository: TokensUsageRepository<RepositoryType, ConsumptionType>
) : TokensUsageServiceBase<RepositoryType, ConsumptionType> {
    override fun computeCost(
        inputTokens: Int,
        outputTokens: Int
    ): BigDecimal {
        // Calculate cost per token
        val inputCostPerToken = openAIProperties.pricePerMlnInputTokens.divide(BigDecimal(1_000_000))
        val outputCostPerToken = openAIProperties.pricePerMlnOutputTokens.divide(BigDecimal(1_000_000))

        // Calculate total cost
        val totalCost = inputCostPerToken.multiply(BigDecimal(inputTokens)) +
                outputCostPerToken.multiply(BigDecimal(outputTokens))

        return totalCost
    }

    override fun getDetailedConsumption(
        userId: UUID,
        month: Int,
        year: Int
    ): List<RepositoryType> {
        return repository.findAllByUserInGivenMonth(
            userId = userId,
            month = month,
            year = year
        )
    }

    override fun getConsumptionStatistics(
        userId: UUID,
        month: Int,
        year: Int
    ): List<TokensUsageStatistics<ConsumptionType>> {
        return repository.findUsageStatsByUserInGivenMonth(
            userId = userId,
            month = month,
            year = year
        )
    }
}