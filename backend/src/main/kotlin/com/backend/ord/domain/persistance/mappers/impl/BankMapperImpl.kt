package com.backend.ord.domain.persistance.mappers.impl

import com.backend.ord.domain.persistance.dto.BankDTO
import com.backend.ord.domain.persistance.entities.Bank
import com.backend.ord.domain.persistance.mappers.BankGroupMapper
import com.backend.ord.domain.persistance.mappers.BankMapper
import com.backend.ord.domain.persistance.mappers.UserMapper
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