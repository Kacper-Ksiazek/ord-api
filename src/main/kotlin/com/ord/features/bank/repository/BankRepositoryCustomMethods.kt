package com.ord.features.bank.repository

import com.ord.features.bank.api.responses.BankListItem
import reactor.core.publisher.Flux
import java.util.UUID

interface BankRepositoryCustomMethods {
    fun findAllListItemsByUserId(userId: UUID): Flux<BankListItem>
}
