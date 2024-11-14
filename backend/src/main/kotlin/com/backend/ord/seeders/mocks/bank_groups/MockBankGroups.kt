package com.backend.ord.seeders.mocks.bank_groups

import com.backend.ord.domain.entities.BankGroup
import com.backend.ord.domain.entities.User
import com.backend.ord.repositories.BankGroupRepository
import com.backend.ord.seeders.mocks._bases.MocksFromJsonFileHandler
import com.backend.ord.seeders.mocks.bank_groups.json_data_models.BankGroupInJSON
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Component

@Component
class MockBankGroups(
    override val repository: BankGroupRepository
) : MocksFromJsonFileHandler<
        BankGroup,
        List<BankGroupInJSON>,
        BankGroupInJSON
        > {
    override val pathToJSONFile: String = "/banks/bank_groups.json"

    override fun typeReference(): TypeReference<List<BankGroupInJSON>> {
        return object : TypeReference<List<BankGroupInJSON>>() {}
    }

    override fun convertToEntity(
        jsonData: BankGroupInJSON,
        user: User
    ): BankGroup {
        return BankGroup(
            id = jsonData.id,
            name = jsonData.name,
            color = jsonData.color,

            user = user
        )
    }
}