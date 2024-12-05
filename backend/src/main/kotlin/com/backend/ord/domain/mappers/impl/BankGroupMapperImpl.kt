package com.backend.ord.domain.mappers.impl

import com.backend.ord.domain.dto.BankGroupDTO
import com.backend.ord.domain.entities.BankGroup
import com.backend.ord.domain.mappers.BankGroupMapper
import com.backend.ord.domain.mappers.UserMapper
import org.springframework.stereotype.Component

@Component
class BankGroupMapperImpl(
    val userMapper: UserMapper
): BankGroupMapper {
    override fun toEntity(dto: BankGroupDTO): BankGroup {
        return BankGroup(
            id = dto.id,

            name = dto.name,
            color = dto.color,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: BankGroup): BankGroupDTO {
        return BankGroupDTO(
            id = entity.id,

            name = entity.name,
            color = entity.color,

            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}