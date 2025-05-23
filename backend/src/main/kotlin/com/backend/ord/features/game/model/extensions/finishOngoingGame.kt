package com.backend.ord.features.game.model.extensions

import com.backend.ord.domain.persistence.entities.FinishedGame
import com.backend.ord.features.game.model.OngoingGame
import com.backend.ord.features.game.model.enums.GameGrade
import com.backend.ord.features.game.model.enums.GameResult
import com.backend.ord.utils.data_classes.Percentage

fun OngoingGame.finish(
    finalScore: Int,
    duration: String,
    result: GameResult
): FinishedGame {
    val grade = when (result) {
        GameResult.COMPLETED -> GameGrade.fromPercentage(Percentage(finalScore))
        else -> GameGrade.NA
    }

    return FinishedGame(
        duration = duration,
        finalScore = finalScore,

        type = type,
        grade = grade,
        result = result,
        language = language,
        difficulty = difficulty,

        user = user
    )
}
