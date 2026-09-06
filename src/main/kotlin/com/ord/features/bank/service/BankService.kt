package com.ord.features.bank.service

import com.ord.features.bank.api.requests.dto.CreateBankRequest
import com.ord.features.bank.api.responses.BankListItem
import com.ord.features.bank.model.BankEntity
import com.ord.shared.services.UserResourceService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
interface BankService : UserResourceService<BankEntity> {
    fun findAllListItems(userId: UUID): Flux<BankListItem>

    fun findByIdOrCreate(
        bankId: UUID?,
        bankToCreate: CreateBankRequest?,
        userId: UUID
    ): Mono<BankEntity>
}