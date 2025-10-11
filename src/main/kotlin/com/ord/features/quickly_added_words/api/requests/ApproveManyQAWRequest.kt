package com.ord.features.quickly_added_words.api.requests

import java.util.UUID

data class ApproveManyQAWRequest(
    val ids: List<UUID>,
)
