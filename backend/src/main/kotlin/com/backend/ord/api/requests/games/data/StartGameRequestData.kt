package com.backend.ord.api.requests.games.data

import com.backend.ord.api.requests.games.StartGameRequest
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName

data class StartGameRequestData(
    override val difficulty: GameDifficulty,
    override val language: LanguageName
) : StartGameRequest

data class UnsafeStartGameRequestData(
    val difficulty: GameDifficulty?,
    val language: LanguageName?
)
