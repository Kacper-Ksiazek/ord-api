package com.backend.ord.seeders.mocks.words_gpt_tokens_usage

import com.backend.ord.api.responses.gpt_tokens_usage.DetailedWordTokensUsage
import com.backend.ord.api.responses.gpt_tokens_usage.TokensUsageWithinTimePeriod
import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.repositories.gpt_tokens_usage.WordTokensUsageRepository
import com.backend.ord.seeders.mocks.words.bases.MocksFromJsonFileHandler
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Component


@Component
class MockDetailedWordsGPTTokensConsumption(
    override val repository: WordTokensUsageRepository,
) : MocksFromJsonFileHandler<
        WordTokensUsage,
        TokensUsageWithinTimePeriod<DetailedWordTokensUsage>,
        DetailedWordTokensUsage
        > {
    override val pathToJSONFile: String =
        "gpt_tokens_usage/gpt_tokens_used_to_generate_ai_words_manuals.json"

    override fun convertToEntity(
        jsonData: DetailedWordTokensUsage,
        user: User
    ): WordTokensUsage {
        return WordTokensUsage(
            word = jsonData.word,
            cost = jsonData.cost,
            inputTokens = jsonData.inputTokens,
            outputTokens = jsonData.outputTokens,
            priceForMlnInputTokens = jsonData.priceForMlnInputTokens,
            priceForMlnOutputTokens = jsonData.priceForMlnOutputTokens,

            translatedTo = jsonData.translatedTo,
            translatedFrom = jsonData.translatedFrom,
            consumptionType = jsonData.consumptionType,

            user = user,
        )
    }

    override fun typeReference(): TypeReference<TokensUsageWithinTimePeriod<DetailedWordTokensUsage>> {
        return object : TypeReference<TokensUsageWithinTimePeriod<DetailedWordTokensUsage>>() {}
    }

    override fun parseFileContent(fileContent: TokensUsageWithinTimePeriod<DetailedWordTokensUsage>): List<DetailedWordTokensUsage> {
        return fileContent.data
    }
}
