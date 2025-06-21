package com.ord.features.story.models.mappers

import com.ord.core.user.model.UserMapper
import com.ord.features.story.models.dto.StoryContextDTO
import com.ord.features.story.models.entities.StoryContextEntity
import com.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class StoryContextMapper(
    private val userMapper: UserMapper
): MapperBase<StoryContextEntity, StoryContextDTO> {
        override fun toDTO(entity: StoryContextEntity): StoryContextDTO {
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

    override fun toEntity(dto: StoryContextDTO): StoryContextEntity {
        return StoryContextEntity(
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