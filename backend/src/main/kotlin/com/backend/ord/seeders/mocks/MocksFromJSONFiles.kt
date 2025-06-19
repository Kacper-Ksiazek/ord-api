package com.backend.ord.seeders.mocks

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.bank_group.model.BankGroupEntity
import com.backend.ord.seeders.mocks.bank_groups.MockBankGroups
import com.backend.ord.seeders.mocks.banks.MockBanks
import com.backend.ord.seeders.mocks.words.MockWordsManuals
import com.backend.ord.seeders.mocks.words_gpt_tokens_usage.MockDetailedWordsGPTTokensConsumption
import org.springframework.stereotype.Component

data class MockedEntitySummary(
    /** The name of the entity */
    val name: String,

    /** The amount of created entities */
    val amount: Int
)

// TODO: To archive

@Component
class MocksFromJSONFiles(
    private val mockWordsManuals: MockWordsManuals,
    private val mockDetailedWordsGPTTokensConsumption: MockDetailedWordsGPTTokensConsumption,
    private val mockBankGroups: MockBankGroups,
    private val mockBanks: MockBanks
) {
    fun run(user: UserEntity): List<MockedEntitySummary> {
        val result = mutableListOf<MockedEntitySummary>()

        val bankGroups: List<BankGroupEntity> = mockBankGroups.seedFromJSONFile(user)

        val banks = mockBanks.seedFromJSONFile(
            user = user,
            bankGroups = bankGroups
        )

        val words = mockWordsManuals.seedFromJSONFile(
            user = user,
            banks = banks
        )

        result.add(
            MockedEntitySummary(
                name = "Bank groups",
                amount = bankGroups.size
            )
        )

        result.add(
            MockedEntitySummary(
                name = "Banks",
                amount = banks.size
            )
        )

        result.add(
            MockedEntitySummary(
                name = "Words",
                amount = words.size
            )
        )

        result.add(
            MockedEntitySummary(
                name = "GPT Tokens Usage on Words",
                amount = mockDetailedWordsGPTTokensConsumption.seedFromJSONFile(user).size
            )
        )

        return result
    }
}