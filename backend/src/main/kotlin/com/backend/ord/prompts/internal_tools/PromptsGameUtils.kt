package com.backend.ord.prompts.internal_tools

import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.language.LanguageProficiencyLevel

internal data class GenerateGamePromptData(
    val language: LanguageName,
    val wordsToUse: List<String>,
    val difficulty: GameDifficulty,
    val languageProficiency: LanguageProficiencyLevel,
)

internal fun prepareGameGenerationPrompt(
    details: GenerateGamePromptData,
    gameTypeDescription: String,
    expectedResponseJSON: String
): String {
    return """
       $gameTypeDescription. The game difficulty is set to ${details.difficulty}, and the foreign language is ${details.language} at ${details.languageProficiency} level.
   
       I want my answer to match this JSON format:
       
       $expectedResponseJSON
       
       Words: [ 
       ${
        details.wordsToUse.mapIndexed { index, word ->
            " ${index + 1}. $word"
        }.joinToString("\n")
    } 
    ]
    
    Do not add any additional words to the list! All words in the list are actual words in the ${details.language} language do not correct them.
    """.trimIndent()
}
