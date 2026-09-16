package com.ord.core.ai_provider_usage.models

/**
 * Stable operation keys for AI provider usage logging, organized by feature.
 */
object AiProviderUsageOperationType {

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
        const val FILL_GAPS = "WORDS_FILL_GAPS"
    }

    object Game {
        object Generate {
            const val SENTENCES_WRITING = "GAME_GENERATE_SENTENCES_WRITING"
            const val WORDS_TYPING = "GAME_GENERATE_WORDS_TYPING"
            const val CROSSWORD = "GAME_GENERATE_CROSSWORD"

            fun createKey(gameTypeName: String): String = "GAME_GENERATE_$gameTypeName"
        }

        object Review {
            const val SENTENCES_WRITING = "GAME_REVIEW_SENTENCES_WRITING"

            fun createKey(gameTypeName: String): String = "GAME_REVIEW_$gameTypeName"
        }
    }

    object Tts {
        const val SPEAK = "TTS_SPEAK"
    }
}
