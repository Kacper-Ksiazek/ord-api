package com.backend.ord.seeders.entities

import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.domain.persistence.entities.BankGroup
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

    fun seedOneEntityForUser(user: UserEntity): BankGroup {
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