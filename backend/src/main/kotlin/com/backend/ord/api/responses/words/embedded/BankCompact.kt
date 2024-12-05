package com.backend.ord.api.responses.words.embedded

import java.util.UUID

data class BankCompact(
    val id: UUID,
    val name: String,
    val description: String,

    val bankGroup: BankGroupCompact? = null
)