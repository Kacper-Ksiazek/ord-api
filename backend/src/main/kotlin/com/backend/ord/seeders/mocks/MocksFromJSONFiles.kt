package com.backend.ord.seeders.mocks

import com.backend.ord.domain.entities.User
import com.backend.ord.seeders.mocks.words.MockWordsManuals
import com.backend.ord.seeders.mocks.words_gpt_tokens_usage.MockDetailedWordsGPTTokensConsumption
import org.springframework.stereotype.Component

data class MockedEntitySummary(
    /** The name of the entity */
    val name: String,

    /** The amount of created entities */
    val amount: Int
)

@Component
class MocksFromJSONFiles(
    private val mockWordsManuals: MockWordsManuals,
    private val mockDetailedWordsGPTTokensConsumption: MockDetailedWordsGPTTokensConsumption
) {
    fun run(user: User): List<MockedEntitySummary> {
        val result = mutableListOf<MockedEntitySummary>();

        result.add(
            MockedEntitySummary(
                name = "Words",
                amount = mockWordsManuals.seedFromJSONFile(user)
            )
        )

        result.add(MockedEntitySummary(
            name = "GPT Tokens Usage on Words",
            amount = mockDetailedWordsGPTTokensConsumption.seedFromJSONFile(user)
        ))

        return result;
    }
}