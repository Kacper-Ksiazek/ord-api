package com.ord.features.conversation.models.conversation_user_message_feedback.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class ErrorType {
    GRAMMAR,
    VOCABULARY,
    SPELLING,
    PUNCTUATION,
    REGISTER  // Formality level mismatch for the context
}