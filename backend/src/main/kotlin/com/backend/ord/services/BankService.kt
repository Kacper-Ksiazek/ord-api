package com.backend.ord.services

import com.backend.ord.api.requests.bank.CreateBankRequest
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.domain.persistence.entities.Bank
import com.backend.ord.shared.services.UserResourceService
import org.springframework.stereotype.Service
import java.util.*

@Service
interface BankService : UserResourceService<Bank> {
    fun findByIdOrCreate(
        bankId: UUID?,
        bankToCreate: CreateBankRequest?,
        user: UserEntity
    ): Bank?
}