package com.backend.ord.features.gpt_tokens_usage_log.variants.story_tokens_usage.model

import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.features.story.models.dto.StoryDTO
import com.backend.ord.features.gpt_tokens_usage_log.variants.story_tokens_usage.model.enums.StoriesGPTTokensConsumptionType
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
