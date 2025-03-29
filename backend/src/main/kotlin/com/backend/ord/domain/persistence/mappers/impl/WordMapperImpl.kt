package com.backend.ord.domain.persistence.mappers.impl

import com.backend.ord.domain.persistence.dto.WordDTO
import com.backend.ord.domain.persistence.entities.Word
import com.backend.ord.domain.persistence.mappers.BankMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.domain.persistence.mappers.WordMapper
import org.springframework.stereotype.Component

@Component
class WordMapperImpl(
    private val userMapper: UserMapper,
    private val bankMapper: BankMapper
) : WordMapper {
    override fun toEntity(dto: WordDTO): Word {
        return Word(
            id = dto.id,

            type = dto.type,
            points = dto.points,
            origin = dto.origin,
            useCases = dto.useCases,
            extraMark = dto.extraMark,
            definition = dto.definition,
            translation = dto.translation,
            isCompleted = dto.isCompleted,
            isBookmarked = dto.isBookmarked,
            translatedFrom = dto.translatedFrom,
            translatedTo = dto.translatedTo,
            exampleSentences = dto.exampleSentences,

            userId = dto.userId,
            user = userMapper.toEntity(dto.user),

            bankId = dto.bankId,
            bank = bankMapper.toEntityOrNull(dto.bank),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            completedAt = dto.completedAt
        )
    }

    override fun toDTO(entity: Word): WordDTO {
        return WordDTO(
            id = entity.id,

            type = entity.type,
            points = entity.points,
            origin = entity.origin,
            useCases = entity.useCases,
            extraMark = entity.extraMark,
            definition = entity.definition,
            translation = entity.translation,
            isCompleted = entity.isCompleted,
            isBookmarked = entity.isBookmarked,
            translatedFrom = entity.translatedFrom,
            translatedTo = entity.translatedTo,
            exampleSentences = entity.exampleSentences,

            userId = entity.userId,
            user = userMapper.toDTO(entity.user),

            bankId = entity.bankId,
            bank = bankMapper.toDTOOrNull(entity.bank),

            bankGroupId = entity.bank?.bankGroupId,

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            completedAt = entity.completedAt
        )
    }
}