package com.ord.features.conversation.models.conversation.extensions

import com.ord.features.conversation.models.conversation.ConversationDTO

fun ConversationDTO.convertToPromptParams(): Map<String, String> {
    return mapOf(
        "language" to language.toString(),
        "level" to proficiencyLevel.toString(),
        "topic" to topic,
        "type" to type.toString(),
        "typeExplanation" to type.contextForAI,
        "tone" to aiTone.toString(),
        "toneInstruction" to aiTone.instructionForAI,
        "additionalContext" to (additionalContext ?: "-")
    )
}