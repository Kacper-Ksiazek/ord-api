package com.backend.ord.features.story.models.mappers

import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.features.story.models.dto.StoryDTO
import com.backend.ord.features.story.models.entities.Story
import com.backend.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class StoryMapper(
    private val userMapper: UserMapper,
    private val storyContextMapper: StoryContextMapper
) : MapperBase<Story, StoryDTO> {
    override fun toEntity(dto: StoryDTO): Story {
        return Story(
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

    override fun toDTO(entity: Story): StoryDTO {
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