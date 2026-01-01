package com.ord.features.conversation.models.conversation_user_message_feedback.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class SuggestionType(val description: String) {
    IMPROVEMENT("User's vocabulary/phrasing is inadequate for context or proficiency level. At C1/C2, basic vocabulary in formal contexts triggers this."),
    ENRICHMENT("User's phrasing is fine, but here are interesting alternatives to expand their repertoire. For C1 learners, suggest C1-C2 level phrasings, not obvious B2 expressions.");

    companion object {
        fun toPromptDescription(): String = entries.joinToString("\n") {
            "- ${it.name}: ${it.description}"
        }
    }
}
