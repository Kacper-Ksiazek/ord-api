package com.backend.ord.repositories.gpt_tokens_usage

import com.backend.ord.domain.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.enums.persistance.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.repositories.gpt_tokens_usage.bases.GPTTokensUsageRepository
import org.springframework.stereotype.Repository

@Repository
interface GameTokensUsageRepository : GPTTokensUsageRepository<GameTokensUsage, GamesGPTTokensConsumptionType>