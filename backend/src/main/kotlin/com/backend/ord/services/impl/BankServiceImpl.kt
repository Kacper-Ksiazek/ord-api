package com.backend.ord.services.impl

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.domain.persistence.entities.Bank
import com.backend.ord.exceptions.REST.BadRequestException
import com.backend.ord.exceptions.REST.NotFoundException
import com.backend.ord.features.bank.api.requests.dto.CreateBankRequest
import com.backend.ord.services.BankService
import com.backend.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class BankServiceImpl(
    override val repository: UserResourceRepository<Bank>
) : BankService {
    override fun findByIdOrCreate(
        bankId: UUID?,
        bankToCreate: CreateBankRequest?,
        user: UserEntity
    ): Bank? {
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
                Bank(
                    name = bankToCreate.name,
                    description = bankToCreate.description,
                    user = user
                )
            )
        }

        return null
    }
}