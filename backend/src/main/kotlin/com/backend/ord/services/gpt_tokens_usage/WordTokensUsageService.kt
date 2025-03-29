package com.backend.ord.services.gpt_tokens_usage

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.tokens_usage.WordsGPTTokensConsumptionType
import com.backend.ord.services.gpt_tokens_usage.bases.TokensUsageServiceBase

interface WordTokensUsageService : TokensUsageServiceBase<WordTokensUsage, WordsGPTTokensConsumptionType> {
    fun save(
        user: User,
        word: String,
        translatedTo: LanguageName,
        translatedFrom: LanguageName,
        consumptionType: WordsGPTTokensConsumptionType,

        inputTokens: Int,
        outputTokens: Int,
    ): WordTokensUsage
}
