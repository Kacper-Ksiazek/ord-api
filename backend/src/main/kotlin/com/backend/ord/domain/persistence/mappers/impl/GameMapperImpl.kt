package com.backend.ord.domain.persistence.mappers.impl

import com.backend.ord.domain.persistence.dto.game.CrosswordGameDTO
import com.backend.ord.domain.persistence.dto.game.GameDTOBase
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordGameProperAnswers
import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.domain.persistence.entities.Game
import com.backend.ord.domain.persistence.mappers.GameMapper
import com.backend.ord.domain.persistence.mappers.UserMapper
import com.backend.ord.enums.persistence.game.GameType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component

@Component
class GameMapperImpl(
    private val userMapper: UserMapper,
) : GameMapper {
    val jsonObjectMapper = jacksonObjectMapper()

    override fun toEntity(dto: GameDTOBase<*, *>): Game {
        return Game(
            id = dto.id,

            type = dto.type,
            grade = dto.grade,
            status = dto.status,
            language = dto.language,
            difficulty = dto.difficulty,
            instruction = jsonObjectMapper.writeValueAsString(dto.instruction),
            properAnswers = jsonObjectMapper.writeValueAsString(dto.properAnswers),

            duration = dto.duration,
            finalScore = dto.finalScore,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: Game): GameDTOBase<*, *> {
        return when (entity.type) {
            GameType.CROSSWORD -> toCrosswordDTO(entity)
            else -> throw IllegalArgumentException("Invalid game type")
        }
    }

    override fun toCrosswordDTO(entity: Game): CrosswordGameDTO {
        return CrosswordGameDTO(
            id = entity.id,

            type = entity.type,
            grade = entity.grade,
            status = entity.status,
            language = entity.language,
            difficulty = entity.difficulty,
            instruction = jsonObjectMapper.readValue(entity.instruction, CrosswordInstruction::class.java),
            properAnswers = jsonObjectMapper.readValue(entity.properAnswers, CrosswordGameProperAnswers::class.java),

            duration = entity.duration,
            finalScore = entity.finalScore,

            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}