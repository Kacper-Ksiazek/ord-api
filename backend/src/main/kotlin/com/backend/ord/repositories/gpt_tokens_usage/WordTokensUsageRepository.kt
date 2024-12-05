package com.backend.ord.repositories.gpt_tokens_usage

import com.backend.ord.domain.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.enums.tokens_usage.WordsGPTTokensConsumptionType
import com.backend.ord.repositories.gpt_tokens_usage.bases.GPTTokensUsageRepository
import org.springframework.stereotype.Repository

@Repository
interface WordTokensUsageRepository : GPTTokensUsageRepository<WordTokensUsage, WordsGPTTokensConsumptionType>