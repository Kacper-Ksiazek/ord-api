package com.backend.ord.features.bank.dto

import com.backend.ord.features.bank_group.dto.BankGroupCompact
import java.util.*

data class BankCompact(
    val id: UUID,
    val name: String,
    val description: String,

    val bankGroup: BankGroupCompact? = null
)