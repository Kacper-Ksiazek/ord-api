package com.ord.core.word.model

import com.ord.core.user.model.UserMapper
import com.ord.features.bank.model.BankMapper
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class WordMapper(
    private val userMapper: UserMapper,
    private val bankMapper: BankMapper
) : BidirectionalEntityMapper<WordEntity, WordDTO> {
    override fun toEntity(dto: WordDTO): WordEntity {
        return WordEntity(
            id = dto.id,

            type = dto.type,
            origin = dto.origin,
            translation = dto.translation,
            definition = dto.definition,
            extraMark = dto.extraMark,

            translatedFrom = dto.translatedFrom,
            translatedTo = dto.translatedTo,

            isCompleted = dto.isCompleted,
            isBookmarked = dto.isBookmarked,

            points = dto.points,

            userId = dto.userId,
            bankId = dto.bankId,
            bankGroupId = dto.bankGroupId,

            completedAt = dto.completedAt,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: WordEntity): WordDTO {
        return WordDTO(
            id = entity.id ?: error("Word ID must not be null"),

            type = entity.type,
            origin = entity.origin,
            translation = entity.translation,
            definition = entity.definition,
            extraMark = entity.extraMark,

            translatedFrom = entity.translatedFrom,
            translatedTo = entity.translatedTo,

            isCompleted = entity.isCompleted,
            isBookmarked = entity.isBookmarked,

            points = entity.points,

            userId = entity.userId,
            bankId = entity.bankId,
            bank = null,
            bankGroupId = entity.bankGroupId,

            completedAt = entity.completedAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}