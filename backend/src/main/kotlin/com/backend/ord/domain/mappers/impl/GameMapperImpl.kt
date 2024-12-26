package com.backend.ord.domain.mappers.impl

import com.backend.ord.domain.dto.game.CrosswordGameDTO
import com.backend.ord.domain.dto.game.GameDTOBase
import com.backend.ord.domain.embedded.game_instructions.CrosswordInstruction
import com.backend.ord.domain.entities.Game
import com.backend.ord.domain.mappers.GameMapper
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.enums.game.GameType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component

@Component
class GameMapperImpl(
    private val userMapper: UserMapper,
) : GameMapper {
    val jsonObjectMapper = jacksonObjectMapper()

    override fun toEntity(dto: GameDTOBase<*>): Game {
        return Game(
            id = dto.id,

            type = dto.type,
            status = dto.status,
            language = dto.language,
            difficulty = dto.difficulty,
            instruction = jsonObjectMapper.writeValueAsString(dto.instruction),

            duration = dto.duration,
            finalScore = dto.finalScore,

            user = userMapper.toEntity(dto.user),

            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    override fun toDTO(entity: Game): GameDTOBase<*> {
        return when (entity.type) {
            GameType.CROSSWORD -> toCrosswordDTO(entity)
            else -> throw IllegalArgumentException("Invalid game type")
        }
    }

    override fun toCrosswordDTO(entity: Game): CrosswordGameDTO {
        return CrosswordGameDTO(
            id = entity.id,

            type = entity.type,
            status = entity.status,
            language = entity.language,
            difficulty = entity.difficulty,
            instruction = jsonObjectMapper.readValue(entity.instruction, CrosswordInstruction::class.java),

            duration = entity.duration,
            finalScore = entity.finalScore,

            user = userMapper.toDTO(entity.user),

            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

}