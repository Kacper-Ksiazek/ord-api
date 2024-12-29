package com.backend.ord.seeders.factories

import com.backend.ord.api.requests.bank.data.CreateBankRequestData
import com.backend.ord.domain.persistance.entities.Bank
import com.backend.ord.domain.persistance.entities.BankGroup
import com.backend.ord.domain.persistance.entities.User
import com.backend.ord.seeders.entities.UserSeeder
import org.springframework.stereotype.Component
import java.util.*

@Component
class BankMockFactory(
    private val userSeeder: UserSeeder,
) : AbstractFactory() {
    fun mockEntity(
        name: String = faker.name().fullName(),
        description: String = faker.lorem().sentence(),
        user: User = userSeeder.seedOneEntity(),
        bankGroup: BankGroup? = null
    ): Bank {
        return Bank(
            name = name,
            description = description,
            user = user,
            bankGroup = bankGroup
        )
    }

    fun mockCreateRequestData(
        name: String = faker.name().fullName(),
        description: String = faker.lorem().sentence(),
        groupId: UUID? = null
    ): CreateBankRequestData {
        return CreateBankRequestData(
            name = name,
            description = description,
            groupId = groupId
        )
    }
}