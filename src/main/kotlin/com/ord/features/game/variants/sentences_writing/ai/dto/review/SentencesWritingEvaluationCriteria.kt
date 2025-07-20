package com.ord.features.game.variants.sentences_writing.ai.dto.review

import com.ord.shared.domain.dto.ScoringCriteria

data class SentencesWritingEvaluationCriteria(
    val sentenceLengthValid: ScoringCriteria,
    val vocabularyProficiencySuitable: ScoringCriteria,
    val answerSizeValid: ScoringCriteria,
    val wordUsageCorrect: ScoringCriteria
)
