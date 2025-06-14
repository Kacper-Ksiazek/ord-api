package com.backend.ord.seeders.factories

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.bank_group.model.BankGroupEntity
import com.backend.ord.seeders.entities.UserSeeder
import org.springframework.stereotype.Component

@Component
class BankGroupFactory(
    private val userSeeder: UserSeeder,
) : AbstractFactory() {
    fun mockEntity(
        name: String = faker.name().fullName(),
        color: String = faker.color().hex(),
        user: UserEntity = userSeeder.seedOneEntity()
    ): BankGroupEntity {
        return BankGroupEntity(
            name = name,
            color = color,
            user = user
        )
    }
}