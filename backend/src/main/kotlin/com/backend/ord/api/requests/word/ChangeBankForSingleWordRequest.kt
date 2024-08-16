package com.backend.ord.api.requests.word

import java.util.UUID

data class ChangeBankForSingleWordRequest(
    val bankId: UUID?
)