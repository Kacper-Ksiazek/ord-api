package com.backend.ord.core.word.api.requests.dto

import com.backend.ord.features.bank.api.requests.dto.CreateBankRequest
import jakarta.validation.Valid
import java.util.*

data class ChangeBankForMultipleWordsRequest(
    val bankId: UUID? = null,
    val wordIds: List<UUID>,

    @field:Valid
    val bankToCreate: CreateBankRequest? = null
)