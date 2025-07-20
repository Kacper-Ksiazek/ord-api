package com.ord.features.game.model.finished_game

import com.ord.core.user.model.UserMapper
import com.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class FinishedGameMapper(
    private val userMapper: UserMapper,
) : MapperBase<FinishedGameEntity, FinishedGameDTO> {
    override fun toDTO(entity: FinishedGameEntity): FinishedGameDTO {
        return FinishedGameDTO(
            id = entity.id,

            score = entity.score,
            duration = entity.duration,
            accuracy = entity.accuracy,

            type = entity.type,
            grade = entity.grade,
            result = entity.result,
            language = entity.language,
            difficulty = entity.difficulty,

            user = userMapper.toDTO(entity.user),
            createdAt = entity.createdAt
        )
    }

    override fun toEntity(dto: FinishedGameDTO): FinishedGameEntity {
        return FinishedGameEntity(
            id = dto.id,

            score = dto.score,
            duration = dto.duration,
            accuracy = dto.accuracy,

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