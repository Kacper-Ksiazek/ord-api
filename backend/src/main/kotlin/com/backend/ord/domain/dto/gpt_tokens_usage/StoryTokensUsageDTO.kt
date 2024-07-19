package com.backend.ord.domain.dto.gpt_tokens_usage

import com.backend.ord.domain.dto.StoryDTO
import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.enums.TokensUsage.StoriesGPTTokensConsumptionType
import java.time.Instant
import java.util.*

data class StoryTokensUsageDTO(
    val id: UUID = UUID.randomUUID(),

    var numberOfTokens: Int,
    var consumptionType: StoriesGPTTokensConsumptionType,

    val story: StoryDTO,
    val user: UserDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
