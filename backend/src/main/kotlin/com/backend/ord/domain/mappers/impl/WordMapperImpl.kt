package com.backend.ord.domain.mappers.impl

import com.backend.ord.domain.dto.WordDTO
import com.backend.ord.domain.entities.Word
import com.backend.ord.domain.mappers.BankMapper
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.domain.mappers.WordMapper
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
            extraMark = dto.extraMark,
            translation = dto.translation,
            isBookmarked = dto.isBookmarked,
            translatedFrom = dto.translatedFrom,
            translatedTo = dto.translatedTo,
            exampleSentences = dto.exampleSentences,

            user = userMapper.toEntity(dto.user),
            bank = bankMapper.toEntityOrNull(dto.bank),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: Word): WordDTO {
        return WordDTO(
            id = entity.id,

            type = entity.type,
            points = entity.points,
            origin = entity.origin,
            extraMark = entity.extraMark,
            translation = entity.translation,
            isBookmarked = entity.isBookmarked,
            translatedFrom = entity.translatedFrom,
            translatedTo = entity.translatedTo,
            exampleSentences = entity.exampleSentences,

            user = userMapper.toDTO(entity.user),
            bank = bankMapper.toDTOOrNull(entity.bank),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}