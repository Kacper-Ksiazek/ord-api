package com.backend.ord.seeders.factories

import com.backend.ord.api.requests.bank.data.CreateBankRequest
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.domain.persistence.entities.Bank
import com.backend.ord.domain.persistence.entities.BankGroup
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
        user: UserEntity = userSeeder.seedOneEntity(),
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
    ): CreateBankRequest {
        return CreateBankRequest(
            name = name,
            description = description,
            groupId = groupId
        )
    }
}