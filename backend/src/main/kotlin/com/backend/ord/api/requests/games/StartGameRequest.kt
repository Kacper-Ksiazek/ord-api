package com.backend.ord.api.requests.games

import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName

interface StartGameRequest {
    val language: LanguageName
    val difficulty: GameDifficulty
}