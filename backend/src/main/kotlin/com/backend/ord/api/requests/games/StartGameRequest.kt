package com.backend.ord.api.requests.games

import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName

data class StartGameRequest(
    val difficulty: GameDifficulty,
    val language: LanguageName
)