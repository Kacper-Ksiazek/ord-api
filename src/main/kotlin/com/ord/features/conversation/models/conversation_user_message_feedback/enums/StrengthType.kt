package com.ord.features.conversation.models.conversation_user_message_feedback.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class StrengthType {
    GRAMMAR,        // Correct grammatical structures, tenses, agreement, word order
    VOCABULARY,     // Good word choice, appropriate vocabulary for level
    FLUENCY,        // Natural flow, smooth expression, native-like phrasing
    PRAGMATICS,     // Appropriate register, politeness, cultural awareness
    COMMUNICATION   // Clear message delivery, coherent response, engaging content
}
