package com.ord.features.game.variants.shared.dto.api_requests

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty

data class StartGameRequest(
    val difficulty: GameDifficulty,
    val language: LanguageName
)

data class UnsafeStartGameRequestData(
    val difficulty: GameDifficulty? = null,
    val language: LanguageName? = null
)
