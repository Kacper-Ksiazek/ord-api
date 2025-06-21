package com.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserDTO
import com.ord.features.gpt_tokens_usage_log.variants.word_tokens_usage.model.enums.WordsGPTTokensConsumptionType
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class WordTokensUsageDTO(
    val id: UUID = UUID.randomUUID(),

    val word: String,
    val cost: BigDecimal,
    val inputTokens: Int,
    val outputTokens: Int,
    val priceForMlnInputTokens: BigDecimal,
    val priceForMlnOutputTokens: BigDecimal,


    var language: LanguageName,
    var translatedTo: LanguageName,
    var consumptionType: WordsGPTTokensConsumptionType,

    val user: UserDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
