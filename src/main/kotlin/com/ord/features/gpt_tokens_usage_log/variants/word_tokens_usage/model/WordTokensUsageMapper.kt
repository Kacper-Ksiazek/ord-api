package com.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.model

import com.ord.core.user.model.UserMapper
import com.ord.shared.models.mappers.BidirectionalEntityMapper
import org.springframework.stereotype.Component

@Component
class WordTokensUsageMapper(
    private val userMapper: UserMapper,
) : BidirectionalEntityMapper<WordTokensUsageEntity, WordTokensUsageDTO> {
    override fun toEntity(dto: WordTokensUsageDTO): WordTokensUsageEntity {
        return WordTokensUsageEntity(
            id = dto.id,

            cost = dto.cost,
            word = dto.word,
            inputTokens = dto.inputTokens,
            outputTokens = dto.outputTokens,
            priceForMlnInputTokens = dto.priceForMlnInputTokens,
            priceForMlnOutputTokens = dto.priceForMlnOutputTokens,

            translatedTo = dto.translatedTo,
            language = dto.language,
            consumptionType = dto.consumptionType,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: WordTokensUsageEntity): WordTokensUsageDTO {
        return WordTokensUsageDTO(
            id = entity.id,

            cost = entity.cost,
            word = entity.word,
            inputTokens = entity.inputTokens,
            outputTokens = entity.outputTokens,
            priceForMlnInputTokens = entity.priceForMlnInputTokens,
            priceForMlnOutputTokens = entity.priceForMlnOutputTokens,

            translatedTo = entity.translatedTo,
            language = entity.language,
            consumptionType = entity.consumptionType,

            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}