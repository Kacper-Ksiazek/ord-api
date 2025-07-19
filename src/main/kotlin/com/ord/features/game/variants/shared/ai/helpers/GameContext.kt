package com.ord.features.game.variants.shared.ai.helpers

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import com.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.ord.features.game.model.ongoing_game.enums.GameDifficulty

/**
 * This DTO is used to pass the context of the game generation to the AI service.
 * It contains the most relevant information about the game parameters and is
 * passed to every abstract function used to generate the game.
 */
data class GameContext(
    val language: LanguageName,
    val difficulty: GameDifficulty,
    val amountOfQuestion: Int,
    val generativeContentLanguage: LanguageName,
    val userLanguageProficiency: LanguageProficiencyLevel,
    val words: List<String>
)
