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
            groupId = dto.groupId,

            createdAt = dto.createdAt,
        )
    }

    override fun toDTO(entity: BankEntity): BankDTO {
        return BankDTO(
            id = entity.id ?: error("Bank is must not be null"),

            name = entity.name,
            description = entity.description,

            userId = entity.userId,
            groupId = entity.groupId,

            createdAt = entity.createdAt,
        )
    }
}