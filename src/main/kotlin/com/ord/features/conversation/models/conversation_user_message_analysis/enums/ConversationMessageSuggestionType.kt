package com.ord.features.conversation.models.conversation_user_message_analysis.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class ConversationMessageSuggestionType(
    val description: String,
    val example: String
) {
    VOCABULARY(
        description = "Alternative word choices for learning and improvement. Offer better, more precise, or more sophisticated vocabulary when the original phrasing is acceptable but could be enhanced. Point the user toward richer vocabulary that matches their proficiency level.",
        example = "\"interesting\" → \"eventful/memorable/noteworthy\" - offers vocabulary alternatives to expand repertoire and add variety"
    ),
    STRUCTURE(
        description = "Alternative sentence structures or phrasing patterns for learning. Suggest different ways to structure phrases that sound more natural, conversational, or sophisticated. NOT for fixing errors - use this to teach better ways to express correctly-formed sentences.",
        example = "\"Did you do anything interesting?\" → \"What else did you get up to?\" - teaches a more natural, conversational phrasing pattern"
    );

    companion object {
        fun toPromptDescription(): String = entries.joinToString("\n") {
            "- ${it.name}: ${it.description}\n  Example: ${it.example}"
        }
    }
}
