package com.ord.features.conversation.models.conversation_user_message_feedback.enums

enum class ErrorType {
    GRAMMAR_TENSE,
    GRAMMAR_AGREEMENT,
    GRAMMAR_WORD_ORDER,
    VOCABULARY_WRONG_WORD,
    VOCABULARY_UNNATURAL_COLLOCATION,
    SPELLING,
    PUNCTUATION,
    MISSING_WORD,
    UNNECESSARY_WORD,
    REGISTER_MISMATCH,
    COHERENCE_ISSUE  // Answer doesn't properly respond to the question or advance the conversation
}