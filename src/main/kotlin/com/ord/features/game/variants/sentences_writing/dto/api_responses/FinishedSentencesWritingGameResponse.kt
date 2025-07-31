package com.ord.features.game.variants.sentences_writing.dto.api_responses

import com.ord.features.game.variants.sentences_writing.ai.dto.review.SentencesWritingEvaluationCriteria
import com.ord.features.game.variants.shared.dto.api_responses.FinishedGameResponse
import java.util.UUID

data class ReviewedSentencesWritingSingleTopic(
    val id: UUID,
    val word: String,
    val topic: String,
    val score: Int,
    val maxScore: Int,
    val suggestedCorrectAnswer: String?,
    val evaluationCriteria: SentencesWritingEvaluationCriteria
)

typealias FinishedSentencesWritingGameResponse = FinishedGameResponse<Set<ReviewedSentencesWritingSingleTopic>>