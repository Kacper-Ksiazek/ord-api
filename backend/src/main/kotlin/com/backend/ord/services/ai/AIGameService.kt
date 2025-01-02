package com.backend.ord.services.ai

import com.backend.ord.domain.persistence.embedded.game_instructions.CrosswordGameProperAnswers
import com.backend.ord.domain.persistence.entities.User
import com.backend.ord.domain.persistence.entities.gpt_tokens_usage.GameTokensUsage
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.services.ai.dto.AIGeneratedCrossword
import java.util.*

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
        data class GeneratedCrossWordGame(
            /**
             * The parsed crossword game received from calling an OpenAI endpoint
             */
            val aiGeneratedCrossword: AIGeneratedCrossword,
            /**
             * A set of logs representing a usage of games token
             */
            val gameTokensUsageLogs: Set<GameTokensUsage>,

            /**
             * A set of all ids of the words used in the game
             */
            val wordsUsedIds: Set<UUID>,

            /**
             * All expected answers to the crossword game
             */
            val properAnswers: CrosswordGameProperAnswers
        )
    }
}