package com.backend.ord.seeders.entities

import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.features.bank_group.model.BankGroupEntity
import com.backend.ord.features.bank_group.repository.BankGroupRepository
import com.backend.ord.seeders.factories.BankGroupFactory
import org.springframework.stereotype.Component

@Component
class BankGroupSeeder(
    private val bankGroupRepository: BankGroupRepository,
    private val bankGroupMockFactory: BankGroupFactory,
    private val userMapper: UserMapper
) : SeederInterface<BankGroupEntity> {
    override fun seedOneEntity(data: BankGroupEntity?): BankGroupEntity {
        return bankGroupRepository.save(data ?: bankGroupMockFactory.mockEntity())
    }

    fun seedOneEntityForUser(user: UserEntity): BankGroupEntity {
        return bankGroupRepository.save(bankGroupMockFactory.mockEntity(user = user))
    }

    fun seedOneEntityForUser(user: UserDTO): BankGroupEntity {
        return seedOneEntityForUser(
            user = userMapper.toEntity(user)
        )
    }

    override fun deleteAll() {
        bankGroupRepository.deleteAll()
    }
}