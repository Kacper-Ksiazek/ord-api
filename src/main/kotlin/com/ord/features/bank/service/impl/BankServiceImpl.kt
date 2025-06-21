package com.ord.features.bank.service.impl

import com.ord.core.user.model.UserEntity
import com.ord.exceptions.REST.BadRequestException
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.bank.api.requests.dto.CreateBankRequest
import com.ord.features.bank.model.BankEntity
import com.ord.features.bank.service.BankService
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class BankServiceImpl(
    override val repository: UserResourceRepository<BankEntity>
) : BankService {
    override fun findByIdOrCreate(
        bankId: UUID?,
        bankToCreate: CreateBankRequest?,
        user: UserEntity
    ): BankEntity? {
        if (bankToCreate != null && bankId != null) {
            throw BadRequestException("You cannot create a new bank and use an existing bank at the same time")
        }

        if (bankId != null) {
            return repository.findOneForUser(
                id = bankId,
                userId = user.id
            ) ?: throw NotFoundException("Bank with ID $bankId not found")
        } else if (bankToCreate != null) {
            return repository.save(
                BankEntity(
                    name = bankToCreate.name,
                    description = bankToCreate.description,
                    user = user
                )
            )
        }

        return null
    }
}