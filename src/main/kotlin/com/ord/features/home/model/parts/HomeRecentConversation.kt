package com.ord.features.home.model.parts

import com.ord.features.conversation.models.conversation.enums.ConversationTone
import com.ord.features.conversation.models.conversation.enums.ConversationType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.UUID

@Schema(description = "Conversation preview on the home screen")
data class HomeRecentConversation(
    val id: UUID,
    val topic: String,
    val type: ConversationType,
    val aiTone: ConversationTone,
    val aiInterlocutorName: String,
    val aiInterlocutorAvatarId: String,

    @Schema(description = "Last update, used as the row date")
    val updatedAt: Instant,
)
