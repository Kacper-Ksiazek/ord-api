package com.backend.ord.seeders.factories

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.bank.api.requests.dto.CreateBankRequest
import com.backend.ord.features.bank.model.BankEntity
import com.backend.ord.features.bank_group.model.BankGroupEntity
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
        bankGroup: BankGroupEntity? = null
    ): BankEntity {
        return BankEntity(
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