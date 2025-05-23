package com.backend.ord.features.game.model.finished_game

import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class FinishedGameMapper(
    private val userMapper: UserMapper,
) : MapperBase<FinishedGame, FinishedGameDTO> {
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