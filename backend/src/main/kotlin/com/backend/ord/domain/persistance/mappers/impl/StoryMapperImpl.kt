package com.backend.ord.domain.persistance.mappers.impl

import com.backend.ord.domain.persistance.dto.StoryDTO
import com.backend.ord.domain.persistance.entities.Story
import com.backend.ord.domain.persistance.mappers.StoryContextMapper
import com.backend.ord.domain.persistance.mappers.StoryMapper
import com.backend.ord.domain.persistance.mappers.UserMapper
import org.springframework.stereotype.Component

@Component
class StoryMapperImpl(
    private val userMapper: UserMapper,
    private val storyContextMapper: StoryContextMapper
) : StoryMapper {
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