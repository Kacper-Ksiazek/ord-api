package com.backend.ord.repositories.gpt_tokens_usage

import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.enums.persistence.tokens_usage.GamesGPTTokensConsumptionType
import com.backend.ord.shared.repositories.GPTTokensUsageRepository
import org.springframework.stereotype.Repository

@Repository
interface GameTokensUsageRepository : GPTTokensUsageRepository<GameTokensUsage, GamesGPTTokensConsumptionType>