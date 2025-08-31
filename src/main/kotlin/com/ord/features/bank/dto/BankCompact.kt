package com.ord.features.bank.dto

import com.ord.features.bank_group.dto.BankGroupCompact
import java.util.*

data class BankCompact(
    val name: String,

    val bankGroup: BankGroupCompact? = null
) {
    companion object {
        val fields = setOf(
            "name",
        )
    }
}