package com.ord.features.game.variants.crossword.dto.api_responses

import com.ord.features.game.variants.shared.dto.api_responses.FinishedGameResponse
import com.ord.features.game.variants.shared.dto.api_responses.helpers.IdentifiableReviewedWordAnswer
import com.ord.features.game.variants.shared.dto.api_responses.helpers.ReviewedWordAnswer

data class CrosswordReviewedAnswers(
    val finalWord: ReviewedWordAnswer,
    val questions: Set<IdentifiableReviewedWordAnswer>
)

typealias FinishedCrosswordGameResponse = FinishedGameResponse<CrosswordReviewedAnswers>