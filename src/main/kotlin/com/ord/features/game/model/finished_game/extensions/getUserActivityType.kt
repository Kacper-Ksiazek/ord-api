package com.ord.features.game.model.finished_game.extensions

import com.ord.features.game.model.finished_game.FinishedGameEntity
import com.ord.features.game.model.ongoing_game.enums.GameGrade
import com.ord.features.game.model.ongoing_game.enums.GameType
import com.ord.features.user_activity_log.model.enums.UserActivityType

fun FinishedGameEntity.getUserActivityType(): UserActivityType {
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
