package com.backend.ord.api.responses.games

import com.backend.ord.api.responses.games.bases.FinishedGameResponseBase
import com.backend.ord.api.responses.games.utils.IdentifiableProperAnswer
import com.backend.ord.api.responses.games.utils.ProperAnswer
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