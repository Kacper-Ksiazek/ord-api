package com.backend.ord.domain.persistence.mappers

import com.backend.ord.domain.persistence.dto.OngoingCrosswordGameDTO
import com.backend.ord.domain.persistence.dto.OngoingGameDTO
import com.backend.ord.domain.persistence.dto.OngoingWordsTypingGameDTO
import com.backend.ord.domain.persistence.entities.OngoingGame
import com.backend.ord.domain.persistence.mappers.bases.MapperBase

interface OngoingGameMapper : MapperBase<OngoingGame, OngoingGameDTO<*>> {
    fun toCrosswordDTO(entity: OngoingGame): OngoingCrosswordGameDTO

    fun toWordsTypingDTO(entity: OngoingGame): OngoingWordsTypingGameDTO

}