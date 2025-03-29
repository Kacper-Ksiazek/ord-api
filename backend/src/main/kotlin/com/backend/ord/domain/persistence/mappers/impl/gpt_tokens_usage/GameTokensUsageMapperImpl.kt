package com.backend.ord.domain.persistence.mappers.impl.gpt_tokens_usage

import com.backend.ord.domain.persistence.dto.gpt_tokens_usage.GameTokensUsageDTO
import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.domain.persistence.mappers.gpt_tokens_usage.GameTokensUsageMapper
import org.springframework.stereotype.Component

@Component
class GameTokensUsageMapperImpl(
    private val userMapper: UserMapper,
) : GameTokensUsageMapper {
    override fun toEntity(dto: GameTokensUsageDTO): GameTokensUsage {
        return GameTokensUsage(
            id = dto.id,

            gameType = dto.gameType,
            gameDifficulty = dto.gameDifficulty,
            consumptionType = dto.consumptionType,
            translatedFrom = dto.translatedFrom,
            instructionLanguage = dto.instructionLanguage,

            cost = dto.cost,
            inputTokens = dto.inputTokens,
            outputTokens = dto.outputTokens,
            priceForMlnInputTokens = dto.priceForMlnInputTokens,
            priceForMlnOutputTokens = dto.priceForMlnOutputTokens,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: GameTokensUsage): GameTokensUsageDTO {
        return GameTokensUsageDTO(
            id = entity.id,

            gameType = entity.gameType,
            gameDifficulty = entity.gameDifficulty,
            consumptionType = entity.consumptionType,
            translatedFrom = entity.translatedFrom,
            instructionLanguage = entity.instructionLanguage,

            cost = entity.cost,
            inputTokens = entity.inputTokens,
            outputTokens = entity.outputTokens,
            priceForMlnInputTokens = entity.priceForMlnInputTokens,
            priceForMlnOutputTokens = entity.priceForMlnOutputTokens,

            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}