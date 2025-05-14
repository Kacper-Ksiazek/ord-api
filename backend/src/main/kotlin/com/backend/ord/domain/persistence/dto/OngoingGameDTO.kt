package com.backend.ord.domain.persistence.dto

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.domain.persistence.jsons.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.domain.persistence.jsons.game_proper_answers.WordsTypingProperAnswers
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.GameType
import java.time.Instant
import java.util.*

data class OngoingGameDTO<TProperAnswers>(
    val id: UUID = UUID.randomUUID(),

    val properAnswers: TProperAnswers,

    val type: GameType,
    val language: LanguageName,
    val difficulty: GameDifficulty,

    val user: UserDTO,
    val userId: UUID = user.id,

    var createdAt: Instant = Instant.now()
)

typealias OngoingCrosswordGameDTO = OngoingGameDTO<CrosswordProperAnswers>
typealias OngoingWordsTypingGameDTO = OngoingGameDTO<WordsTypingProperAnswers>
