package com.backend.ord.domain.mappers.impl.gpt_tokens_usage

import com.backend.ord.domain.dto.gpt_tokens_usage.WordTokensUsageDTO
import com.backend.ord.domain.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.domain.mappers.WordMapper
import com.backend.ord.domain.mappers.gpt_tokens_usage.WordTokensUsageMapper
import org.springframework.stereotype.Component

@Component
class WordTokensUsageMapper(
    private val userMapper: UserMapper,
    private val wordMapper: WordMapper
) : WordTokensUsageMapper {
    override fun toEntity(dto: WordTokensUsageDTO): WordTokensUsage {
        return WordTokensUsage(
            id = dto.id,

            word = dto.word,
            translatedTo = dto.translatedTo,
            translatedFrom = dto.translatedFrom,
            numberOfTokens = dto.numberOfTokens,
            consumptionType = dto.consumptionType,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: WordTokensUsage): WordTokensUsageDTO {
        return WordTokensUsageDTO(
            id = entity.id,

            user = userMapper.toDTO(entity.user),
            word = entity.word,
            translatedTo = entity.translatedTo,
            translatedFrom = entity.translatedFrom,
            numberOfTokens = entity.numberOfTokens,
            consumptionType = entity.consumptionType,

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}