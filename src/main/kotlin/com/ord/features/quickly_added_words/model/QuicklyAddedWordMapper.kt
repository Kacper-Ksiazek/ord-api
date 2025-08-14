package com.ord.features.quickly_added_words.model

import com.ord.core.user.model.UserMapper
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class QuicklyAddedWordMapper(
    private val userMapper: UserMapper
): BidirectionalEntityMapper<QuicklyAddedWordEntity, QuicklyAddedWordDTO> {
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