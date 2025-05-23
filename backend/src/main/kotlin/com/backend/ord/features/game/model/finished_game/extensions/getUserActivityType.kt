package com.backend.ord.features.game.model.finished_game.extensions

import com.backend.ord.domain.persistence.entities.FinishedGame
import com.backend.ord.enums.persistence.UserActivityType
import com.backend.ord.features.game.model.enums.GameGrade
import com.backend.ord.features.game.model.enums.GameType

fun FinishedGame.getUserActivityType(): UserActivityType {
    return when (type) {
        GameType.CROSSWORD -> when (grade) {
            GameGrade.S -> UserActivityType.CROSSWORD_GAME_COMPLETED_FLAWLESSLY
            else -> UserActivityType.CROSSWORD_GAME_COMPLETED_WITH_MISTAKES
        }

        GameType.WORDS_TYPING -> when (grade) {
            GameGrade.S -> UserActivityType.WORDS_TYPING_GAME_COMPLETED_FLAWLESSLY
            else -> UserActivityType.WORDS_TYPING_GAME_COMPLETED_WITH_MISTAKES
        }

        else -> throw UnsupportedOperationException()
    }
}
