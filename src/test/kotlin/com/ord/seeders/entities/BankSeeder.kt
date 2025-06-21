package com.ord.seeders.entities

import com.ord.core.user.model.UserDTO
import com.ord.core.user.model.UserEntity
import com.ord.core.user.model.UserMapper
import com.ord.features.bank.model.BankEntity
import com.ord.features.bank.repository.BankRepository
import com.ord.features.bank_group.model.BankGroupEntity
import com.ord.seeders.entities.bases.SeederInterface
import com.ord.seeders.factories.BankFactory
import org.springframework.stereotype.Component

@Component
class BankSeeder(
    private val bankMockFactory: BankFactory,
    private val bankRepository: BankRepository,
    private val userMapper: UserMapper
) : SeederInterface<BankEntity> {
    override fun seedOneEntity(data: BankEntity?): BankEntity {
        return bankRepository.save(data ?: bankMockFactory.mockEntity())
    }

    fun seedOneEntityForUser(
        user: UserEntity,
        bankGroup: BankGroupEntity? = null
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
        bankGroup: BankGroupEntity? = null
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