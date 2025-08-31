package com.ord.core.user.model

import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class UserMapper : BidirectionalEntityMapper<UserEntity, UserDTO> {
    override fun toEntity(dto: UserDTO): UserEntity {
        return UserEntity(
            id = dto.id,

            name = dto.name,
            email = dto.email,
            password = dto.password,
            nativeLanguage = dto.nativeLanguage,

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: UserEntity): UserDTO {
        return UserDTO(
            id = entity.id,

            name = entity.name,
            email = entity.email,
            password = entity.password,
            nativeLanguage = entity.nativeLanguage,

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}