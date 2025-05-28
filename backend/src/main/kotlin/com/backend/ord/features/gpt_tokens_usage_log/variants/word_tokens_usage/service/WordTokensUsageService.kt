package com.backend.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.service

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.enums.persistence.tokens_usage.WordsGPTTokensConsumptionType
import com.backend.ord.features.gpt_tokens_usage_log.variants.shared.service.TokensUsageServiceBase
import com.backend.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.model.WordTokensUsage

interface WordTokensUsageService : TokensUsageServiceBase<WordTokensUsage, WordsGPTTokensConsumptionType> {
    fun save(
        user: UserEntity,
        word: String,
        translatedTo: LanguageName,
        translatedFrom: LanguageName,
        consumptionType: WordsGPTTokensConsumptionType,

        inputTokens: Int,
        outputTokens: Int,
    ): WordTokensUsage
}