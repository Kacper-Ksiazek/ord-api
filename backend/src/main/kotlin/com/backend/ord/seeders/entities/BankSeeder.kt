package com.backend.ord.seeders.entities

import com.backend.ord.domain.entities.Bank
import com.backend.ord.repositories.BankRepository
import com.backend.ord.seeders.factories.BankMockFactory

class BankSeeder(
    private val bankMockFactory: BankMockFactory,
    private val bankRepository: BankRepository
) : SeederInterface<Bank> {
    override fun seedOneEntity(data: Bank?): Bank {
        return bankRepository.save(data ?: bankMockFactory.mockEntity())
    }

    override fun deleteAll() {
        bankRepository.deleteAll()
    }
}