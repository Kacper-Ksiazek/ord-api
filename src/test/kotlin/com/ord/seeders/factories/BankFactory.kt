package com.ord.seeders.factories

import com.ord.core.user.model.UserEntity
import com.ord.features.bank.api.requests.dto.CreateBankRequest
import com.ord.features.bank.model.BankEntity
import com.ord.features.bank_group.model.BankGroupEntity
import com.ord.seeders.entities.UserSeeder
import com.ord.seeders.factories.bases.FactoryBase
import org.springframework.stereotype.Component
import java.util.*

@Component
class BankFactory(
    private val userSeeder: UserSeeder,
) : FactoryBase() {
    fun mockEntity(
        name: String = faker.name().fullName(),
        description: String = faker.lorem().sentence(),
        user: UserEntity = userSeeder.seedOneEntity(),
        bankGroup: BankGroupEntity? = null
    ): BankEntity {
        return BankEntity(
            name = name,
            description = description,
            userId = user.id!!,
            groupId = bankGroup?.id,
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