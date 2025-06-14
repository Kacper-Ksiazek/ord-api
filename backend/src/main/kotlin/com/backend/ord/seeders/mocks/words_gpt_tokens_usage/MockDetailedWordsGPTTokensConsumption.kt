package com.backend.ord.seeders.mocks.words_gpt_tokens_usage

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.dto.api_responses.DetailedWordTokensUsage
import com.backend.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.model.WordTokensUsageEntity
import com.backend.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.repository.WordTokensUsageRepository
import com.backend.ord.seeders.mocks.bases.MocksFromJsonFileHandler
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.stereotype.Component

@Component
class MockDetailedWordsGPTTokensConsumption(
    override val repository: WordTokensUsageRepository,
) : MocksFromJsonFileHandler<
        WordTokensUsageEntity,
        List<DetailedWordTokensUsage>,
        DetailedWordTokensUsage
        > {
    override val pathToJsonFile: String =
        "mocks/gpt_tokens_usage/gpt_tokens_used_to_generate_ai_words_manuals.json"

    override fun convertToEntity(
        jsonData: DetailedWordTokensUsage,
        user: UserEntity
    ): WordTokensUsageEntity {
        return WordTokensUsageEntity(
            word = jsonData.word,
            cost = jsonData.cost,
            inputTokens = jsonData.inputTokens,
            outputTokens = jsonData.outputTokens,
            priceForMlnInputTokens = jsonData.priceForMlnInputTokens,
            priceForMlnOutputTokens = jsonData.priceForMlnOutputTokens,

            translatedTo = jsonData.translatedTo,
            language = jsonData.language,
            consumptionType = jsonData.consumptionType,

            user = user,
        )
    }

    override val jsonFileContentTypeRef: TypeReference<List<DetailedWordTokensUsage>> =
        object : TypeReference<List<DetailedWordTokensUsage>>() {}
}
