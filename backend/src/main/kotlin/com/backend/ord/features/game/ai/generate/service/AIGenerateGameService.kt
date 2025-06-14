package com.backend.ord.features.game.ai.generate.service

import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.features.game.ai.generate.dto.GeneratedCrosswordGame
import com.backend.ord.features.game.ai.generate.dto.GeneratedSentencesWritingGame
import com.backend.ord.features.game.ai.generate.dto.GeneratedWordsTypingGame
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty

interface AIGenerateGameService {
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