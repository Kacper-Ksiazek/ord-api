package com.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.service

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.gpt_tokens_usage_log.variants.shared.service.TokensUsageServiceBase
import com.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.model.WordTokensUsageEntity
import com.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.model.enums.WordsGPTTokensConsumptionType

interface WordTokensUsageService : TokensUsageServiceBase<WordTokensUsageEntity, WordsGPTTokensConsumptionType> {
    fun save(
        user: UserEntity,
        word: String,
        translatedTo: LanguageName,
        translatedFrom: LanguageName,
        consumptionType: WordsGPTTokensConsumptionType,

        inputTokens: Int,
        outputTokens: Int,
    ): WordTokensUsageEntity
}