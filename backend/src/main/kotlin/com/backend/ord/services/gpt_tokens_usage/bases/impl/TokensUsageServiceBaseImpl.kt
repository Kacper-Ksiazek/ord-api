package com.backend.ord.services.gpt_tokens_usage.bases.impl

import com.backend.ord.api.responses.gpt_tokens_usage.TokensUsageStatistics
import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.domain.entities.interfaces.IdentifiableUserResource
import com.backend.ord.repositories.gpt_tokens_usage.bases.GPTTokensUsageRepository
import com.backend.ord.services.gpt_tokens_usage.bases.TokensUsageServiceBase
import java.math.BigDecimal
import java.util.UUID

abstract class TokensUsageServiceBaseImpl<
        RepositoryType : IdentifiableUserResource,
        ConsumptionType
        >(
    override val openAIProperties: OpenAIProperties,
    override val repository: GPTTokensUsageRepository<RepositoryType, ConsumptionType>
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