package com.ord.features.conversation.models.conversation_user_message_feedback.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class StrengthType(val description: String) {
    GRAMMAR("Correct use of grammatical structures, tenses, agreement, word order"),
    VOCABULARY("Good word choice, appropriate vocabulary for level, precise terminology"),
    FLUENCY("Natural flow, smooth expression, native-like phrasing"),
    PRAGMATICS("Appropriate register, politeness, cultural awareness"),
    COMMUNICATION("Clear message delivery, coherent response, engaging content");

    companion object {
        fun toPromptDescription(): String = entries.joinToString("\n") {
            "- ${it.name}: ${it.description}"
        }
    }
}