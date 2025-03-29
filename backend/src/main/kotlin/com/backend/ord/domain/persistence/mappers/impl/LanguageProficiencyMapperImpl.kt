package com.backend.ord.domain.persistence.mappers.impl

import com.backend.ord.domain.persistence.dto.LanguageProficiencyDTO
import com.backend.ord.domain.persistence.entities.LanguageProficiency
import com.backend.ord.domain.persistence.mappers.LanguageProficiencyMapper
import org.springframework.stereotype.Component

@Component
class LanguageProficiencyMapperImpl(
    private val userMapper: UserMapperImpl
) : LanguageProficiencyMapper {
    override fun toEntity(dto: LanguageProficiencyDTO): LanguageProficiency {
        return LanguageProficiency(
            id = dto.id,

            language = dto.language,
            proficiency = dto.proficiency,
            generativeContentLanguage = dto.generativeContentLanguage,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: LanguageProficiency): LanguageProficiencyDTO {
        return LanguageProficiencyDTO(
            id = entity.id,

            language = entity.language,
            proficiency = entity.proficiency,
            generativeContentLanguage = entity.generativeContentLanguage,

            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}