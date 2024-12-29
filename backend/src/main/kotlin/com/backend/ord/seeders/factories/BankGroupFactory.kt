package com.backend.ord.seeders.factories

import com.backend.ord.domain.persistence.entities.BankGroup
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.seeders.entities.UserSeeder
import org.springframework.stereotype.Component

@Component
class BankGroupFactory(
    private val userSeeder: UserSeeder,
) : AbstractFactory() {
    fun mockEntity(
        name: String = faker.name().fullName(),
        color: String = faker.color().hex(),
        user: User = userSeeder.seedOneEntity()
    ): BankGroup {
        return BankGroup(
            name = name,
            color = color,
            user = user
        )
    }
}