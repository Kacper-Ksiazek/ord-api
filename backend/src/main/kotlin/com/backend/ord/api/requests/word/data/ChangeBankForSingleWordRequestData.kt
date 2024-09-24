package com.backend.ord.api.requests.word.data

import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.api.requests.word.ChangeBankForSingleWordRequest
import jakarta.validation.Valid
import java.util.*

data class ChangeBankForSingleWordRequestData(
    override val bankId: UUID? = null,

    @field:Valid
    override val bankToCreate: CreateBankRequestData? = null
) : ChangeBankForSingleWordRequest