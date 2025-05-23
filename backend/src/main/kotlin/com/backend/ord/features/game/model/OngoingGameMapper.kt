package com.backend.ord.features.game.model

import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.features.game.model.enums.GameType
import com.backend.ord.features.game.model.json.CrosswordProperAnswers
import com.backend.ord.features.game.model.json.WordsTypingProperAnswers
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component

@Component
class OngoingGameMapper(
    private val userMapper: UserMapper,
) {
    val jsonObjectMapper = jacksonObjectMapper()

    fun toCrosswordDTO(entity: OngoingGame): OngoingCrosswordGameDTO {
        return entity.convertToCertainDTO(object : TypeReference<CrosswordProperAnswers>() {})
    }

    fun toWordsTypingDTO(entity: OngoingGame): OngoingWordsTypingGameDTO {
        return entity.convertToCertainDTO(object : TypeReference<WordsTypingProperAnswers>() {})
    }

    fun toDTO(entity: OngoingGame): OngoingGameDTO<*> {
        return when (entity.type) {
            GameType.CROSSWORD -> toCrosswordDTO(entity)
            GameType.WORDS_TYPING -> toWordsTypingDTO(entity)
            else -> throw IllegalArgumentException("Invalid game type")
        }
    }

    fun toEntity(dto: OngoingGameDTO<*>): OngoingGame {
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

    private fun <T : Any> OngoingGame.convertToCertainDTO(typeReference: TypeReference<T>): OngoingGameDTO<T> {
        return OngoingGameDTO(
            id = id,
            properAnswers = jsonObjectMapper.readValue(properAnswers, typeReference),
            type = type,
            language = language,
            difficulty = difficulty,
            user = userMapper.toDTO(user),
            createdAt = createdAt
        )
    }
}

