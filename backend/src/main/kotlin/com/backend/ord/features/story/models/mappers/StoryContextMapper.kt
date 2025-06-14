package com.backend.ord.features.story.models.mappers

import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.features.story.models.dto.StoryContextDTO
import com.backend.ord.features.story.models.entities.StoryContext
import com.backend.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class StoryContextMapper(
    private val userMapper: UserMapper
): MapperBase<StoryContext, StoryContextDTO> {
        override fun toDTO(entity: StoryContext): StoryContextDTO {
        return StoryContextDTO(
            id = entity.id,

            type = entity.type,
            title = entity.title,
            prompt = entity.prompt,

            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    override fun toEntity(dto: StoryContextDTO): StoryContext {
        return StoryContext(
            id = dto.id,

            type = dto.type,
            title = dto.title,
            prompt = dto.prompt,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }
}