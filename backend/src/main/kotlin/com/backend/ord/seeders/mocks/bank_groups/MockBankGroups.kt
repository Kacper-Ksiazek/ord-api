package com.backend.ord.seeders.mocks.bank_groups

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.bank_group.model.BankGroupEntity
import com.backend.ord.features.bank_group.repository.BankGroupRepository
import com.backend.ord.seeders.mocks.bank_groups.json_data_models.BankGroupInJSON
import com.backend.ord.seeders.mocks.bases.MocksFromJsonFileHandler
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Component

@Component
class MockBankGroups(
    override val repository: BankGroupRepository
) : MocksFromJsonFileHandler<
        BankGroupEntity,
        List<BankGroupInJSON>,
        BankGroupInJSON
        > {
    override val pathToJsonFile: String = "mocks/banks/bank_groups.json"

    override val jsonFileContentTypeRef: TypeReference<List<BankGroupInJSON>> =
        object : TypeReference<List<BankGroupInJSON>>() {}

    override fun convertToEntity(
        jsonData: BankGroupInJSON,
        user: UserEntity
    ): BankGroupEntity {
        return BankGroupEntity(
            name = jsonData.name,
            color = jsonData.color,

            user = user
        )
    }
}