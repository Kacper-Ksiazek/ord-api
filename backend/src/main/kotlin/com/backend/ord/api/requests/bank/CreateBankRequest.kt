package com.backend.ord.api.requests.bank

import java.util.*

interface CreateBankRequest {
    val name: String
    val description: String

    val groupId: UUID?
}