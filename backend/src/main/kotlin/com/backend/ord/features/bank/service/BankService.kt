package com.backend.ord.features.bank.service

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.bank.api.requests.dto.CreateBankRequest
import com.backend.ord.features.bank.model.BankEntity
import com.backend.ord.shared.services.UserResourceService
import org.springframework.stereotype.Service
import java.util.*

@Service
interface BankService : UserResourceService<BankEntity> {
    fun findByIdOrCreate(
        bankId: UUID?,
        bankToCreate: CreateBankRequest?,
        user: UserEntity
    ): BankEntity?
}