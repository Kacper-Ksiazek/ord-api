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
            numberOfTokens = dto.numberOfTokens,
            consumptionType = dto.consumptionType,

            user = userMapper.toEntity(dto.user),
            word = wordMapper.toEntity(dto.word),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: WordTokensUsage): WordTokensUsageDTO {
        return WordTokensUsageDTO(
            id = entity.id,

            numberOfTokens = entity.numberOfTokens,
            consumptionType = entity.consumptionType,

            user = userMapper.toDTO(entity.user),
            word = wordMapper.toDTO(entity.word),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}