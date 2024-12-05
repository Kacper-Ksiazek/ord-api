package com.backend.ord.api.requests.word

import com.backend.ord.api.requests.bank.CreateBankRequest
import java.util.*

interface ChangeBankForMultipleWordsRequest {
    val wordIds: List<UUID>

    val bankId: UUID?
    val bankToCreate: CreateBankRequest?
}