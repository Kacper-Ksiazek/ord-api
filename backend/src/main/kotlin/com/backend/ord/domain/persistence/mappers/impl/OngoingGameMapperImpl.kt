package com.backend.ord.domain.persistence.mappers.impl

import com.backend.ord.domain.persistence.dto.OngoingCrosswordGameDTO
import com.backend.ord.domain.persistence.dto.OngoingGameDTO
import com.backend.ord.domain.persistence.entities.OngoingGame
import com.backend.ord.domain.persistence.jsons.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component

@Component
class OngoingGameMapperImpl(
    private val userMapper: UserMapper,
) : OngoingGameMapper {
    val jsonObjectMapper = jacksonObjectMapper()

    override fun toCrosswordDTO(entity: OngoingGame): OngoingCrosswordGameDTO {
        return OngoingCrosswordGameDTO(
            id = entity.id,

            properAnswers = jsonObjectMapper.readValue(entity.properAnswers, CrosswordProperAnswers::class.java),

            type = entity.type,
            language = entity.language,
            difficulty = entity.difficulty,

            user = userMapper.toDTO(entity.user),
            createdAt = entity.createdAt
        )
    }

    override fun toDTO(entity: OngoingGame): OngoingGameDTO<*> {
        return when (entity.type) {
            GameType.CROSSWORD -> toCrosswordDTO(entity)
            else -> throw IllegalArgumentException("Invalid game type")
        }
    }

    override fun toEntity(dto: OngoingGameDTO<*>): OngoingGame {
        return OngoingGame(
            id = dto.id,

            type = dto.type,
            language = dto.language,
            difficulty = dto.difficulty,
            properAnswers = jsonObjectMapper.writeValueAsString(dto.properAnswers),

            user = userMapper.toEntity(dto.user),
            createdAt = dto.createdAt
        )
    }
}