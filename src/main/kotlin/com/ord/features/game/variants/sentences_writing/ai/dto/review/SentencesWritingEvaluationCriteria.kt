package com.ord.features.game.variants.sentences_writing.ai.dto.review

import com.ord.shared.domain.dto.ScoringCriteria

data class SentencesWritingEvaluationCriteria(
    val fitsTopic: Boolean,
    val vocabulary: ScoringCriteria,
    val answerLength: ScoringCriteria,
    val correctWordUsage: ScoringCriteria
)
