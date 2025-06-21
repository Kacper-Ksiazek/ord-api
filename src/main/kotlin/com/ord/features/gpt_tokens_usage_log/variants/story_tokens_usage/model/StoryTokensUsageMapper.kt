package com.ord.features.gpt_tokens_usage_log.variants.story_tokens_usage.model

import com.ord.core.user.model.UserMapper
import com.ord.features.story.models.mappers.StoryMapper
import com.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class StoryTokensUsageMapper(
    private val userMapper: UserMapper,
    private val storyMapper: StoryMapper
) : MapperBase<StoryTokensUsageEntity, StoryTokensUsageDTO> {
    override fun toEntity(dto: StoryTokensUsageDTO): StoryTokensUsageEntity {
        return StoryTokensUsageEntity(
            id = dto.id,
            numberOfTokens = dto.numberOfTokens,
            consumptionType = dto.consumptionType,

            user = userMapper.toEntity(dto.user),
            story = storyMapper.toEntity(dto.story),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: StoryTokensUsageEntity): StoryTokensUsageDTO {
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