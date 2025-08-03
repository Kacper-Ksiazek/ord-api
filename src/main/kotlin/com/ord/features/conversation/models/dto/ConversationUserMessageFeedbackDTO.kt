package com.ord.features.conversation.models.dto

import java.time.Instant
import java.util.UUID

data class ConversationUserMessageFeedbackDTO(
    val id: UUID = UUID.randomUUID(),

    val grammar: Int,
    val vocabulary: Int,
    val answerLength: Int,
    val suggestedAnswer: String? = null,

    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
