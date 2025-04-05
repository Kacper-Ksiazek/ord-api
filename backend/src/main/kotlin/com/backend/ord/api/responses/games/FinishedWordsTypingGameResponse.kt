package com.backend.ord.api.responses.games

import com.backend.ord.api.responses.games.bases.FinishedGameResponseBase
import com.backend.ord.api.responses.games.utils.IdentifiableProperAnswer
import com.backend.ord.utils.data_classes.Percentage

class FinishedWordsTypingGameResponse(
    finalScore: Double,

    val properAnswers: Set<IdentifiableProperAnswer>,
) : FinishedGameResponseBase(finalScore) {

    constructor(
        totalPoints: Int,
        answers: List<IdentifiableProperAnswer>
    ) : this(
        finalScore = Percentage(totalPoints).value,
        properAnswers = answers.toSet()
    )
}
