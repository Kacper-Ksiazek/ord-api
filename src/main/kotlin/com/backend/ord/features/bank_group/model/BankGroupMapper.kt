package com.backend.ord.features.bank_group.model

import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class BankGroupMapper(
    private val userMapper: UserMapper
) : MapperBase<BankGroupEntity, BankGroupDTO> {
    override fun toEntity(dto: BankGroupDTO): BankGroupEntity {
        return BankGroupEntity(
            id = dto.id,

            name = dto.name,
            color = dto.color,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: BankGroupEntity): BankGroupDTO {
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