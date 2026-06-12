package com.ord.features.quickly_added_words.api.requests

import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class ApproveManyQAWRequest(
    @field:NotEmpty(message = "IDs list cannot be empty")
    val ids: List<UUID>,
)
