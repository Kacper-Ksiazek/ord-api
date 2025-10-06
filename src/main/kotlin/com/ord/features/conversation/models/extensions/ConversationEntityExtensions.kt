package com.ord.features.conversation.models.extensions

import com.ord.features.conversation.models.dto.ConversationDTO

fun ConversationDTO.convertToPromptParams(): Map<String, String> {
    return mapOf(
        "language" to language.toString(),
        "level" to proficiencyLevel.toString(),
        "topic" to topic,
        "type" to type.toString(),
        "typeExplanation" to type.contextForAI,
        "additionalContext" to (additionalContext ?: "-")
    )
}