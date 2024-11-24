package com.backend.ord.seeders.entities

import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.domain.entities.BankGroup
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.mappers.UserMapper
import com.backend.ord.repositories.BankGroupRepository
import com.backend.ord.seeders.factories.BankGroupFactory
import org.springframework.stereotype.Component

@Component
class BankGroupSeeder(
    private val bankGroupRepository: BankGroupRepository,
    private val bankGroupMockFactory: BankGroupFactory,
    private val userMapper: UserMapper
) : SeederInterface<BankGroup> {
    override fun seedOneEntity(data: BankGroup?): BankGroup {
        return bankGroupRepository.save(data ?: bankGroupMockFactory.mockEntity())
    }

    fun seedOneEntityForUser(user: User): BankGroup {
        return bankGroupRepository.save(bankGroupMockFactory.mockEntity(user = user))
    }

    fun seedOneEntityForUser(user: UserDTO): BankGroup {
        return seedOneEntityForUser(
            user = userMapper.toEntity(user)
        )
    }

    override fun deleteAll() {
        bankGroupRepository.deleteAll()
    }
}