package com.ord.features.game.model.ongoing_game

import com.ord.core.user.model.UserMapper
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.json.CrosswordProperAnswers
import com.ord.features.game.model.ongoing_game.json.WordsTypingProperAnswers
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.features.game.model.ongoing_game.json.SentencesWritingProperAnswers
import org.springframework.stereotype.Component

@Component
class OngoingGameMapper(
    private val userMapper: UserMapper,
) {
    val jsonObjectMapper = jacksonObjectMapper()

    fun toCrosswordDTO(entity: OngoingGameEntity): OngoingCrosswordGameDTO {
        return entity.convertToCertainDTO(object : TypeReference<CrosswordProperAnswers>() {})
    }

    fun toWordsTypingDTO(entity: OngoingGameEntity): OngoingWordsTypingGameDTO {
        return entity.convertToCertainDTO(object : TypeReference<WordsTypingProperAnswers>() {})
    }

    fun toSentencesWritingDTO(entity: OngoingGameEntity): OngoingSentencesWritingGameDTO {
        return entity.convertToCertainDTO(object : TypeReference<SentencesWritingProperAnswers>() {})
    }

    fun toDTO(entity: OngoingGameEntity): OngoingGameDTO<*> {
        return when (entity.type) {
            GameType.CROSSWORD -> toCrosswordDTO(entity)
            GameType.WORDS_TYPING -> toWordsTypingDTO(entity)
            GameType.SENTENCES_WRITING -> toSentencesWritingDTO(entity)
            else -> throw IllegalArgumentException("Invalid game type")
        }
    }

    fun toEntity(dto: OngoingGameDTO<*>): OngoingGameEntity {
        return OngoingGameEntity(
            id = dto.id,

            type = dto.type,
            language = dto.language,
            difficulty = dto.difficulty,
            properAnswers = jsonObjectMapper.writeValueAsString(dto.properAnswers),

            user = userMapper.toEntity(dto.user),
            createdAt = dto.createdAt
        )
    }

    private fun <T : Any> OngoingGameEntity.convertToCertainDTO(typeReference: TypeReference<T>): OngoingGameDTO<T> {
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

