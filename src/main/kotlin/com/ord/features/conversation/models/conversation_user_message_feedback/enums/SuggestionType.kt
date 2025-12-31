package com.ord.features.conversation.models.conversation_user_message_feedback.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class SuggestionType {
    IMPROVEMENT,  // Vocabulary/phrasing is inadequate for context (especially at C1/C2)
    ENRICHMENT    // Interesting alternative phrasings to expand repertoire at user's level
}
