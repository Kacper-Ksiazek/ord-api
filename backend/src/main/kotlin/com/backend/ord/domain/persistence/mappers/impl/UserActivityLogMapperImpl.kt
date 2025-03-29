package com.backend.ord.domain.persistence.mappers.impl

import com.backend.ord.domain.persistence.dto.UserActivityLogDTO
import com.backend.ord.domain.persistence.entities.UserActivityLog
import com.backend.ord.domain.persistence.mappers.UserActivityLogMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import org.springframework.stereotype.Component

@Component
class UserActivityLogMapperImpl(
    private val userMapper: UserMapper,
) : UserActivityLogMapper {
    override fun toEntity(dto: UserActivityLogDTO): UserActivityLog {
        return UserActivityLog(
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

    override fun toDTO(entity: UserActivityLog): UserActivityLogDTO {
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