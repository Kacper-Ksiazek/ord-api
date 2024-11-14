package com.backend.ord.seeders.mocks.banks.json_data_models

import java.util.UUID

data class BankInJSON(
    val name: String,
    val description: String,
    val groupId: UUID
)