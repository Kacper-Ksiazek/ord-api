package com.backend.ord.api.responses.games

import com.backend.ord.config.ScoringResult
import com.backend.ord.enums.persistence.game.GameGrade
import com.backend.ord.utils.data_classes.Percentage


data class FinishedGameResponse(
    val finalScore: Percentage,
    val properAnswers: Set<ProperAnswers>,
) {
    val grade: GameGrade
        get() = GameGrade.fromPercentage(finalScore)

    constructor(
        totalPoints: Int,
        properAnswers: List<ProperAnswers>
    ) : this(
        finalScore = Percentage(totalPoints),
        properAnswers = properAnswers.toSet()
    )

    companion object {
        data class ProperAnswers(
            val expectedAnswer: String,
            val userAnswer: String?,
            val result: ScoringResult,
        )
    }
}
