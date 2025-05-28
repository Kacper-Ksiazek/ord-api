package com.backend.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.service.impl

import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.enums.persistence.tokens_usage.WordsGPTTokensConsumptionType
import com.backend.ord.features.gpt_tokens_usage_log.variants.shared.repository.TokensUsageRepository
import com.backend.ord.features.gpt_tokens_usage_log.variants.shared.service.impl.TokensUsageServiceBaseImpl
import com.backend.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.model.WordTokensUsage
import com.backend.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.service.WordTokensUsageService
import org.springframework.stereotype.Service

@Service
class WordTokensUsageServiceImpl(
    override val repository: TokensUsageRepository<WordTokensUsage, WordsGPTTokensConsumptionType>,
    override val openAIProperties: OpenAIProperties
) : WordTokensUsageService, TokensUsageServiceBaseImpl<WordTokensUsage, WordsGPTTokensConsumptionType>(
    repository = repository,
    openAIProperties = openAIProperties,
) {
    override fun save(
        user: UserEntity,
        word: String,
        translatedTo: LanguageName,
        translatedFrom: LanguageName,
        consumptionType: WordsGPTTokensConsumptionType,
        inputTokens: Int,
        outputTokens: Int
    ): WordTokensUsage {
        return repository.save(
            WordTokensUsage(
                user = user,
                word = word,
                translatedTo = translatedTo,
                language = translatedFrom,
                consumptionType = consumptionType,
                inputTokens = inputTokens,
                outputTokens = outputTokens,
                priceForMlnInputTokens = openAIProperties.pricePerMlnInputTokens,
                priceForMlnOutputTokens = openAIProperties.pricePerMlnOutputTokens,
                cost = computeCost(inputTokens, outputTokens)
            )
        )
    }
}