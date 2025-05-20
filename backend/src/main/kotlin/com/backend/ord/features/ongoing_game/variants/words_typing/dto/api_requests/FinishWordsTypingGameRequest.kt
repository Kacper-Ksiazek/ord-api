package com.backend.ord.features.ongoing_game.variants.words_typing.dto.api_requests

import com.backend.ord.features.ongoing_game.variants.shared.dto.api_requests.bases.FinishGameRequestDataBase
import com.backend.ord.features.ongoing_game.variants.shared.dto.api_requests.helpers.WordUserAnswer
import jakarta.validation.Valid
import java.util.*

data class CrosswordUserAnswers(
    val finalWord: String,

    @field:Valid
    val questions: Set<WordUserAnswer>
)


data class FinishCrosswordGameRequest(
    override val gameId: UUID,
    override val duration: String,

    @field:Valid override val answers: CrosswordUserAnswers
) : FinishGameRequestDataBase<CrosswordUserAnswers>


