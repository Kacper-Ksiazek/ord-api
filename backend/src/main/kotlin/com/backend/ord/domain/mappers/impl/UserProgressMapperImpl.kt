package com.backend.ord.domain.mappers.impl

import com.backend.ord.domain.dto.UserProgressDTO
import com.backend.ord.domain.entities.UserProgress
import com.backend.ord.domain.mappers.GameMapper
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.domain.mappers.UserProgressMapper
import org.springframework.stereotype.Component

@Component
class UserProgressMapperImpl(
    private val userMapper: UserMapper,
    private val gameMapper: GameMapper
) : UserProgressMapper {
    override fun toEntity(dto: UserProgressDTO): UserProgress {
        return UserProgress(
            id = dto.id,

            pointsObtained = dto.pointsObtained,
            user = userMapper.toEntity(dto.user),
            game = gameMapper.toEntity(dto.game),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: UserProgress): UserProgressDTO {
        return UserProgressDTO(
            id = entity.id,

            pointsObtained = entity.pointsObtained,
            user = userMapper.toDTO(entity.user),
            game = gameMapper.toDTO(entity.game),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}