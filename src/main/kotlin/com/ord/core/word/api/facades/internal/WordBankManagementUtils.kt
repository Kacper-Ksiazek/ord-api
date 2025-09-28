package com.ord.core.word.api.facades.internal

import com.ord.exceptions.REST.BadRequestException
import com.ord.features.bank.api.requests.dto.CreateBankRequest
import com.ord.features.bank.model.BankEntity
import com.ord.features.bank.service.BankService
import com.ord.shared.utils.data_classes.NonRequired
import org.springframework.dao.DataIntegrityViolationException
import reactor.core.publisher.Mono
import java.util.*

internal fun getBankFromRequest(
    bankService: BankService,
    bankId: UUID?,
    bankToCreate: CreateBankRequest?,
    userId: UUID,
): Mono<BankEntity> {
    if (bankToCreate != null && bankId != null) {
        return Mono.error(BadRequestException("You cannot create a new bank and use an existing bank at the same time"))
    }

    return bankService.findByIdOrCreate(
        bankId = bankId,
        bankToCreate = bankToCreate,
        userId = userId
    )
        .cast(BankEntity::class.java)
        .onErrorMap(DataIntegrityViolationException::class.java) {
            BadRequestException("The bank with name ${bankToCreate!!.name} already exists for this user")
        }
}

internal fun getBankFromRequestOrNull(
    bankService: BankService,
    bankId: UUID?,
    bankToCreate: CreateBankRequest?,
    userId: UUID,
): Mono<NonRequired<BankEntity>> {
    return Mono.defer {
        if (bankToCreate != null && bankId != null) {
            Mono.error(BadRequestException("You cannot create a new bank and use an existing bank at the same time"))
        } else if (bankToCreate == null && bankId == null) {
            Mono.just(NonRequired(null))
        } else bankService
            .findByIdOrCreate(
                bankId = bankId,
                bankToCreate = bankToCreate,
                userId = userId
            )
            .map { NonRequired(it) }
            .onErrorMap(DataIntegrityViolationException::class.java) {
                BadRequestException("The bank with name ${bankToCreate!!.name} already exists for this user")
            }
    }
}
