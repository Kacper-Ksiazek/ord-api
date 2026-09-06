package com.ord.features.bank.api.responses

import com.ord.features.bank_group.dto.BankGroupCompact
import java.util.*

data class BankListItem(
    val id: UUID,
    val name: String,
    val bankGroup: BankGroupCompact? = null,
)
