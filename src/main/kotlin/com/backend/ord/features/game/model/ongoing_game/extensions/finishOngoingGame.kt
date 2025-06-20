package com.backend.ord.features.game.model.ongoing_game.extensions

import com.backend.ord.features.game.model.finished_game.FinishedGameEntity
import com.backend.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.backend.ord.features.game.model.ongoing_game.enums.GameGrade
import com.backend.ord.features.game.model.ongoing_game.enums.GameResult
import com.backend.ord.shared.utils.data_classes.Percentage

fun OngoingGameEntity.finish(
    finalScore: Int,
    duration: String,
    result: GameResult
): FinishedGameEntity {
    val grade = when (result) {
        GameResult.COMPLETED -> GameGrade.fromPercentage(Percentage(finalScore))
        else -> GameGrade.NA
    }

    return FinishedGameEntity(
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
