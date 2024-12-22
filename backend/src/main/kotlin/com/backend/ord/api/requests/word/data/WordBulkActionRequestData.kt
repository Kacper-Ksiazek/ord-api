package com.backend.ord.api.requests.word.data

import com.backend.ord.api.requests.word.WordsBulkActionRequest
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class WordBulkActionRequestData(
    @field:NotNull(message = "Ids cannot be null")
    @field:Size(min = 1, message = "Ids must contain at least one element")
    override val ids: List<UUID>
) : WordsBulkActionRequest
