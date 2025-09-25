package com.ord.features.bank.model

import com.ord.core.user.model.UserMapper
import com.ord.features.bank_group.model.BankGroupMapper
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class BankMapper(
    private val userMapper: UserMapper,
    private val bankGroupMapper: BankGroupMapper
) : BidirectionalEntityMapper<BankEntity, BankDTO> {
    override fun toEntity(dto: BankDTO): BankEntity {
        return BankEntity(
            id = dto.id,

            name = dto.name,
            description = dto.description,

            userId = dto.userId,

            bankGroupId = dto.bankGroupId,
            bankGroup = bankGroupMapper.toEntityOrNull(dto.bankGroup),

            createdAt = dto.createdAt,
        )
    }

    override fun toDTO(entity: BankEntity): BankDTO {
        return BankDTO(
            id = entity.id,

            name = entity.name,
            description = entity.description,

            userId = entity.userId,

            bankGroupId = entity.bankGroupId,
            bankGroup = bankGroupMapper.toDTOOrNull(entity.bankGroup),

            createdAt = entity.createdAt,
        )
    }
}