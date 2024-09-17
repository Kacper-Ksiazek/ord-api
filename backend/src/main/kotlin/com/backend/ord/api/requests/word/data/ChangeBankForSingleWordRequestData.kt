package com.backend.ord.api.requests.word.data

import com.backend.ord.api.requests.bank.CreateBankRequest
import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.api.requests.word.ChangeBankForSingleWordRequest
import java.util.UUID

data class ChangeBankForSingleWordRequestData(
    override val bankId: UUID? = null,
    override val bankToCreate: CreateBankRequestData? = null
) : ChangeBankForSingleWordRequest