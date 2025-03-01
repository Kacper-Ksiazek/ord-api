package com.backend.ord.api.requests.games.data

import jakarta.validation.constraints.Size
import java.util.*

data class WordUserAnswer(
    val id: UUID,

    @field:Size(min = 1, max = 255, message = "Word must be between 1 and 255 characters")
    val word: String
)
