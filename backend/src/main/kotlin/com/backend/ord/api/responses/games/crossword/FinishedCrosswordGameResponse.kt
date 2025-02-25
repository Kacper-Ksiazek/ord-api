package com.backend.ord.api.responses.games.crossword

import com.backend.ord.api.responses.games.IdentifiableProperAnswer
import com.backend.ord.api.responses.games.ProperAnswer
import com.backend.ord.api.responses.games.bases.FinishedGameResponseBase
import com.backend.ord.utils.data_classes.Percentage

class FinishedCrosswordGameResponse(
    finalScore: Double,

    val properFinalWord: ProperAnswer,
    val properQuestionsAnswers: Set<IdentifiableProperAnswer>,
) : FinishedGameResponseBase(finalScore) {

    constructor(
        totalPoints: Int,
        properFinalWord: ProperAnswer,
        properQuestionsAnswers: List<IdentifiableProperAnswer>
    ) : this(
        finalScore = Percentage(totalPoints).value,
        properFinalWord = properFinalWord,
        properQuestionsAnswers = properQuestionsAnswers.toSet()
    )
}