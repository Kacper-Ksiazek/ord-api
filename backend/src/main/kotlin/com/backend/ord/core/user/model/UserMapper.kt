package com.backend.ord.core.user.model

import com.backend.ord.shared.models.MapperBase
import org.springframework.stereotype.Component

@Component
class UserMapper : MapperBase<UserEntity, UserDTO> {
    override fun toEntity(dto: UserDTO): UserEntity {
        return UserEntity(
            id = dto.id,

            name = dto.name,
            email = dto.email,
            role = dto.role,
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
            role = entity.role,
            password = entity.password,
            nativeLanguage = entity.nativeLanguage,

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}