package com.ord.features.bank.service

import com.ord.core.user.model.UserEntity
import com.ord.features.bank.api.requests.dto.CreateBankRequest
import com.ord.features.bank.model.BankEntity
import com.ord.shared.services.UserResourceService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
interface BankService : UserResourceService<BankEntity> {
    fun findByIdOrCreate(
        bankId: UUID?,
        bankToCreate: CreateBankRequest?,
        userId: UUID
    ): Mono<BankEntity?>
}