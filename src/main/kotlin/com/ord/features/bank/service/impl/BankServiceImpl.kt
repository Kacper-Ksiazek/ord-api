package com.ord.features.bank.service.impl

import com.ord.exceptions.REST.BadRequestException
import com.ord.exceptions.REST.NotFoundException
import com.ord.features.bank.api.requests.dto.CreateBankRequest
import com.ord.features.bank.model.BankEntity
import com.ord.features.bank.repository.BankRepository
import com.ord.features.bank.service.BankService
import com.ord.shared.repositories.UserResourceRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class BankServiceImpl(
    private val bankRepository: BankRepository
) : BankService {
    override val userRepository: UserResourceRepository<BankEntity> = bankRepository
    override val crudRepository: ReactiveCrudRepository<BankEntity, UUID> = bankRepository

    override fun findByIdOrCreate(
        bankId: UUID?,
        bankToCreate: CreateBankRequest?,
        userId: UUID
    ): Mono<BankEntity?> {
        if (bankToCreate != null && bankId != null) {
            return Mono.error(BadRequestException("You cannot create a new bank and use an existing bank at the same time"))
        }

        return when {
            bankId != null -> {
                bankRepository.findOneForUser(userId, bankId)
                    .switchIfEmpty(Mono.error(NotFoundException("Bank with ID $bankId not found")))
            }

            bankToCreate != null -> {
                bankRepository.save(
                    BankEntity(
                        name = bankToCreate.name,
                        description = bankToCreate.description,
                        userId = userId
                    )
                )
            }

            else -> Mono.error(BadRequestException("Either bankId or bankToCreate must be provided"))
        }
    }
}