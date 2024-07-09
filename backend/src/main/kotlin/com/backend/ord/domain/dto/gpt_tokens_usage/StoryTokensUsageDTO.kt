package com.backend.ord.domain.dto.gpt_tokens_usage

import com.backend.ord.domain.dto.StoryDTO
import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.domain.dto.abstracts.DTOBase
import com.backend.ord.enums.TokensUsage.StoriesGPTTokensConsumptionType

data class StoryTokensUsageDTO(
    var numberOfTokens: Int,
    var consumptionType: StoriesGPTTokensConsumptionType,

    val story: StoryDTO,
    val user: UserDTO
) : DTOBase()
