package com.backend.ord.services.ai

import com.backend.ord.domain.persistence.embedded.game_proper_answers.CrosswordProperAnswers
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
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
    ): GeneratedCrossWordGame

    companion object {
        /**
         * Represents an AI generated crossword game with information about the involved words, tokens usage and proper answers.
         *
         * @property properAnswers The solution to the crossword game
         * @property aiGeneratedCrossword The parsed crossword game received from calling an OpenAI endpoint
         */
        data class GeneratedCrossWordGame(
            val properAnswers: CrosswordProperAnswers,
            val aiGeneratedCrossword: AIGeneratedCrossword,
        )
    }
}