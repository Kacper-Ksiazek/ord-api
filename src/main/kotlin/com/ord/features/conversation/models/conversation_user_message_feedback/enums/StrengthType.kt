package com.ord.features.conversation.models.conversation_user_message_feedback.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class StrengthType(
    val description: String,
    val example: String
) {
    GRAMMAR(
        description = "Correct use of grammatical structures, tenses, agreement, word order",
        example = "\"If I had known earlier\" → Excellent use of third conditional - shows mastery of complex hypothetical structures"
    ),
    VOCABULARY(
        description = "Good word choice, appropriate vocabulary for level, precise terminology",
        example = "\"breathtaking scenery\" → Sophisticated adjective choice that elevates the description"
    ),
    FLUENCY(
        description = "Natural flow, smooth expression, native-like phrasing",
        example = "\"I couldn't help but smile\" → Natural idiomatic expression that a native speaker would use"
    ),
    PRAGMATICS(
        description = "Appropriate register, politeness, cultural awareness",
        example = "\"Could you possibly help me?\" → Appropriate politeness level for formal request"
    ),
    COMMUNICATION(
        description = "Clear message delivery, coherent response, engaging content",
        example = "\"That reminds me of the time when...\" → Smooth topic transition that keeps the conversation engaging"
    );

    companion object {
        fun toPromptDescription(): String = entries.joinToString("\n") {
            "- ${it.name}: ${it.description}\n  Example: ${it.example}"
        }
    }
}