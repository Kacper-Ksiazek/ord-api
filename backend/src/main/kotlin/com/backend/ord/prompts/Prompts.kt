package com.backend.ord.prompts

import com.backend.ord.domain.persistence.entities.LanguageProficiency
import com.backend.ord.enums.persistence.game.GameDifficulty
import com.backend.ord.enums.persistence.game.getNumberOfWordsForCrossword
import com.backend.ord.enums.persistence.game.getNumberOfWordsForWordsTypingGame
import com.backend.ord.enums.persistence.language.LanguageName
import com.backend.ord.enums.persistence.language.LanguageProficiencyLevel
import com.backend.ord.enums.persistence.word.WordExtraMark
import com.backend.ord.enums.persistence.word.WordType
import com.backend.ord.prompts.internal_tools.GenerateGamePromptData
import com.backend.ord.prompts.internal_tools.prepareGameGenerationPrompt
import com.backend.ord.utils.EnumUtils.joinEnumValues

object Prompts {
    const val DEFAULT_CONTEXT =
        "Do not include anything more than this JSON and do not add markdown formatting. I want your output to be suitable for jsonObjectMapper.readValue."

    object Games {
        fun generateCrosswordQuestionsPrompt(
            language: LanguageName,
            wordsToUse: List<String>,
            difficulty: GameDifficulty,
            languageProficiency: LanguageProficiency,
        ): String {
            val details = GenerateGamePromptData(language, wordsToUse, difficulty, languageProficiency.proficiency)
            val amountOfQuestions: Int = difficulty.getNumberOfWordsForCrossword()

            return prepareGameGenerationPrompt(
                details = details,
                gameTypeDescription = "Generate a foreign language practicing crossword.",
                expectedResponseJSON = """
                {
                     answer: string // Either a new word or a short phrase. Do not use a word from the list provided
                     answerExplanation: string // DO NOT include an answer in its explanation. Generate this in the ${languageProficiency.generativeContentLanguage} language
                     questions: {
                       word: string // Use words for the provided list. Each word can be used only once
                       clue: string // DO NOT include the word in its clue. Generate this in the ${languageProficiency.generativeContentLanguage} language
                     }[] // A list of $amountOfQuestions with words from the provided list
               }
               """.trimIndent()
            )
        }

        fun generateWordsTypingGamePrompt(
            language: LanguageName,
            wordsToUse: List<String>,
            difficulty: GameDifficulty,
            languageProficiency: LanguageProficiency,
        ): String {
            val details = GenerateGamePromptData(language, wordsToUse, difficulty, languageProficiency.proficiency)
            val amountOfQuestions: Int = difficulty.getNumberOfWordsForWordsTypingGame()

            return prepareGameGenerationPrompt(
                details = details,
                gameTypeDescription = "Generate a foreign language practicing words typing game.",
                expectedResponseJSON = """
                   Map<string, string> // Map of words and their clues
                   
                   For this map:
                   - key (word) - Use words for the provided list. Each word can be used only once
                   - value (clue) -DO NOT include the word in its clue. Generate this in the ${languageProficiency.generativeContentLanguage} language
                   
                   A list of $amountOfQuestions with words from the provided list
               """.trimIndent()
            )
        }
    }

    object AIWords {

        fun generateWordManualPrompt(
            word: String,
            wordLanguage: LanguageName,
            desiredLanguage: LanguageName,
            proficiency: LanguageProficiencyLevel,
            generativeContentLanguage: LanguageName,
        ): String {
            return """
                Response as a foreign language tutor. Generate a manual entry for $wordLanguage "$word" at $proficiency proficiency level in $desiredLanguage language. 
                Explain always the most common usage of the word, do not provide any rare or outdated meanings.

                Generate response in JSON format matching following typescript interface:

                type response = {
                translation: string, // Translation of the word in $desiredLanguage translateTo. If the word is an idiom or a phrase, provide a translation that is as close as possible to the original meaning, do not translate it literally.
                definition: string, // One or two short and concise sentences in $generativeContentLanguage
                type: ${WordType::class.joinEnumValues(separator = " | ")},
                extraMark: null | ${WordExtraMark::class.joinEnumValues(separator = " | ")}, // Leave null if none of the options are good enough
                useCases: string[], // If word has multiple definitions, provide multiple use cases in $generativeContentLanguage
                exampleSentences: {
                	sentence: string, // Sentence in $wordLanguage
                	translation: string // Sentence in $desiredLanguage
                }[] // At least 3 examples. In both languages, the word and its translation should be surrounded with single asterisks.
                }

                Additionally, return exactly:
                    - WORD_MISSPELLED if the word is misspelled
                    - NON_EXISTENT_WORD if the word does not exist in the language
            """.trimIndent()
        }

    }
}