package com.backend.ord.seeders.entities

import com.backend.ord.domain.persistance.dto.UserDTO
import com.backend.ord.domain.persistance.entities.Bank
import com.backend.ord.domain.persistance.entities.BankGroup
import com.backend.ord.domain.persistance.entities.User
import com.backend.ord.domain.persistance.mappers.UserMapper
import com.backend.ord.repositories.BankRepository
import com.backend.ord.seeders.factories.BankMockFactory
import org.springframework.stereotype.Component

@Component
class BankSeeder(
    private val bankMockFactory: BankMockFactory,
    private val bankRepository: BankRepository,
    private val userMapper: UserMapper
) : SeederInterface<Bank> {
    override fun seedOneEntity(data: Bank?): Bank {
        return bankRepository.save(data ?: bankMockFactory.mockEntity())
    }

    fun seedOneEntityForUser(
        user: User,
        bankGroup: BankGroup? = null
    ): Bank {
        return bankRepository.save(
            bankMockFactory.mockEntity(
                user = user,
                bankGroup = bankGroup
            )
        )
    }

    fun seedOneEntityForUser(
        user: UserDTO,
        bankGroup: BankGroup? = null
    ): Bank {
        return seedOneEntityForUser(
            user = userMapper.toEntity(user),
            bankGroup = bankGroup
        )
    }

    override fun deleteAll() {
        bankRepository.deleteAll()
    }
}