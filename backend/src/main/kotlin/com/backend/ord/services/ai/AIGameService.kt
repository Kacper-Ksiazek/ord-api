package com.backend.ord.services.ai

import com.backend.ord.domain.entities.User
import com.backend.ord.domain.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.enums.persistance.game.GameDifficulty
import com.backend.ord.enums.persistance.language.LanguageName
import com.backend.ord.services.ai.dto.AIGeneratedCrossword

/**
 * Service for generating games via AI requests.
 */
interface AIGameService {
    /**
     * Generates a crossword game.
     *
     * @param user The user for whom the game is generated.
     * @param language The language of the game.
     * @param difficulty The difficulty of the game.
     *
     * @return The generated crossword game.
     */
    fun generateCrosswordGame(
        user: User,
        language: LanguageName,
        difficulty: GameDifficulty
    ): Pair<AIGeneratedCrossword, Set<GameTokensUsage>>
}