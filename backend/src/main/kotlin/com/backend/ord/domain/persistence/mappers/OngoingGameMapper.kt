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

    override fun toDTO(entity: OngoingGame): OngoingGameDTO<*> {
        return when (entity.type) {
            GameType.CROSSWORD -> toCrosswordDTO(entity)
            GameType.WORDS_TYPING -> toWordsTypingDTO(entity)
            else -> throw IllegalArgumentException("Unsupported type: ${entity.type}")
        }
    }
}