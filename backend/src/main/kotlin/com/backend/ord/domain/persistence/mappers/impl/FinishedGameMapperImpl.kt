package com.backend.ord.domain.persistence.mappers.impl

import com.backend.ord.domain.persistence.dto.FinishedGameDTO
import com.backend.ord.domain.persistence.entities.FinishedGame
import com.backend.ord.domain.persistence.mappers.FinishedGameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import org.springframework.stereotype.Component

@Component
class FinishedGameMapperImpl(
    private val userMapper: UserMapper,
) : FinishedGameMapper {
    override fun toDTO(entity: FinishedGame): FinishedGameDTO {
        return FinishedGameDTO(
            id = entity.id,

            duration = entity.duration,
            finalScore = entity.finalScore,

            type = entity.type,
            grade = entity.grade,
            result = entity.result,
            language = entity.language,
            difficulty = entity.difficulty,

            user = userMapper.toDTO(entity.user),
            createdAt = entity.createdAt
        )
    }

    override fun toEntity(dto: FinishedGameDTO): FinishedGame {
        return FinishedGame(
            id = dto.id,

            duration = dto.duration,
            finalScore = dto.finalScore,

            type = dto.type,
            grade = dto.grade,
            result = dto.result,
            language = dto.language,
            difficulty = dto.difficulty,

            user = userMapper.toEntity(dto.user),
            createdAt = dto.createdAt
        )
    }
}