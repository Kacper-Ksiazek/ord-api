package com.backend.ord.domain.dto.gpt_tokens_usage

import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.enums.Language.LanguageName
import com.backend.ord.enums.TokensUsage.WordsGPTTokensConsumptionType
import java.time.Instant
import java.util.*

data class WordTokensUsageDTO(
    val id: UUID = UUID.randomUUID(),

    val word: String,
    var numberOfTokens: Int,
    var translatedTo: LanguageName,
    var translatedFrom: LanguageName,
    var consumptionType:WordsGPTTokensConsumptionType,

    val user: UserDTO,

    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)
