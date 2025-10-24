package com.ord.features.quickly_added_words.model

import com.ord.core.user.model.UserMapper
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class QuicklyAddedWordMapper(
    private val userMapper: UserMapper
) : BidirectionalEntityMapper<QuicklyAddedWordEntity, QuicklyAddedWordDTO> {
    override fun toEntity(dto: QuicklyAddedWordDTO): QuicklyAddedWordEntity {
        return QuicklyAddedWordEntity(
            id = dto.id,

            word = dto.word,
            language = dto.language,

            definition = dto.definition,
            extraMark = dto.extraMark,
            type = dto.type,

            isApproved = dto.isApproved,

            createdAt = dto.createdAt,

            userId = dto.userId,
        )
    }

    override fun toDTO(entity: QuicklyAddedWordEntity): QuicklyAddedWordDTO {
        return QuicklyAddedWordDTO(
            id = entity.id ?: error("Entity id is null"),

            word = entity.word,
            language = entity.language,

            definition = entity.definition,
            extraMark = entity.extraMark,
            type = entity.type,

            isApproved = entity.isApproved,

            userId = entity.userId,

            createdAt = entity.createdAt
        )
    }
}