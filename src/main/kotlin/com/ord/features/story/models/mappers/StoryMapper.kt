package com.ord.features.story.models.mappers

import com.ord.core.user.model.UserMapper
import com.ord.features.story.models.dto.StoryDTO
import com.ord.features.story.models.entities.StoryEntity
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class StoryMapper(
    private val userMapper: UserMapper,
    private val storyContextMapper: StoryContextMapper
) : BidirectionalEntityMapper<StoryEntity, StoryDTO> {
    override fun toEntity(dto: StoryDTO): StoryEntity {
        return StoryEntity(
            id = dto.id,

            title = dto.title,
            content = dto.content,
            explanations = dto.explanations,

            user = userMapper.toEntity(dto.user),
            storyContext = storyContextMapper.toEntity(dto.storyContext),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: StoryEntity): StoryDTO {
        return StoryDTO(
            id = entity.id,

            title = entity.title,
            content = entity.content,
            explanations = entity.explanations,

            user = userMapper.toDTO(entity.user),
            storyContext = storyContextMapper.toDTO(entity.storyContext),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}