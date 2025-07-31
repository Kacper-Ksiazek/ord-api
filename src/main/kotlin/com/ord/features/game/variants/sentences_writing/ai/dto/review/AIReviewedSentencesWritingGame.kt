package com.ord.features.game.variants.sentences_writing.ai.dto.review

import java.util.UUID

data class AIReviewedSingleTopic(
    val topicId: UUID,
    val evaluationCriteria: SentencesWritingEvaluationCriteria,
    val suggestedCorrectAnswer: String?
)

typealias AIReviewedSentencesWritingGame = List<AIReviewedSingleTopic>
