package com.backend.ord.features.ongoing_game.variants.words_typing.dto.api_responses

import com.backend.ord.features.ongoing_game.variants.shared.dto.api_responses.FinishedGameResponseBase
import com.backend.ord.features.ongoing_game.variants.shared.dto.api_responses.helpers.IdentifiableProperAnswer
import com.backend.ord.utils.data_classes.Percentage

class FinishedWordsTypingGameResponse(
    finalScore: Double,

    val properAnswers: Set<IdentifiableProperAnswer>,
) : FinishedGameResponseBase(finalScore) {

    constructor(
        totalPoints: Int,
        properAnswers: Set<IdentifiableProperAnswer>
    ) : this(
        finalScore = Percentage(totalPoints).value,
        properAnswers = properAnswers
    )
}