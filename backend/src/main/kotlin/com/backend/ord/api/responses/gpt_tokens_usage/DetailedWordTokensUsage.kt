package com.backend.ord.api.responses.gpt_tokens_usage

import com.backend.ord.domain.entities.gpt_tokens_usage.WordTokensUsage
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.TokensUsage.WordsGPTTokensConsumptionType
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class DetailedWordTokensUsage(
    val id: UUID,

    val word: String,
    val cost: BigDecimal,
    val inputTokens: Int,
    val outputTokens: Int,
    val priceForMlnInputTokens: BigDecimal,
    val priceForMlnOutputTokens: BigDecimal,

    var translatedTo: LanguageName,
    var translatedFrom: LanguageName,
    var consumptionType: WordsGPTTokensConsumptionType,

    val createdAt: Instant = Instant.now(),
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
        translatedFrom = translatedFrom,
        consumptionType = consumptionType,

        createdAt = createdAt,
    )
}
