package com.backend.ord.services.gpt_tokens_usage.bases.impl

import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.domain.entities.interfaces.IdentifiableUserResource
import com.backend.ord.repositories.bases.UserResourceRepository
import com.backend.ord.services.gpt_tokens_usage.bases.TokensUsageServiceBase
import java.math.BigDecimal

abstract class TokensUsageServiceBaseImpl<T : IdentifiableUserResource>(
    override val openAIProperties: OpenAIProperties,
    override val repository: UserResourceRepository<T>
) : TokensUsageServiceBase<T> {
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
}