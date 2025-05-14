package com.backend.ord.domain.persistence.mappers.impl

import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.domain.persistence.dto.BankDTO
import com.backend.ord.domain.persistence.entities.Bank
import com.backend.ord.domain.persistence.mappers.BankGroupMapper
import com.backend.ord.domain.persistence.mappers.BankMapper
import org.springframework.stereotype.Component

@Component
class BankMapperImpl(
    private val userMapper: UserMapper,
    private val bankGroupMapper: BankGroupMapper
) : BankMapper {
    override fun toEntity(dto: BankDTO): Bank {
        return Bank(
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

    override fun toDTO(entity: Bank): BankDTO {
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