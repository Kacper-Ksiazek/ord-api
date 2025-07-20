package com.ord.features.game.variants.sentences_writing.ai.dto.review

data class AIReviewedSentencesWritingGame(
    val word: String,
    val evaluationCriteria: SentencesWritingEvaluationCriteria,
    val suggestedCorrectAnswer: String?
)
