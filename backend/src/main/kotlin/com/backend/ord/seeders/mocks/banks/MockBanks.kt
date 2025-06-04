package com.backend.ord.seeders.mocks.banks

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.bank.model.BankEntity
import com.backend.ord.features.bank.repository.BankRepository
import com.backend.ord.features.bank_group.model.BankGroupEntity
import com.backend.ord.seeders.mocks.banks.json_data_models.BankInJSON
import com.backend.ord.seeders.mocks.bases.MocksFromJsonFileHandler
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Component

@Component
class MockBanks(
    override val repository: BankRepository
) : MocksFromJsonFileHandler<
        BankEntity,
        List<BankInJSON>,
        BankInJSON
        > {
    private lateinit var bankGroups: List<BankGroupEntity>

    override val pathToJsonFile: String = "mocks/banks/banks.json"

    override fun convertToEntity(
        jsonData: BankInJSON,
        user: UserEntity
    ): BankEntity {

        return BankEntity(
            name = jsonData.name,
            description = jsonData.description,

            bankGroup = this.bankGroups.find { it.name == jsonData.groupName },

            user = user
        )
    }

    override val jsonFileContentTypeRef: TypeReference<List<BankInJSON>> =
        object : TypeReference<List<BankInJSON>>() {}

    fun seedFromJSONFile(
        user: UserEntity,
        bankGroups: List<BankGroupEntity>
    ): List<BankEntity> {
        this.bankGroups = bankGroups

        return this.seedFromJSONFile(user)
    }
}