package com.backend.ord.prompts

import com.backend.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.features.game.ai.review.llm_api_requests.SentencesWritingMultipleTopicProperAnswerForAI
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty
import com.backend.ord.features.game.model.ongoing_game.extensions.getNumberOfWordsForCrossword
import com.backend.ord.features.game.model.ongoing_game.extensions.getNumberOfWordsForWordsTypingGame
import com.backend.ord.prompts.internal_tools.GenerateGamePromptData
import com.backend.ord.prompts.internal_tools.prepareGamePrompt

object Prompts {
    const val DEFAULT_CONTEXT =
        "Do not include anything more than this JSON and do not add markdown formatting. I want your output to be suitable for jsonObjectMapper.readValue."

    object Games {
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

        fun reviewSentencesWritingGamePrompt(
            language: LanguageName,
            difficulty: GameDifficulty,
            languageProficiency: LanguageProficiencyEntity,
            answers: Set<SentencesWritingMultipleTopicProperAnswerForAI>
        ): String {
            val serializedAnswers = answers
                .joinToString(separator = ", ") {
                    """
                        {
                        "word": "${it.word}",
                        "topic": "${it.topic}"
                        "answer": "${it.answer}"
                        }
                    """.trimIndent()
                }

            return """
                You are a vocabulary tutor. Review the following answers based on the following criteria:

                1. The sentence is sufficiently long to match the difficulty level of the game.
                2. The vocabulary used reflects the user's language proficiency.
                3. The length of the answer is appropriate, meaning it is neither too short nor excessively long.
                4. The requested word is used correctly. If it is not used at all, award 0 points.

                Be strict and provide a harsh, critical evaluation, adequately to both - user advance level and the game difficulty.
                
                The game difficulty is set to $difficulty, and the foreign language is $language at $languageProficiency level.

                Answers:
                [
                $serializedAnswers
                ]
                
                Your response must adhere to this TS format:
                
                interface ScoringCriteria {
                  score: number // value ranging [0-10] inclusive
                  comment?: string // If clarification is NOT needed, then do not add this field at all
                }

                interface ExpectedResult { 
                  word: string,
                  evaluation_criteria: {
                     "sentence_length_valid": ScoringCriteria,
                     "vocabulary_proficiency_suitable": ScoringCriteria,
                     "answer_size_valid": ScoringCriteria,
                     "word_usage_correct": ScoringCriteria  
                  },
                  suggested_correct_answer: string | null // If user answer is valid, then leave this as null
                }
            """.trimIndent()
        }
    }
}