package com.backend.ord.api.requests.word.data

import com.backend.ord.api.requests.bank.data.CreateBankRequest
import jakarta.validation.Valid
import java.util.*

data class ChangeBankForSingleWordRequest(
    val bankId: UUID? = null,

    @field:Valid
    val bankToCreate: CreateBankRequest? = null
)