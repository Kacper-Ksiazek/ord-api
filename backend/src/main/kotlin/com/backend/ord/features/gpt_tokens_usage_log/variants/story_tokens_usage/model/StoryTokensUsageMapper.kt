package com.backend.ord.features.gpt_tokens_usage_log.variants.story_tokens_usage.model

import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.domain.persistence.mappers.StoryMapper
import com.backend.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class StoryTokensUsageMapper(
    private val userMapper: UserMapper,
    private val storyMapper: StoryMapper
) : MapperBase<StoryTokensUsage, StoryTokensUsageDTO> {
    override fun toEntity(dto: StoryTokensUsageDTO): StoryTokensUsage {
        return StoryTokensUsage(
            id = dto.id,
            numberOfTokens = dto.numberOfTokens,
            consumptionType = dto.consumptionType,

            user = userMapper.toEntity(dto.user),
            story = storyMapper.toEntity(dto.story),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: StoryTokensUsage): StoryTokensUsageDTO {
        return StoryTokensUsageDTO(
            id = entity.id,

            numberOfTokens = entity.numberOfTokens,
            consumptionType = entity.consumptionType,

            user = userMapper.toDTO(entity.user),
            story = storyMapper.toDTO(entity.story),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}