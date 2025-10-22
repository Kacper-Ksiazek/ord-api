package com.ord.core.word.api.crud.requests.dto

import com.ord.features.bank.api.requests.dto.CreateBankRequest
import jakarta.validation.Valid
import java.util.*

data class ChangeBankForMultipleWordsRequest(
    val bankId: UUID? = null,
    val wordIds: List<UUID>,

    @field:Valid
    val bankToCreate: CreateBankRequest? = null
)