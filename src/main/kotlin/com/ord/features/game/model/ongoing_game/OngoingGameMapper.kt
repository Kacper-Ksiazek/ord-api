package com.ord.features.game.model.ongoing_game

import com.ord.core.user.model.UserMapper
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.json.CrosswordProperAnswers
import com.ord.features.game.model.ongoing_game.json.WordsTypingProperAnswers
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ord.features.game.model.ongoing_game.json.SentencesWritingProperAnswers
import io.r2dbc.postgresql.codec.Json
import org.springframework.stereotype.Component

fun OngoingGameEntity.toSentencesWritingDTO(ongoingGameMapper: OngoingGameMapper): OngoingSentencesWritingGameDTO {
    if (this.type != GameType.SENTENCES_WRITING) {
        throw IllegalArgumentException("Entity type is not SENTENCES_WRITING")
    }

    return ongoingGameMapper.toSentencesWritingDTO(this)
}

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
            properAnswers = serializeProperAnswers(dto.properAnswers),

            userId = dto.userId,
            createdAt = dto.createdAt
        )
    }


    fun <T> serializeProperAnswers(properAnswers: T): Json {
        return Json.of(jsonObjectMapper.writeValueAsString(properAnswers))
    }

    private fun <T : Any> OngoingGameEntity.convertToCertainDTO(typeReference: TypeReference<T>): OngoingGameDTO<T> {
        return OngoingGameDTO(
            id = id ?: error("OngoingGame id must not be null"),
            properAnswers = jsonObjectMapper.readValue(properAnswers.asString(), typeReference),
            type = type,
            language = language,
            difficulty = difficulty,
            userId = userId,
            createdAt = createdAt
        )
    }
}

