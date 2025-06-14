package com.backend.ord.core.langugae_proficiency.model

import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class LanguageProficiencyMapper(
    private val userMapper: UserMapper
) : MapperBase<LanguageProficiencyEntity, LanguageProficiencyDTO> {
    override fun toEntity(dto: LanguageProficiencyDTO): LanguageProficiencyEntity {
        return LanguageProficiencyEntity(
            id = dto.id,

            language = dto.language,
            proficiency = dto.proficiency,
            generativeContentLanguage = dto.generativeContentLanguage,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: LanguageProficiencyEntity): LanguageProficiencyDTO {
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