package com.backend.ord.core.word.api.requests.dto

import com.backend.ord.features.bank.api.requests.dto.CreateBankRequest
import jakarta.validation.Valid
import java.util.*

data class ChangeBankForSingleWordRequest(
    val bankId: UUID? = null,

    @field:Valid
    val bankToCreate: CreateBankRequest? = null
)