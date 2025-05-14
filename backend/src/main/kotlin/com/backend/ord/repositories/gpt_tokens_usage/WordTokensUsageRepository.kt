package com.backend.ord.repositories.gpt_tokens_usage

import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.enums.persistence.tokens_usage.WordsGPTTokensConsumptionType
import com.backend.ord.shared.repositories.GPTTokensUsageRepository
import org.springframework.stereotype.Repository

@Repository
interface WordTokensUsageRepository : GPTTokensUsageRepository<WordTokensUsage, WordsGPTTokensConsumptionType>