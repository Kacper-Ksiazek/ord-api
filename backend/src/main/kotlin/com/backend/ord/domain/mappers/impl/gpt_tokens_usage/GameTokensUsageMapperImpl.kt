package com.backend.ord.domain.mappers.impl.gpt_tokens_usage

import com.backend.ord.domain.dto.gpt_tokens_usage.GameTokensUsageDTO
import com.backend.ord.domain.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.domain.mappers.GameMapper
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.domain.mappers.gpt_tokens_usage.GameTokensUsageMapper
import org.springframework.stereotype.Component

@Component
class GameTokensUsageMapperImpl(
    private val userMapper: UserMapper,
    private val gameMapper: GameMapper
) : GameTokensUsageMapper {
    override fun toEntity(dto: GameTokensUsageDTO): GameTokensUsage {
        return GameTokensUsage(
            id = dto.id,

            gameType = dto.gameType,
            gameDifficulty = dto.gameDifficulty,
            consumptionType = dto.consumptionType,
            leadingLanguage = dto.leadingLanguage,
            instructionLanguage = dto.instructionLanguage,

            cost = dto.cost,
            inputTokens = dto.inputTokens,
            outputTokens = dto.outputTokens,
            priceForMlnInputTokens = dto.priceForMlnInputTokens,
            priceForMlnOutputTokens = dto.priceForMlnOutputTokens,

            user = userMapper.toEntity(dto.user),
            game = gameMapper.toEntity(dto.game),

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
            leadingLanguage = entity.leadingLanguage,
            instructionLanguage = entity.instructionLanguage,

            cost = entity.cost,
            inputTokens = entity.inputTokens,
            outputTokens = entity.outputTokens,
            priceForMlnInputTokens = entity.priceForMlnInputTokens,
            priceForMlnOutputTokens = entity.priceForMlnOutputTokens,

            user = userMapper.toDTO(entity.user),
            game = gameMapper.toDTO(entity.game),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}