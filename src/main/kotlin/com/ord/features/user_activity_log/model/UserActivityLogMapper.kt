package com.ord.features.user_activity_log.model

import com.ord.core.user.model.UserMapper
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class UserActivityLogMapper(
    private val userMapper: UserMapper,
) : BidirectionalEntityMapper<UserActivityLogEntity, UserActivityLogDTO> {
    override fun toEntity(dto: UserActivityLogDTO): UserActivityLogEntity {
        return UserActivityLogEntity(
            id = dto.id,

            type = dto.type,
            language = dto.language,
            gameDifficulty = dto.gameDifficulty,
            points = dto.points,

            userId = dto.userId,

            createdAt = dto.createdAt,
        )
    }

    override fun toDTO(entity: UserActivityLogEntity): UserActivityLogDTO {
        return UserActivityLogDTO(
            id = entity.id ?: error("UserActivityLog id must not be null"),

            type = entity.type,
            language = entity.language,
            gameDifficulty = entity.gameDifficulty,
            points = entity.points,

            userId = entity.userId,

            createdAt = entity.createdAt,
        )
    }
}