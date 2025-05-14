package com.backend.ord.api.responses.gpt_tokens_usage

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.enums.persistence.tokens_usage.WordsGPTTokensConsumptionType
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class DetailedWordTokensUsage(
    val id: UUID,

    val word: String,
    val cost: BigDecimal,
    val inputTokens: Int,
    val outputTokens: Int,
    val priceForMlnInputTokens: BigDecimal,
    val priceForMlnOutputTokens: BigDecimal,

    var translatedTo: LanguageName,
    var language: LanguageName,
    var consumptionType: WordsGPTTokensConsumptionType,

    val createdAt: Instant,
)

fun WordTokensUsage.toDetailedWordTokensUsage(): DetailedWordTokensUsage {
    return DetailedWordTokensUsage(
        id = id,

        word = word,
        cost = cost,
        inputTokens = inputTokens,
        outputTokens = outputTokens,
        priceForMlnInputTokens = priceForMlnInputTokens,
        priceForMlnOutputTokens = priceForMlnOutputTokens,

        translatedTo = translatedTo,
        language = language,
        consumptionType = consumptionType,

        createdAt = createdAt,
    )
}
