package com.ord.features.game.variants.shared.dto.api_responses

import com.ord.features.game.model.ongoing_game.enums.GameGrade
import com.ord.shared.utils.data_classes.Percentage

data class FinishedGameResponse<TReviewedAnswers>(
    val score: Int,
    val maxScore: Int,
    val reviewedAnswers: TReviewedAnswers
) {
    val grade: GameGrade
        get() = GameGrade.Companion.fromPercentage(accuracyAsPercentage)

    val accuracy: String
        get() = accuracyAsPercentage.toString()

    private val accuracyAsPercentage
        get() = Percentage(100 * score.toDouble() / maxScore.toDouble())
}