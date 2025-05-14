package com.backend.ord.services.ai

import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.services.ai.dto.generated_games.GeneratedCrosswordGame
import com.backend.ord.services.ai.dto.generated_games.GeneratedSentencesWritingGame
import com.backend.ord.services.ai.dto.generated_games.GeneratedWordsTypingGame

interface AIGameService {
    fun generateCrosswordGame(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedCrosswordGame

    fun generateWordsTypingGame(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedWordsTypingGame

    fun generateSentencesWritingGame(
        user: UserEntity,
        language: LanguageName,
        difficulty: GameDifficulty
    ): GeneratedSentencesWritingGame
}