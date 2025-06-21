package com.backend.ord.features.user_activity_log.model

import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class UserActivityLogMapper(
    private val userMapper: UserMapper,
) : MapperBase<UserActivityLogEntity, UserActivityLogDTO> {
    override fun toEntity(dto: UserActivityLogDTO): UserActivityLogEntity {
        return UserActivityLogEntity(
            id = dto.id,

            type = dto.type,
            language = dto.language,
            gameDifficulty = dto.gameDifficulty,
            points = dto.points,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: UserActivityLogEntity): UserActivityLogDTO {
        return UserActivityLogDTO(
            id = entity.id,

            type = entity.type,
            language = entity.language,
            gameDifficulty = entity.gameDifficulty,
            points = entity.points,

            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}