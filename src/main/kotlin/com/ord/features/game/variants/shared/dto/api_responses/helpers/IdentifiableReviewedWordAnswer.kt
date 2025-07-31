package com.ord.features.game.variants.shared.dto.api_responses.helpers

import com.ord.features.game.services.GameReviewService
import com.ord.features.game.variants.shared.enums.WordAnswerScore
import com.ord.shared.utils.data_classes.Percentage
import java.util.*

data class IdentifiableReviewedWordAnswer(
    val id: UUID,
    val expectedAnswer: String,
    val userAnswer: String?,
    val score: WordAnswerScore,
)

fun Set<IdentifiableReviewedWordAnswer>.calculatedWeightedModuleScore(
    gameMaxScore: Int,
    moduleWeight: Percentage = Percentage(100),
): Int {
    val earnedPoints: Double = this.sumOf { it.score.wage }

    return GameReviewService.calculatedWeightedModuleScore(
        earnedPoints = earnedPoints,
        pointsToEarn = this.size * WordAnswerScore.CORRECT.wage,
        moduleWeight = moduleWeight,
        gameMaxScore = gameMaxScore,
    )
}