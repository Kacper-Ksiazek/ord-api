package com.ord.features.bank_group.model

import com.ord.core.user.model.UserMapper
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class BankGroupMapper(
    private val userMapper: UserMapper
) : BidirectionalEntityMapper<BankGroupEntity, BankGroupDTO> {
    override fun toEntity(dto: BankGroupDTO): BankGroupEntity {
        return BankGroupEntity(
            id = dto.id,

            name = dto.name,
            color = dto.color,

            userId = dto.userId,

            createdAt = dto.createdAt,
        )
    }

    override fun toDTO(entity: BankGroupEntity): BankGroupDTO {
        return BankGroupDTO(
            id = entity.id ?: error("Bank group id must not be null"),

            name = entity.name,
            color = entity.color,

            userId = entity.userId,

            createdAt = entity.createdAt,
        )
    }
}