package com.backend.ord.seeders.mocks.banks.json_data_models

import java.util.UUID

data class BankInJSON(
    val name: String,
    val description: String,

    /**
     * This is the unique name of a group of banks, which will be then used
     * to map the relationship between the bank and the group
     * */
    val groupName: String
)