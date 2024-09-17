package com.backend.ord.api.requests.word

import com.backend.ord.api.requests.bank.CreateBankRequest
import java.util.*

interface ChangeBankForSingleWordRequest {
    val bankId: UUID?
    val bankToCreate: CreateBankRequest?
}