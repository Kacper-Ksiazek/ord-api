package com.ord.features.game.model.ongoing_game

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.game.model.ongoing_game.json.CrosswordProperAnswers
import com.ord.features.game.model.ongoing_game.json.WordsTypingProperAnswers
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
