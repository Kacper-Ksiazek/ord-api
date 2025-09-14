package com.ord.core.word.api.facades.internal

import com.ord.core.user.model.UserEntity
import com.ord.exceptions.REST.BadRequestException
import com.ord.features.bank.api.requests.dto.CreateBankRequest
import com.ord.features.bank.model.BankEntity
import com.ord.features.bank.service.BankService
import org.springframework.dao.DataIntegrityViolationException
import reactor.core.publisher.Mono
import java.util.*

internal fun getBankFromRequest(
    bankService: BankService,
    bankId: UUID?,
    bankToCreate: CreateBankRequest?,
    user: UserEntity
): Mono<BankEntity> {
    if (bankToCreate == null && bankId == null) {
        return Mono.error(BadRequestException("Either bankToCreate or bankId has to be specifed"))
    }

    if (bankToCreate != null && bankId != null) {
        return Mono.error(BadRequestException("You cannot create a new bank and use an existing bank at the same time"))
    }

    return bankService.findByIdOrCreate(
        bankId = bankId,
        bankToCreate = bankToCreate,
        user = user
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
    user: UserEntity
): Mono<BankEntity?> {
    if (bankToCreate != null && bankId != null) {
        return Mono.error(BadRequestException("You cannot create a new bank and use an existing bank at the same time"))
    }

    return bankService.findByIdOrCreate(
        bankId = bankId,
        bankToCreate = bankToCreate,
        user = user
    )
    .onErrorMap(DataIntegrityViolationException::class.java) { 
        BadRequestException("The bank with name ${bankToCreate!!.name} already exists for this user")
    }
}
