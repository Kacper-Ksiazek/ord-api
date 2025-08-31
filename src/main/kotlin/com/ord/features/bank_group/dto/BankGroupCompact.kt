package com.ord.features.bank_group.dto

data class BankGroupCompact(
    val name: String,
    val color: String
) {
    companion object {
        val fields = setOf(
            "name",
            "color"
        )
    }
}