package com.ord.core.word.models.word

import com.ord.core.word.models.word_progress.WordProgressDTO
import com.ord.features.bank.model.BankMapper
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class WordMapper(
    private val bankMapper: BankMapper,
) : BidirectionalEntityMapper<WordEntity, WordDTO> {
    override fun toEntity(dto: WordDTO): WordEntity {
        return WordEntity(
            id = dto.id,
            status = dto.status,
            type = dto.type,
            sourceWord = dto.sourceWord,
            translation = dto.translation,
            definition = dto.definition,
            extraMark = dto.extraMark,
            language = dto.language,
            isBookmarked = dto.isBookmarked,
            userId = dto.userId,
            bankId = dto.bankId,
            bankGroupId = dto.bankGroupId,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
        )
    }

    override fun toDTO(entity: WordEntity): WordDTO {
        return WordDTO(
            id = entity.id ?: error("Word ID must not be null"),
            status = entity.status,
            type = entity.type,
            sourceWord = entity.sourceWord,
            translation = entity.translation,
            definition = entity.definition,
            extraMark = entity.extraMark,
            language = entity.language,
            isBookmarked = entity.isBookmarked,
            userId = entity.userId,
            bankId = entity.bankId,
            bank = null,
            bankGroupId = entity.bankGroupId,
            progress = null,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    fun toDTO(entity: WordEntity, progress: WordProgressDTO?): WordDTO {
        return toDTO(entity).apply { this.progress = progress }
    }
}
