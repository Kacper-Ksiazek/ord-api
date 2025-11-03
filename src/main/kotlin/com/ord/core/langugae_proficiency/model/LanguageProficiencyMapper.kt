package com.ord.core.langugae_proficiency.model

import com.ord.core.user.model.UserMapper
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class LanguageProficiencyMapper(
    private val userMapper: UserMapper
) : BidirectionalEntityMapper<LanguageProficiencyEntity, LanguageProficiencyDTO> {
    override fun toEntity(dto: LanguageProficiencyDTO): LanguageProficiencyEntity {
        return LanguageProficiencyEntity(
            id = dto.id,

            language = dto.language,
            level = dto.level,
            translateTo = dto.translateTo,
            generativeContentLanguage = dto.generativeContentLanguage,

            userId = dto.userId,

            createdAt = dto.createdAt,
        )
    }

    override fun toDTO(entity: LanguageProficiencyEntity): LanguageProficiencyDTO {
        return LanguageProficiencyDTO(
            id = entity.id ?: error("Language proficiency ID cannot be null"),

            language = entity.language,
            level = entity.level,
            translateTo = entity.translateTo,
            generativeContentLanguage = entity.generativeContentLanguage,

            userId = entity.userId,

            createdAt = entity.createdAt,
        )
    }
}