package com.ord.seeders.factories

import com.ord.core.user.model.UserEntity
import com.ord.features.bank_group.model.BankGroupEntity
import com.ord.seeders.entities.UserSeeder
import com.ord.seeders.factories.bases.FactoryBase
import org.springframework.stereotype.Component

@Component
class BankGroupFactory(
    private val userSeeder: UserSeeder,
) : FactoryBase() {
    fun mockEntity(
        name: String = faker.name().fullName(),
        color: String = faker.color().hex(),
        user: UserEntity = userSeeder.seedOneEntity()
    ): BankGroupEntity {
        return BankGroupEntity(
            name = name,
            color = color,
            userId = user.id!!,
        )
    }
}