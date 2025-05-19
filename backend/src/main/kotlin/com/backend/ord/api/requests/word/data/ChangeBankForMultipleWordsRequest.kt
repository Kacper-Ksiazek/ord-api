package com.backend.ord.api.requests.word.data

import com.backend.ord.api.requests.bank.data.CreateBankRequest
import jakarta.validation.Valid
import java.util.*

data class ChangeBankForMultipleWordsRequest(
    val bankId: UUID? = null,
    val wordIds: List<UUID>,

    @field:Valid
    val bankToCreate: CreateBankRequest? = null
)