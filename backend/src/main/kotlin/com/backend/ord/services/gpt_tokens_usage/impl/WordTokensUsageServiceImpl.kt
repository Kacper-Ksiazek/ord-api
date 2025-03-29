package com.backend.ord.services.gpt_tokens_usage.impl

import com.backend.ord.config.properties.OpenAIProperties
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.tokens_usage.WordsGPTTokensConsumptionType
import com.backend.ord.repositories.gpt_tokens_usage.bases.GPTTokensUsageRepository
import com.backend.ord.services.gpt_tokens_usage.WordTokensUsageService
import com.backend.ord.services.gpt_tokens_usage.bases.impl.TokensUsageServiceBaseImpl
import org.springframework.stereotype.Service

@Service
class WordTokensUsageServiceImpl(
    override val repository: GPTTokensUsageRepository<WordTokensUsage, WordsGPTTokensConsumptionType>,
    override val openAIProperties: OpenAIProperties
) : WordTokensUsageService, TokensUsageServiceBaseImpl<WordTokensUsage, WordsGPTTokensConsumptionType>(
    repository = repository,
    openAIProperties = openAIProperties,
) {
    override fun save(
        user: User,
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
                translatedFrom = translatedFrom,
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