package com.backend.ord.controllers.unsafe_api_requests

import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName

data class UnsafeStartGameRequestData(
    val difficulty: GameDifficulty?,
    val language: LanguageName?
)

