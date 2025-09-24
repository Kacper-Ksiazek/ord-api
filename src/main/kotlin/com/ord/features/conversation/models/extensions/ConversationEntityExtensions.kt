package com.ord.features.conversation.models.extensions

import com.ord.features.conversation.models.dto.ConversationDTO

fun ConversationDTO.convertToPromptParams(): Map<String, String> {
    return mapOf(
        "language" to language.toString(),
        "level" to proficiencyLevel.toString(),
        "topic" to topic,
        "goal" to goal.toString(),
        "goalExplanation" to goal.contextForAI,
        "additionalContext" to (additionalContext ?: "-")
    )
}