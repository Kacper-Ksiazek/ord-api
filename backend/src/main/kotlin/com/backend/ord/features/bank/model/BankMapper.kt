package com.backend.ord.features.bank.model

import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.features.bank_group.model.BankGroupMapper
import com.backend.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class BankMapper(
    private val userMapper: UserMapper,
    private val bankGroupMapper: BankGroupMapper
) : MapperBase<BankEntity, BankDTO> {
    override fun toEntity(dto: BankDTO): BankEntity {
        return BankEntity(
            id = dto.id,

            name = dto.name,
            description = dto.description,

            user = userMapper.toEntity(dto.user),

            bankGroupId = dto.bankGroupId,
            bankGroup = bankGroupMapper.toEntityOrNull(dto.bankGroup),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: BankEntity): BankDTO {
        return BankDTO(
            id = entity.id,

            name = entity.name,
            description = entity.description,

            user = userMapper.toDTO(entity.user),

            bankGroupId = entity.bankGroupId,
            bankGroup = bankGroupMapper.toDTOOrNull(entity.bankGroup),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}