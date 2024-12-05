package com.backend.ord.api.requests.word.data

import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.api.requests.word.ChangeBankForMultipleWordsRequest
import jakarta.validation.Valid
import java.util.*

data class ChangeBankForMultipleWordsRequestData(
    override val wordIds: List<UUID>,

    override val bankId: UUID? = null,

    @field:Valid
    override val bankToCreate: CreateBankRequestData? = null
) : ChangeBankForMultipleWordsRequest