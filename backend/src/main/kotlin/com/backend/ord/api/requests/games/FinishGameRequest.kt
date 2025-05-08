package com.backend.ord.api.requests.games

import com.backend.ord.api.requests.games.utils.WordUserAnswer
import jakarta.validation.Valid
import java.util.*

// ---
// General purpose classes
// ---

data class FinishGameRequestData<TAnswers>(
    val gameId: UUID,

    val duration: String,

    @field:Valid
    val answers: TAnswers
)

data class UnsafeFinishGameRequestData<TAnswers>(
    val gameId: UUID? = null,
    val duration: String? = null,
    val answers: TAnswers? = null
)

// ---
// Specific for given DTO classes
// ---

data class CrosswordUserAnswers(
    val finalWord: String,

    @field:Valid
    val questions: Set<WordUserAnswer>
)

typealias CrosswordToFinishRequest = FinishGameRequestData<CrosswordUserAnswers>

typealias WordsTypingToFinishRequest = FinishGameRequestData<Set<WordUserAnswer>>

