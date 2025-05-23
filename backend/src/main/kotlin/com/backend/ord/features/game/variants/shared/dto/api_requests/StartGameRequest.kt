package com.backend.ord.features.game.variants.shared.dto.api_requests

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.features.game.model.enums.GameDifficulty

data class StartGameRequest(
    val difficulty: GameDifficulty,
    val language: LanguageName
)

data class UnsafeStartGameRequestData(
    val difficulty: GameDifficulty? = null,
    val language: LanguageName? = null
)
