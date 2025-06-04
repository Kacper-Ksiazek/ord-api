package com.backend.ord.core.word.api.facades.internal

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.features.bank.api.requests.dto.CreateBankRequest
import com.backend.ord.features.bank.model.Bank
import com.backend.ord.features.bank.service.BankService
import org.springframework.dao.DataIntegrityViolationException
import java.util.*

internal fun getBankFromRequest(
    bankService: BankService,
    bankId: UUID?,
    bankToCreate: CreateBankRequest?,
    user: UserEntity
): Bank {
    if (bankToCreate == null && bankId == null) {
        throw BadRequestException("Either bankToCreate or bankId has to be specifed")
    }

    if (bankToCreate != null && bankId != null) {
        throw BadRequestException("You cannot create a new bank and use an existing bank at the same time")
    }

    return try {
        bankService.findByIdOrCreate(
            bankId = bankId,
            bankToCreate = bankToCreate,
            user = user
        )!!
    } catch (_: DataIntegrityViolationException) {
        throw BadRequestException("The bank with name ${bankToCreate!!.name} already exists for this user")
    }
}

internal fun getBankFromRequestOrNull(
    bankService: BankService,
    bankId: UUID?,
    bankToCreate: CreateBankRequest?,
    user: UserEntity
): Bank? {
    if (bankToCreate != null && bankId != null) {
        throw BadRequestException("You cannot create a new bank and use an existing bank at the same time")
    }

    return try {
        bankService.findByIdOrCreate(
            bankId = bankId,
            bankToCreate = bankToCreate,
            user = user
        )
    } catch (e: DataIntegrityViolationException) {
        throw BadRequestException("The bank with name ${bankToCreate!!.name} already exists for this user")
    }
}
