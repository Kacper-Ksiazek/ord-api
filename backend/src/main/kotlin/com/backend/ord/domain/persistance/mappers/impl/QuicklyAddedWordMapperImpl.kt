package com.backend.ord.domain.persistance.mappers.impl

import com.backend.ord.domain.persistance.dto.QuicklyAddedWordDTO
import com.backend.ord.domain.persistance.entities.QuicklyAddedWord
import com.backend.ord.domain.persistance.mappers.QuicklyAddedWordMapper
import org.springframework.stereotype.Component

@Component
class QuicklyAddedWordMapperImpl(
    private val userMapper: UserMapperImpl
) : QuicklyAddedWordMapper {
    override fun toEntity(dto: QuicklyAddedWordDTO): QuicklyAddedWord {
        return QuicklyAddedWord(
            id = dto.id,

            word = dto.word,
            language = dto.language,
            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: QuicklyAddedWord): QuicklyAddedWordDTO {
        return QuicklyAddedWordDTO(
            id = entity.id,

            word = entity.word, language = entity.language,
            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

}