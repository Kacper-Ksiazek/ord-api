package com.ord.features.conversation.models.conversation_user_message_feedback.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class ConversationMessageSuggestionType(
    val description: String,
    val example: String
) {
    IMPROVEMENT(
        description = "User's vocabulary/phrasing is inadequate for context or proficiency level. Offering alternatives would help them express themselves more effectively or appropriately.",
        example = "\"It's good\" → \"It's excellent/outstanding\" - provides stronger vocabulary for positive feedback"
    ),
    ENRICHMENT(
        description = "User's phrasing is fine, but here are interesting alternatives to expand repertoire and add variety.",
        example = "\"very happy\" → \"delighted/thrilled/overjoyed\" - teaches synonyms to add variety"
    ),
    CORRECTION(
        description = "The original phrase contains an error. Use this when suggesting a fix for a mistake.",
        example = "\"I am agree\" → \"I agree\" - corrects grammatical error"
    );

    companion object {
        fun toPromptDescription(): String = entries.joinToString("\n") {
            "- ${it.name}: ${it.description}\n  Example: ${it.example}"
        }
    }
}
