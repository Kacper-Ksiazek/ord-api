package com.backend.ord.features.quickly_added_words.model

import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class QuicklyAddedWordMapper(
    private val userMapper: UserMapper
): MapperBase<QuicklyAddedWordEntity, QuicklyAddedWordDTO> {
    override fun toEntity(dto: QuicklyAddedWordDTO): QuicklyAddedWordEntity {
        return QuicklyAddedWordEntity(
            id = dto.id,

            word = dto.word,
            language = dto.language,
            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: QuicklyAddedWordEntity): QuicklyAddedWordDTO {
        return QuicklyAddedWordDTO(
            id = entity.id,

            word = entity.word, language = entity.language,
            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}