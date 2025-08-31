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
            generativeContentLanguage = dto.generativeContentLanguage,

            userId = dto.userId,
            user = userMapper.toEntityOrNull(dto.user),

            createdAt = dto.createdAt,
        )
    }

    override fun toDTO(entity: LanguageProficiencyEntity): LanguageProficiencyDTO {
        return LanguageProficiencyDTO(
            id = entity.id,

            language = entity.language,
            level = entity.level,
            generativeContentLanguage = entity.generativeContentLanguage,

            userId = entity.userId,
            user = userMapper.toDTOOrNull(entity.user),

            createdAt = entity.createdAt,
        )
    }
}