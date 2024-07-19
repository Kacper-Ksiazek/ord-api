package com.backend.ord.domain.mappers.impl.gpt_tokens_usage

import com.backend.ord.domain.dto.gpt_tokens_usage.StoryTokensUsageDTO
import com.backend.ord.domain.entities.gpt_tokens_usage.StoryTokensUsage
import com.backend.ord.domain.mappers.StoryMapper
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.domain.mappers.gpt_tokens_usage.StoryTokensUsageMapper
import org.springframework.stereotype.Component

@Component
class StoryTokensUsageMapperImpl(
    private val userMapper: UserMapper,
    private val storyMapper: StoryMapper
): StoryTokensUsageMapper {
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