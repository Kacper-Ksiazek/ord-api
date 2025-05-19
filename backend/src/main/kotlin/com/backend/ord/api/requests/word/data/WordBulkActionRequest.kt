package com.backend.ord.api.requests.word.data

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

data class WordBulkActionRequest(
    @field:NotNull(message = "Ids cannot be null")
    @field:Size(min = 1, message = "Ids must contain at least one element")
    val ids: List<UUID>
)
