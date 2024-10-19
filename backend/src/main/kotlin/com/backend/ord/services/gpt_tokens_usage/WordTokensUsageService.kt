package com.backend.ord.services.gpt_tokens_usage

import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.TokensUsage.WordsGPTTokensConsumptionType
import com.backend.ord.services.gpt_tokens_usage.bases.TokensUsageServiceBase

interface WordTokensUsageService: TokensUsageServiceBase<WordTokensUsage> {
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
