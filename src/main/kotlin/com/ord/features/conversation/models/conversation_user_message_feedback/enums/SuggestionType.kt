package com.ord.features.conversation.models.conversation_user_message_feedback.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class SuggestionType(
    val description: String,
    val example: String
) {
    IMPROVEMENT(
        description = "User's vocabulary/phrasing is inadequate for context or proficiency level. At C1/C2, basic vocabulary in formal contexts triggers this.",
        example = "\"very good\" → [\"excellent\", \"outstanding\"] - At C1 level, 'very good' is too basic for formal contexts"
    ),
    ENRICHMENT(
        description = "User's phrasing is fine, but here are interesting alternatives to expand their repertoire. For C1 learners, suggest C1-C2 level phrasings, not obvious B2 expressions.",
        example = "\"I went to the store\" → [\"I popped by the store\", \"I stopped by the store\"] - These phrasal verbs sound more natural in casual conversation"
    );

    companion object {
        fun toPromptDescription(): String = entries.joinToString("\n") {
            "- ${it.name}: ${it.description}\n  Example: ${it.example}"
        }
    }
}
