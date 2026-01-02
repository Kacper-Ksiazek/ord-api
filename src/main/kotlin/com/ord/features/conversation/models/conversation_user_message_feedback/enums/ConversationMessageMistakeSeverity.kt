package com.ord.features.conversation.models.conversation_user_message_feedback.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class ConversationMessageMistakeSeverity(
    val description: String,
    val example: String
) {
    MINOR(
        description = "Technically incorrect but meaning is clear, or unnatural but acceptable. Does not impede communication.",
        example = "\"I saw couple of deer\" (missing article 'a') → Meaning is clear despite missing article"
    ),
    MODERATE(
        description = "Noticeable error that reduces fluency or sounds unnatural. May cause brief confusion.",
        example = "\"The weather were perfect\" (subject-verb disagreement) → Grammatically incorrect and reduces fluency"
    ),
    CRITICAL(
        description = "Impedes communication or would confuse a native speaker. Significantly affects understanding.",
        example = "\"I'm going to started\" (wrong verb form) → Creates confusion about tense and intent"
    );

    companion object {
        fun toPromptDescription(): String = entries.joinToString("\n") {
            "- ${it.name}: ${it.description}\n  Example: ${it.example}"
        }
    }
}
