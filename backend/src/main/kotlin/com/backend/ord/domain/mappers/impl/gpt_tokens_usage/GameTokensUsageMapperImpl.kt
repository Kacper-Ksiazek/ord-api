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
): GameTokensUsageMapper {
    override fun toEntity(dto: GameTokensUsageDTO): GameTokensUsage {
        return GameTokensUsage(
            id = dto.id,
            numberOfTokens = dto.numberOfTokens,
            consumptionType = dto.consumptionType,

            user = userMapper.toEntity(dto.user),
            game = gameMapper.toEntity(dto.game),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: GameTokensUsage): GameTokensUsageDTO {
        return GameTokensUsageDTO(
            id = entity.id,

            numberOfTokens = entity.numberOfTokens,
            consumptionType = entity.consumptionType,

            user = userMapper.toDTO(entity.user),
            game = gameMapper.toDTO(entity.game),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}