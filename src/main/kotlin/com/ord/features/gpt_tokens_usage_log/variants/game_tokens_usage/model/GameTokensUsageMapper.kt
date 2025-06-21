package com.ord.features.gpt_tokens_usage_log.variants.game_tokens_usage.model

import com.ord.core.user.model.UserMapper
import com.ord.shared.models.mappers.MapperBase
import org.springframework.stereotype.Component

@Component
class GameTokensUsageMapper(
    private val userMapper: UserMapper,
) : MapperBase<GameTokensUsageEntity, GameTokensUsageDTO> {
    override fun toEntity(dto: GameTokensUsageDTO): GameTokensUsageEntity {
        return GameTokensUsageEntity(
            id = dto.id,

            language = dto.language,
            gameType = dto.gameType,
            gameDifficulty = dto.gameDifficulty,
            consumptionType = dto.consumptionType,

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

    override fun toDTO(entity: GameTokensUsageEntity): GameTokensUsageDTO {
        return GameTokensUsageDTO(
            id = entity.id,

            language = entity.language,
            gameType = entity.gameType,
            gameDifficulty = entity.gameDifficulty,
            consumptionType = entity.consumptionType,

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