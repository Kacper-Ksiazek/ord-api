package com.backend.ord.domain.persistence.mappers

import com.backend.ord.domain.persistence.dto.OngoingCrosswordGameDTO
import com.backend.ord.domain.persistence.dto.OngoingGameDTO
import com.backend.ord.domain.persistence.dto.OngoingWordsTypingGameDTO
import com.backend.ord.domain.persistence.entities.OngoingGame
import com.backend.ord.domain.persistence.mappers.bases.MapperBase
import com.backend.ord.enums.persistence.game.GameType

interface OngoingGameMapper : MapperBase<OngoingGame, OngoingGameDTO<*>> {
    fun toCrosswordDTO(entity: OngoingGame): OngoingCrosswordGameDTO

    fun toWordsTypingDTO(entity: OngoingGame): OngoingWordsTypingGameDTO

    fun <T : OngoingGameDTO<*>> toDTO(entity: OngoingGame, clazz: Class<T>): T {
        val dto: OngoingGameDTO<*> = when (entity.type) {
            GameType.CROSSWORD -> toCrosswordDTO(entity)
            GameType.WORDS_TYPING -> toWordsTypingDTO(entity)
            else -> throw IllegalArgumentException("Unsupported type: ${entity.type}")
        }

        if (!clazz.isInstance(dto)) {
            throw IllegalArgumentException("Expected type ${clazz.simpleName}, but got ${dto::class.simpleName}")
        }

        return clazz.cast(dto)
    }
}