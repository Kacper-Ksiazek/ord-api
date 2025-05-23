package com.backend.ord.features.game.variants.shared.dto.api_responses.helpers

import com.backend.ord.features.game.services.GameReviewService
import com.backend.ord.features.game.variants.shared.enums.AnswerScore
import com.backend.ord.utils.data_classes.Percentage
import java.util.*

data class IdentifiableProperAnswer(
    val id: UUID,
    val expectedAnswer: String,
    val userAnswer: String?,
    val score: AnswerScore,
)

fun Set<IdentifiableProperAnswer>.computeFinalScore(
    moduleRatio: Percentage = Percentage(100),
): Int {
    val receivedPoints: Double = this.sumOf { it.score.wage }

    return GameReviewService.computeFinalScoreComponent(
        receivedPoints = receivedPoints,
        moduleRatio = moduleRatio,
        maxPoints = this.size * AnswerScore.CORRECT.wage,
    )
}