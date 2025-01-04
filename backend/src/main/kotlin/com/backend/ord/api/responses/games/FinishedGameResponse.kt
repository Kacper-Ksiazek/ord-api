package com.backend.ord.api.responses.games

import com.backend.ord.config.ScoringResult
import com.backend.ord.enums.persistence.game.GameGrade
import com.backend.ord.utils.data_classes.Percentage


data class FinishedGameResponse(
    val finalScore: Double,
    val properAnswers: Set<ProperAnswers>,
) {
    private val _finalScore: Percentage
        get() = Percentage(finalScore)

    val grade: GameGrade
        get() = GameGrade.fromPercentage(_finalScore)

    constructor(
        totalPoints: Int,
        properAnswers: List<ProperAnswers>
    ) : this(
        finalScore = Percentage(totalPoints).value,
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
