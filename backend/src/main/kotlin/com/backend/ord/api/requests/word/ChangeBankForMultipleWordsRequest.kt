package com.backend.ord.api.requests.word

import java.util.UUID

data class ChangeBankForMultipleWordsRequest(
    val wordIds: List<UUID>,
    val bankId: UUID?
)