package com.backend.ord.domain.mappers.impl

import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.mappers.UserMapper
import org.springframework.stereotype.Component

@Component
class UserMapperImpl() : UserMapper {
    override fun toEntity(dto: UserDTO): User {
        return User(
            id = dto.id,

            name = dto.name,
            email = dto.email,
            role = dto.role,
            password = dto.password,

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: User): UserDTO {
        return UserDTO(
            id = entity.id,

            name = entity.name,
            email = entity.email,
            role = entity.role,
            password = entity.password,

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

}
