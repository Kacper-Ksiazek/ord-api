package com.backend.ord.services

import com.backend.ord.api.requests.bank.CreateBankRequest
import com.backend.ord.domain.entities.Bank
import com.backend.ord.domain.entities.User
import com.backend.ord.services.bases.UserResourceService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
interface BankService : UserResourceService<Bank> {
    fun findByIdOrCreate(
        bankId: UUID?,
        bankToCreate: CreateBankRequest?,
        user: User
    ): Bank?
}