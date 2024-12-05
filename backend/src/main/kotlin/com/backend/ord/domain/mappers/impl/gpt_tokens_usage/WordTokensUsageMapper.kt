package com.backend.ord.domain.mappers.impl.gpt_tokens_usage

import com.backend.ord.domain.dto.gpt_tokens_usage.WordTokensUsageDTO
import com.backend.ord.domain.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.domain.mappers.gpt_tokens_usage.WordTokensUsageMapper
import org.springframework.stereotype.Component

@Component
class WordTokensUsageMapper(
    private val userMapper: UserMapper,
) : WordTokensUsageMapper {
    override fun toEntity(dto: WordTokensUsageDTO): WordTokensUsage {
        return WordTokensUsage(
            id = dto.id,

            cost = dto.cost,
            word = dto.word,
            inputTokens = dto.inputTokens,
            outputTokens = dto.outputTokens,
            priceForMlnInputTokens = dto.priceForMlnInputTokens,
            priceForMlnOutputTokens = dto.priceForMlnOutputTokens,

            translatedTo = dto.translatedTo,
            translatedFrom = dto.translatedFrom,
            consumptionType = dto.consumptionType,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: WordTokensUsage): WordTokensUsageDTO {
        return WordTokensUsageDTO(
            id = entity.id,

            cost = entity.cost,
            word = entity.word,
            inputTokens = entity.inputTokens,
            outputTokens = entity.outputTokens,
            priceForMlnInputTokens = entity.priceForMlnInputTokens,
            priceForMlnOutputTokens = entity.priceForMlnOutputTokens,

            translatedTo = entity.translatedTo,
            translatedFrom = entity.translatedFrom,
            consumptionType = entity.consumptionType,

            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}