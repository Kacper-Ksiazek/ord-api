package com.ord.features.game.model.ongoing_game.extensions

import com.ord.config.GamesConfig
import com.ord.features.game.model.finished_game.FinishedGameEntity
import com.ord.features.game.model.ongoing_game.OngoingGameEntity
import com.ord.features.game.model.ongoing_game.enums.GameGrade
import com.ord.features.game.model.ongoing_game.enums.GameResult
import com.ord.shared.utils.data_classes.Percentage

fun OngoingGameEntity.finish(
    score: Int,
    duration: String,
): FinishedGameEntity {
    val maxScore = GamesConfig.GameScoring.MaxScore.getMaxScoreForGameType(type)
    val grade = GameGrade.fromPercentage(Percentage(100 * score / maxScore))
    val accuracy: Float = (score / maxScore).toFloat()

    return FinishedGameEntity(
        score = score,
        accuracy = accuracy,
        duration = duration,

        type = type,
        grade = grade,
        language = language,
        difficulty = difficulty,
        result = GameResult.COMPLETED,

        user = user
    )
}

fun OngoingGameEntity.cancel(
    duration: String
): FinishedGameEntity {
    return FinishedGameEntity(
        duration = duration,
        score = 0,
        accuracy = 0f,

        type = type,
        grade = GameGrade.NA,
        result = GameResult.CANCELLED,
        language = language,
        difficulty = difficulty,

        user = user
    )
}
