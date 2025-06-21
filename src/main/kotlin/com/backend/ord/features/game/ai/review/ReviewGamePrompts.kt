package com.backend.ord.features.game.ai.review

import com.backend.ord.core.langugae_proficiency.model.LanguageProficiencyEntity
import com.backend.ord.core.langugae_proficiency.model.enums.LanguageName
import com.backend.ord.features.game.ai.review.llm_api_requests.SentencesWritingMultipleTopicProperAnswerForAI
import com.backend.ord.features.game.model.ongoing_game.enums.GameDifficulty

object ReviewGamePrompts {
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