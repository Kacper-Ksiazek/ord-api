package com.backend.ord.api.requests.games

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.enums.persistence.game.GameDifficulty

data class StartGameRequest(
    val difficulty: GameDifficulty,
    val language: LanguageName
)

data class UnsafeStartGameRequestData(
    val difficulty: GameDifficulty? = null,
    val language: LanguageName? = null
)
