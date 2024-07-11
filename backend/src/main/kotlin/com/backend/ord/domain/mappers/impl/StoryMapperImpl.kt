package com.backend.ord.domain.mappers.impl

import com.backend.ord.domain.dto.StoryDTO
import com.backend.ord.domain.entities.Story
import com.backend.ord.domain.mappers.StoryMapper
import com.backend.ord.domain.mappers.UserMapper
import org.springframework.stereotype.Component

@Component
class StoryMapperImpl(
    private val userMapper: UserMapper
) : StoryMapper {
    override fun toEntity(dto: StoryDTO): Story {
        return Story(
            id = dto.id,

            title = dto.title,
            content = dto.content,
            explanations = dto.explanations,
            user = userMapper.toEntity(dto.user),

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

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

}