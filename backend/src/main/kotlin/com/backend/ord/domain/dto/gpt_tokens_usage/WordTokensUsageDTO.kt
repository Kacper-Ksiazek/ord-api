package com.backend.ord.domain.dto.gpt_tokens_usage

import com.backend.ord.domain.dto.abstracts.DTOBase
import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.domain.dto.WordDTO
import com.backend.ord.enums.TokensUsage.StoriesGPTTokensConsumptionType

data class WordTokensUsageDTO(
    var numberOfTokens: Int,
    var consumptionType: StoriesGPTTokensConsumptionType,

    val word: WordDTO,
    val user: UserDTO
) : DTOBase()
