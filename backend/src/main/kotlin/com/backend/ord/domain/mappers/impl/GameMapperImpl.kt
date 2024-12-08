package com.backend.ord.domain.mappers.impl

import com.backend.ord.domain.dto.GameDTO
import com.backend.ord.domain.entities.Game
import com.backend.ord.domain.mappers.GameMapper
import com.backend.ord.domain.mappers.UserMapper
import org.springframework.stereotype.Component

@Component
class GameMapperImpl(
    private val userMapper: UserMapper
) : GameMapper {
    override fun toEntity(dto: GameDTO): Game {
        return Game(
            id = dto.id,

            type = dto.type,
            status = dto.status,
            difficulty = dto.difficulty,
            instruction = dto.instruction,

            duration = dto.duration,
            acquiredPoints = dto.acquiredPoints,
            accuracyRate = dto.accuracyRate,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: Game): GameDTO {
        return GameDTO(
            id = entity.id,

            type = entity.type,
            status = entity.status,
            difficulty = entity.difficulty,
            instruction = entity.instruction,

            duration = entity.duration,
            accuracyRate = entity.accuracyRate,
            acquiredPoints = entity.acquiredPoints,

            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

}