package com.backend.ord.seeders.entities

import com.backend.ord.core.user.model.UserDTO
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.core.user.model.UserMapper
import com.backend.ord.domain.persistence.entities.BankGroup
import com.backend.ord.features.bank.model.BankEntity
import com.backend.ord.features.bank.repository.BankRepository
import com.backend.ord.seeders.factories.BankMockFactory
import org.springframework.stereotype.Component

@Component
class BankSeeder(
    private val bankMockFactory: BankMockFactory,
    private val bankRepository: BankRepository,
    private val userMapper: UserMapper
) : SeederInterface<BankEntity> {
    override fun seedOneEntity(data: BankEntity?): BankEntity {
        return bankRepository.save(data ?: bankMockFactory.mockEntity())
    }

    fun seedOneEntityForUser(
        user: UserEntity,
        bankGroup: BankGroup? = null
    ): BankEntity {
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
    ): BankEntity {
        return seedOneEntityForUser(
            user = userMapper.toEntity(user),
            bankGroup = bankGroup
        )
    }

    override fun deleteAll() {
        bankRepository.deleteAll()
    }
}