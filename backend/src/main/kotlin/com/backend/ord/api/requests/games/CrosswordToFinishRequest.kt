package com.backend.ord.api.requests.games

import com.backend.ord.api.requests.games.utils.WordUserAnswer
import jakarta.validation.Valid
import java.util.*


data class CrosswordUserAnswers(
    val answer: String,

    @field:Valid
    val questionsAnswers: Set<WordUserAnswer>
)


data class CrosswordToFinishRequest(
    val gameId: UUID,

    val duration: String,

    @field:Valid
    val userAnswers: CrosswordUserAnswers
)

data class UnsafeFinishCrosswordGameRequestData(
    val gameId: UUID? = null,
    val duration: String? = null,
    val userAnswers: CrosswordUserAnswers? = null
)
