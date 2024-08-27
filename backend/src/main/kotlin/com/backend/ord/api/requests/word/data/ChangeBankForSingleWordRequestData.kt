package com.backend.ord.api.requests.word.data

import com.backend.ord.api.requests.word.ChangeBankForSingleWordRequest
import java.util.UUID

data class ChangeBankForSingleWordRequestData(
    override val bankId: UUID?
) : ChangeBankForSingleWordRequest