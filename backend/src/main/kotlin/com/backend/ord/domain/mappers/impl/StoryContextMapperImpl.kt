package com.backend.ord.domain.mappers.impl

import com.backend.ord.domain.dto.StoryContextDTO
import com.backend.ord.domain.entities.StoryContext
import com.backend.ord.domain.mappers.StoryContextMapper
import com.backend.ord.domain.mappers.StoryMapper
import com.backend.ord.domain.mappers.UserMapper
import org.springframework.stereotype.Component

@Component
class StoryContextMapperImpl(
    private val userMapper: UserMapper
) : StoryContextMapper {
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