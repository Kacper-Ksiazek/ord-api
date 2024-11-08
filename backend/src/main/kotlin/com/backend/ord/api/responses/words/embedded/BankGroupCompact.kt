package com.backend.ord.api.responses.words.embedded

import java.util.UUID

data class BankGroupCompact(
    val id: UUID,

    val name: String,
    val color: String
)
