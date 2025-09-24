package com.ord.core.user.model

import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

fun UserEntity.toDTO(): UserDTO = UserDTO(
    id = id ?: error("Expected DB to generate ID"),

    name = name,
    email = email,
    password = password,
    nativeLanguage = nativeLanguage,

    createdAt = createdAt,
    updatedAt = updatedAt
)

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
        return entity.toDTO()
    }
}