package com.ord.features.bank.service.impl

import com.ord.core.user.model.UserEntity
import com.ord.exceptions.REST.BadRequestException
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.bank.api.requests.dto.CreateBankRequest
import com.ord.features.bank.model.BankEntity
import com.ord.features.bank.service.BankService
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class BankServiceImpl(
    override val repository: UserResourceRepository<BankEntity>
) : BankService {
    override fun findByIdOrCreate(
        bankId: UUID?,
        bankToCreate: CreateBankRequest?,
        user: UserEntity
    ): Mono<BankEntity?> {
        if (bankToCreate != null && bankId != null) {
            return Mono.error(BadRequestException("You cannot create a new bank and use an existing bank at the same time"))
        }

        return when {
            bankId != null -> {
                repository.findOneForUser(bankId, user.id)
                    .cast(BankEntity::class.java)
                    .switchIfEmpty(Mono.error(NotFoundException("Bank with ID $bankId not found")))
            }
            bankToCreate != null -> {
                repository.save(
                    BankEntity(
                        name = bankToCreate.name,
                        description = bankToCreate.description,
                        userId = user.id,
                        user = user
                    )
                ).cast(BankEntity::class.java)
            }
            else -> Mono.error(BadRequestException("Either bankId or bankToCreate must be provided"))
        }
    }
}