package com.backend.ord.api.requests.games

import com.backend.ord.api.requests.games.utils.WordUserAnswer
import jakarta.validation.Valid
import java.util.*

data class WordsTypingToFinishRequest(
    val gameId: UUID,

    @field:Valid
    val answers: Set<WordUserAnswer>
)