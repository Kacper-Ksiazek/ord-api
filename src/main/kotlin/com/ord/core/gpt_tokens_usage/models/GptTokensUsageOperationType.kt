package com.ord.core.gpt_tokens_usage.models

/**
 * Object containing all GPT token usage operation types, organized by feature.
 * Used for categorizing and tracking different AI operations that consume tokens.
 */
object GptTokensUsageOperationType {

    object Conversation {
        const val INITIALIZE = "CONVERSATION_INITIALIZE"
        const val AI_RESPONSE = "CONVERSATION_AI_RESPONSE"
        const val REVIEW_USER_MESSAGE = "CONVERSATION_REVIEW_USER_MESSAGE"
        const val GENERATE_AI_MESSAGE_LEARNING_TIPS = "CONVERSATION_GENERATE_AI_MESSAGE_LEARNING_TIPS"
        const val SUGGEST_TOPICS = "CONVERSATION_SUGGEST_TOPICS"
        const val GENERATE_INTERLOCUTOR = "CONVERSATION_GENERATE_INTERLOCUTOR"
    }

    object AIExplainer {
        const val EXPLAIN_PHRASE = "AI_EXPLAINER_EXPLAIN_PHRASE"
    }

    object Words {
        const val GENERATE_MANUAL = "WORDS_GENERATE_MANUAL"
        const val SUGGEST_VOCABULARY = "WORDS_SUGGEST_VOCABULARY"
    }

    object Game {
        object Generate {
            const val SENTENCES_WRITING = "GAME_GENERATE_SENTENCES_WRITING"
            const val WORDS_TYPING = "GAME_GENERATE_WORDS_TYPING"
            const val CROSSWORD = "GAME_GENERATE_CROSSWORD"

            /**
             * Creates a game generation operation type from the game type name.
             * Example: "SENTENCES_WRITING" -> "GAME_GENERATE_SENTENCES_WRITING"
             */
            fun createKey(gameTypeName: String): String {
                return "GAME_GENERATE_$gameTypeName"
            }
        }

        object Review {
            const val SENTENCES_WRITING = "GAME_REVIEW_SENTENCES_WRITING"

            /**
             * Creates a game review operation type from the game type name.
             * Example: "SENTENCES_WRITING" -> "GAME_REVIEW_SENTENCES_WRITING"
             */
            fun createKey(gameTypeName: String): String {
                return "GAME_REVIEW_$gameTypeName"
            }
        }
    }
}
