package com.backend.ord.domain.persistence.dto.gpt_tokens_usage

import com.backend.ord.domain.persistence.dto.UserDTO
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.tokens_usage.WordsGPTTokensConsumptionType
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


    var translatedTo: LanguageName,
    var translatedFrom: LanguageName,
    var consumptionType: WordsGPTTokensConsumptionType,

    val user: UserDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
