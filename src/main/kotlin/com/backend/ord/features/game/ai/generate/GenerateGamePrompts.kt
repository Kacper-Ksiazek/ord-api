package com.backend.ord.features.game.ai.generate

import com.backend.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageProficiencyLevel
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.backend.ord.features.game.model.ongoing_game.extensions.getNumberOfWordsForCrossword
import com.backend.ord.features.game.model.ongoing_game.extensions.getNumberOfWordsForWordsTypingGame

object GenerateGamePrompts {
    fun generateCrosswordQuestionsPrompt(
        language: LanguageName,
        wordsToUse: List<String>,
        difficulty: GameDifficulty,
        languageProficiency: LanguageProficiencyEntity,
    ): String {
        val details = GenerateGamePromptData(language, wordsToUse, difficulty, languageProficiency.proficiency)
        val amountOfQuestions: Int = difficulty.getNumberOfWordsForCrossword()

        return prepareGamePrompt(
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
               """
        )
    }

    fun generateWordsTypingGamePrompt(
        language: LanguageName,
        wordsToUse: List<String>,
        difficulty: GameDifficulty,
        languageProficiency: LanguageProficiencyEntity,
    ): String {
        val details = GenerateGamePromptData(language, wordsToUse, difficulty, languageProficiency.proficiency)
        val amountOfQuestions: Int = difficulty.getNumberOfWordsForWordsTypingGame()

        return prepareGamePrompt(
            details = details,
            gameTypeDescription = "Create a word typing game designed for practicing vocabulary in a foreign language",
            expectedResponseJSON = """
                   Map<string, string> where each key is a word from the provided list, 
                   and the corresponding value is a clue that describes the word without including the word itself. 
                   
                   The clues should be in ${languageProficiency.generativeContentLanguage}.

                   You will generate $amountOfQuestions such pairs.
                   
                   Output the result in the following format:

                   { 
                       'word1': 'Clue for word1 in the specified language', 
                       'word2': 'Clue for word2 in the specified language', 
                       ...
                   } 
               """
        )
    }

    fun generateSentencesWritingGamePrompt(
        language: LanguageName,
        wordsToUse: List<String>,
        difficulty: GameDifficulty,
        languageProficiency: LanguageProficiencyEntity,
    ): String {
        val details = GenerateGamePromptData(language, wordsToUse, difficulty, languageProficiency.proficiency)

        return prepareGamePrompt(
            details = details,
            gameTypeDescription = """
                    You are a writing tutor in a vocabulary learning app. Your task is to generate one topic for each word. 
                    
                    These topics should encourage users to write cohesive sentences that clearly demonstrate their understanding of each word. 
                    
                    Keep the topics casual and straightforward, allowing 
                    users to respond in just one or two sentences, rather than writing a full essay.
                """.trimIndent(),
            expectedResponseJSON = """
                    "Your answer must adhere to this structure: Map<String, String>, where the keys are words, and the values are their corresponding topics.
                """.trimIndent()
        )
    }

    private data class GenerateGamePromptData(
        val language: LanguageName,
        val wordsToUse: List<String>,
        val difficulty: GameDifficulty,
        val languageProficiency: LanguageProficiencyLevel,
    )

    private fun prepareGamePrompt(
        details: GenerateGamePromptData,
        gameTypeDescription: String,
        expectedResponseJSON: String,

        ): String {
        val serializedWords = details.wordsToUse.mapIndexed { index, word ->
            " ${index + 1}. $word"
        }.joinToString("\n")


        return """
       $gameTypeDescription. 
       
       The game difficulty is set to ${details.difficulty}, and the foreign language is ${details.language} at ${details.languageProficiency} level.
   
       Your response must adhere to this JSON format:
       
       $expectedResponseJSON
       
       Words: [ 
       $serializedWords 
       ]
    
       Do not add any additional words to the list! All words in the list are actual words in the ${details.language} language do not correct them.
    """.trimIndent()
    }

}