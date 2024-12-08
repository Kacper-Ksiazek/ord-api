package com.backend.ord.services.ai

import com.backend.ord.domain.entities.LanguageProficiency
import com.backend.ord.enums.game.GameDifficulty
import com.backend.ord.enums.language.LanguageName
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import com.backend.ord.domain.entities.User

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
    ): AIGeneratedCrossword
}