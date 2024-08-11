package com.backend.ord.seeders.entities

import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.domain.entities.Bank
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.mappers.UserMapper
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

    fun seedOneEntityForUser(user: User): Bank {
        return bankRepository.save(bankMockFactory.mockEntity(user = user))
    }

    fun seedOneEntityForUser(user: UserDTO): Bank {
        return seedOneEntityForUser(userMapper.toEntity(user))
    }

    override fun deleteAll() {
        bankRepository.deleteAll()
    }
}