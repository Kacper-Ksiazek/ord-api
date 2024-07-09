package com.backend.ord.domain.dto.gpt_tokens_usage

import com.backend.ord.domain.dto.GameDTO
import com.backend.ord.domain.dto.UserDTO
import com.backend.ord.domain.dto.abstracts.DTOBase
import com.backend.ord.enums.TokensUsage.GamesGPTTokensConsumptionType

data class GameTokensUsageDTO(
    var numberOfTokens: Int,
    var consumptionType: GamesGPTTokensConsumptionType,

    val user: UserDTO,
    val games: GameDTO
) : DTOBase()
