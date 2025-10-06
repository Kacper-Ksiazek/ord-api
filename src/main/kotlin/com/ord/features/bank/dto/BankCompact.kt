package com.ord.features.bank.dto

import com.ord.features.bank_group.dto.BankGroupCompact
import io.r2dbc.spi.Readable

data class BankCompact(
    val name: String,

    val bankGroup: BankGroupCompact? = null
) {
    companion object {
        val fields = setOf(
            "name",
        )

        fun construct(row: Readable): BankCompact? {
            val hasBank: Boolean = row.get("bank_name", String::class.java) != null
            val hasBankGroup: Boolean = hasBank && row.get("bank_group_name", String::class.java) != null

            return if (hasBank) {
                BankCompact(
                    name = row.get("bank_name", String::class.java)!!,
                    bankGroup = if (hasBankGroup) {
                        BankGroupCompact(
                            name = row.get("bank_group_name", String::class.java)!!,
                            color = row.get("bank_group_color", String::class.java)!!
                        )
                    } else null
                )
            } else null
        }
    }
}