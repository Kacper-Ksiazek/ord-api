package com.backend.ord.features.ongoing_game.variants.crossword.dto.api_responses

import com.backend.ord.features.ongoing_game.variants.shared.dto.api_responses.FinishedGameResponseBase
import com.backend.ord.features.ongoing_game.variants.shared.dto.api_responses.helpers.IdentifiableProperAnswer
import com.backend.ord.features.ongoing_game.variants.shared.dto.api_responses.helpers.ProperAnswer
import com.backend.ord.utils.data_classes.Percentage

class FinishedCrosswordGameResponse(
    finalScore: Double,

    val properFinalWord: ProperAnswer,
    val properQuestionsAnswers: Set<IdentifiableProperAnswer>,
) : FinishedGameResponseBase(finalScore) {

    constructor(
        totalPoints: Int,
        properFinalWord: ProperAnswer,
        properQuestionsAnswers: Set<IdentifiableProperAnswer>
    ) : this(
        finalScore = Percentage(totalPoints).value,
        properFinalWord = properFinalWord,
        properQuestionsAnswers = properQuestionsAnswers
    )
}