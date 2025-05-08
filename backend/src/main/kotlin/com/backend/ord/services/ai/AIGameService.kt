package com.backend.ord.services.ai

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.services.ai.dto.GeneratedCrosswordGame
import com.backend.ord.services.ai.dto.GeneratedWordsTypingGame

interface AIGameService {
    fun generateCrosswordGame(
        user: User,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedCrosswordGame

    fun generateWordsTypingGame(
        user: User,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedWordsTypingGame
}