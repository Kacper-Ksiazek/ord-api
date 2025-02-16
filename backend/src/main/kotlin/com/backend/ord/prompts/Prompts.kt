package com.backend.ord.prompts

import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.language.LanguageProficiencyLevel

object Prompts {
    const val DEFAULT_CONTEXT =
        "Do not include anything more than this json and do not add markdown formatting. I want your output to be suitable for jsonObjectMapper.readValue."

    /**
     * Prepare a prompt to generate a list of questions for a crossword game.
     */
    fun generateCrosswordQuestionsPrompt(
        amountOfQuestions: Int,
        language: LanguageName,
        wordsToUse: List<String>,
        difficulty: GameDifficulty,
        languageProficiency: LanguageProficiencyLevel,
    ): String {
        return """
               Generate a foreign language practicing crossword. The game difficulty is set to $difficulty, and the foreign language is $language at $languageProficiency level.
               
               I want my answer to match this json format:
               
               {
                 answer: string // Either a new word or a short phrase. Do not use a word from the list provided
                 answerExplanation: string // DO NOT include an answer in its explanation
                 questions: {
                   word: string // Use words for the provided list. Each word can be used only once
                   clue: string // DO NOT include the word in its clue
                 }[] // A list of $amountOfQuestions with words from the provided list
               }
               
               Words: [ ${wordsToUse.joinToString(", ") { it }} ]
            """.trimIndent()
    }

    // TODO: Gather all prompts here
}