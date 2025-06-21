package com.ord.features.game.ai.generate.service

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.user.model.UserEntity
import com.ord.features.game.ai.generate.dto.GeneratedCrosswordGame
import com.ord.features.game.ai.generate.dto.GeneratedSentencesWritingGame
import com.ord.features.game.ai.generate.dto.GeneratedWordsTypingGame
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty

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