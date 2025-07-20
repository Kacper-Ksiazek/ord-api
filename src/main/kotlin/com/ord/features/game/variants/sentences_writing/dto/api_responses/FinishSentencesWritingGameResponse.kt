package com.ord.features.game.variants.sentences_writing.dto.api_responses

import com.ord.features.game.variants.sentences_writing.ai.dto.review.SentencesWritingEvaluationCriteria
import com.ord.features.game.variants.shared.dto.api_responses.FinishedGameResponse
import java.util.UUID

data class ReviewedSentencesWritingSingleTopic(
    val id: UUID,
    val evaluationCriteria: SentencesWritingEvaluationCriteria,
    val points: Int,
    val maxPoints: Int,
    val suggestedCorrectAnswer: String?
)

typealias FinishSentencesWritingGameResponse = FinishedGameResponse<Set<ReviewedSentencesWritingSingleTopic>>