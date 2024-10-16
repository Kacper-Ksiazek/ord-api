package com.backend.ord.services.gpt_tokens_usage.bases

import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.domain.entities.interfaces.IdentifiableUserResource
import com.backend.ord.repositories.bases.UserResourceRepository
import java.math.BigDecimal

interface TokensUsageServiceBase<T : IdentifiableUserResource> {
    val repository: UserResourceRepository<T>
    val openAIProperties: OpenAIProperties

    fun computeCost(inputTokens: Int, outputTokens: Int): BigDecimal
}