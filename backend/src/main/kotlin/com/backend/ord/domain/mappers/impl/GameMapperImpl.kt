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

            finalScore = dto.finalScore,
            acquiredPoints = dto.acquiredPoints,
            type = dto.type,
            status = dto.status,
            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: Game): GameDTO {
        return GameDTO(
            id = entity.id,

            finalScore = entity.finalScore,
            acquiredPoints = entity.acquiredPoints,
            type = entity.type,
            status = entity.status,
            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

}