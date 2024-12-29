package com.backend.ord.services

import com.backend.ord.api.requests.bank.CreateBankRequest
import com.backend.ord.domain.persistance.entities.Bank
import com.backend.ord.domain.persistance.entities.User
import com.backend.ord.services.bases.UserResourceService
import org.springframework.stereotype.Service
import java.util.*

@Service
interface BankService : UserResourceService<Bank> {
    fun findByIdOrCreate(
        bankId: UUID?,
        bankToCreate: CreateBankRequest?,
        user: User
    ): Bank?
}