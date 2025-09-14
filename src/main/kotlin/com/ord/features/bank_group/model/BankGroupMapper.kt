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

            user = userMapper.toEntity(dto.user),
            userId = dto.user.id,

            createdAt = dto.createdAt,
        )
    }

    override fun toDTO(entity: BankGroupEntity): BankGroupDTO {
        return BankGroupDTO(
            id = entity.id,

            name = entity.name,
            color = entity.color,

            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
        )
    }
}