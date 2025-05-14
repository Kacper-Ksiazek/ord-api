package com.backend.ord.domain.persistence.mappers.impl

import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.domain.persistence.dto.OngoingCrosswordGameDTO
import com.backend.ord.domain.persistence.dto.OngoingGameDTO
import com.backend.ord.domain.persistence.dto.OngoingWordsTypingGameDTO
import com.backend.ord.domain.persistence.entities.OngoingGame
import com.backend.ord.domain.persistence.jsons.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.domain.persistence.jsons.game_proper_answers.WordsTypingProperAnswers
import com.backend.ord.domain.persistence.mappers.OngoingGameMapper
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component

@Component
class OngoingGameMapperImpl(
    private val userMapper: UserMapper,
) : OngoingGameMapper {
    val jsonObjectMapper = jacksonObjectMapper()

    override fun toCrosswordDTO(entity: OngoingGame): OngoingCrosswordGameDTO {
        return entity.convertToCertainDTO(object : TypeReference<CrosswordProperAnswers>() {})
    }

    override fun toWordsTypingDTO(entity: OngoingGame): OngoingWordsTypingGameDTO {
        return entity.convertToCertainDTO(object : TypeReference<WordsTypingProperAnswers>() {})
    }

//    override fun toDTO(entity: OngoingGame): OngoingGameDTO<*> {
//        return when (entity.type) {
//            GameType.CROSSWORD -> toCrosswordDTO(entity)
//            else -> throw IllegalArgumentException("Invalid game type")
//        }
//    }

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