package com.backend.ord.services.ai

import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.services.ai.dto.generated_games.GeneratedCrosswordGame
import com.backend.ord.services.ai.dto.generated_games.GeneratedSentencesWritingGame
import com.backend.ord.services.ai.dto.generated_games.GeneratedWordsTypingGame

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

    fun generateSentencesWritingGame(
        user: User,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedSentencesWritingGame
}