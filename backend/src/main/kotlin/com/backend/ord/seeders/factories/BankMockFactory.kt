package com.backend.ord.seeders.factories

import com.backend.ord.api.requests.bank.CreateBankRequest
import com.backend.ord.domain.entities.Bank
import com.backend.ord.domain.entities.User
import com.backend.ord.seeders.entities.UserSeeder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class BankMockFactory(
    private val userSeeder: UserSeeder,
) : AbstractFactory() {
    fun mockEntity(
        name: String = faker.name().fullName(),
        description: String = faker.lorem().sentence(),
        user: User = userSeeder.seedOneEntity()
    ): Bank {
        return Bank(
            name = name,
            description = description,
            user = user
        )
    }

    fun mockCreateRequest(
        name: String = faker.name().fullName(),
        description: String = faker.lorem().sentence(),
        groupId: UUID? = null
    ): CreateBankRequest {
        return CreateBankRequest(
            name = name,
            description = description,
            groupId = groupId
        )
    }
}