package com.backend.ord.domain.mappers.impl

import com.backend.ord.domain.dto.BankGroupDTO
import com.backend.ord.domain.entities.BankGroup
import com.backend.ord.domain.mappers.BankGroupMapper
import org.springframework.stereotype.Component

@Component
class BankGroupMapperImpl : BankGroupMapper {
    override fun toEntity(dto: BankGroupDTO): BankGroup {
        return BankGroup(
            id = dto.id,

            name = dto.name,
            color = dto.color,

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: BankGroup): BankGroupDTO {
        return BankGroupDTO(
            id = entity.id,

            name = entity.name,
            color = entity.color,

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}