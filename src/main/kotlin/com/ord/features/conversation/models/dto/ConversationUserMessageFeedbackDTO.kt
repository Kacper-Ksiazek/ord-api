package com.ord.features.conversation.models.dto

import java.time.Instant
import java.util.UUID

data class ConversationUserMessageFeedbackDTO(
    val id: UUID = UUID.randomUUID(),

    val rating: Int,
    val comment: String? = null,
    val correctForm: String? = null,

    val messageId: UUID,

    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

