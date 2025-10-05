package com.ord.features.game.variants.shared.dto.api_requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.validators.annotations.ValidLanguageName
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty
import jakarta.validation.constraints.NotNull

data class StartGameRequest(
    @field:NotNull(message = "Difficulty cannot be null")
    val difficulty: GameDifficulty,

    @field:NotNull(message = "Language cannot be null")
    @field:ValidLanguageName
    val language: LanguageName
)

data class UnsafeStartGameRequestData(
    val difficulty: GameDifficulty? = null,
    val language: LanguageName? = null
)
