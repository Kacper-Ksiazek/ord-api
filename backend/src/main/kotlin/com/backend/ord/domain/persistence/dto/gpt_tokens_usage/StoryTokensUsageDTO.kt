package com.backend.ord.domain.persistence.dto.gpt_tokens_usage

import com.backend.ord.domain.persistence.dto.StoryDTO
import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.enums.persistence.tokens_usage.StoriesGPTTokensConsumptionType
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
